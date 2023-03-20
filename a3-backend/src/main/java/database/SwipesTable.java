package database;

import com.google.gson.Gson;
import com.mysql.cj.jdbc.MysqlDataSource;
import model.GsonSingleton;
import model.MatchStats;
import model.Matches;
import model.SwipeDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Essentially a data access object.
 */
public class SwipesTable implements Table {
    // Connection pool and thread pool.
    private final int poolSize;
    private final BlockingQueue<Connection> connections;
    private ThreadPoolExecutor threadPool = null;

    // Statements;
    private static final String CLEAR = "DELETE FROM swipes";
    private static final String INSERT = "INSERT INTO swipes(swiper, swipee, comment, swipe) VALUES(?,?,?,?)";
    private static final String SELECT_MATCHES = "SELECT likes.swipee FROM swipes AS likes INNER JOIN swipes AS liked ON likes.swipee = liked.swiper AND likes.swiper = liked.swipee WHERE likes.swiper = ? LIMIT 100";
    private static final String SELECT_STATS = "SELECT SUM(CASE WHEN swipe = 'right' THEN 1 ELSE 0 END) AS likes, SUM(CASE WHEN swipe = 'left' THEN 1 ELSE 0 END) AS dislikes FROM swipes WHERE swipee = ?";

    /**
     * Initialize object with given poolSize of connections and threads.
     * Simplifying the problem by giving 1 thread 1 connection.
     *
     * @param poolSize
     * @throws SQLException
     */
    public SwipesTable(int poolSize) throws SQLException {
        // Sources and connections.
        MysqlDataSource source = new MysqlDataSource();
        source.setURL(URL);
        source.setUser(USERNAME);
        source.setPassword(PASSWORD);

        // Connection pool and thread pool.
        this.poolSize = poolSize;
        connections = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connections.add(source.getConnection());
        }
    }

    /**
     * Util function for truncating table.
     *
     * @throws InterruptedException
     */
    public void clearTable() throws InterruptedException {
        Connection connection = connections.take();
        try (PreparedStatement statement = connection.prepareStatement(CLEAR)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connections.add(connection);
    }

    /**
     * Async submitting a SwipeDetails to commit to the database.
     *
     * @param swipe
     */
    public void submitSwipe(SwipeDetails swipe) {
        if (threadPool == null) {
            threadPool = new ThreadPoolExecutor(poolSize, poolSize, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        }
        threadPool.execute(() -> {
            Connection connection;
            try {
                connection = connections.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setString(1, swipe.getSwiper());
                statement.setString(2, swipe.getSwipee());
                statement.setString(3, swipe.getComment());
                statement.setString(4, swipe.getSwipe().toString());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connections.add(connection);
        });
    }

    /**
     * Explicity blocks execution until all writes have been committed.
     *
     * @throws InterruptedException
     */
    public void waitForWrites() throws InterruptedException {
        // Stop accepting new tasks.
        threadPool.shutdown();
        if (!threadPool.awaitTermination(1, TimeUnit.MINUTES)) {
            threadPool.shutdownNow();
        }
        threadPool = null;
    }

    /**
     * Matches are defined as when 2 people both right swipe each other.
     * Order of returned list is not defined.
     *
     * @param userId
     * @return
     * @throws InterruptedException
     */
    public Matches getMatchesFor(String userId) throws InterruptedException {
        Connection connection = connections.take();
        Matches matches = new Matches();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_MATCHES)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                matches.addMatch(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connections.add(connection);
        return matches;
    }

    /**
     * Queries "left" and "right" count given a userId.
     *
     * @param userId
     * @return
     * @throws InterruptedException
     */
    public MatchStats getMatchStatsFor(String userId) throws InterruptedException {
        Connection connection = connections.take();
        MatchStats stats = new MatchStats(0, 0);
        try (PreparedStatement statement = connection.prepareStatement(SELECT_STATS)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stats = new MatchStats(resultSet.getInt(1), resultSet.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connections.add(connection);
        return stats;
    }

    /**
     * Local main function used for testing best number of threads/connections.
     *
     * @param args
     * @throws SQLException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        SwipesTable table = new SwipesTable(32);
        table.clearTable();
        long start = System.nanoTime();
        for (int i = 0; i < 200; ++i) {
            for (int j = 0; j < 200; ++j) {
                table.submitSwipe(new SwipeDetails(String.valueOf(i), String.valueOf(j), "", SwipeDetails.Swipe.right));
            }
        }
        table.waitForWrites();
        long nanos = System.nanoTime() - start;

        System.out.println(40000d * 1000000000 / nanos);

        Gson gson = GsonSingleton.getInstance();

        for (int i = 0; i < 3; ++i) {
            Matches matches = table.getMatchesFor("0");
            System.out.println(gson.toJson(matches));

            MatchStats matchStats = table.getMatchStatsFor("0");
            System.out.println(gson.toJson(matchStats));
        }
    }
}
