package consumer;

import com.google.gson.Gson;
import database.SwipesTable;
import model.GsonSingleton;
import model.SwipeDetails;
import service.RabbitMQ;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Consumer registers itself to rabbitmq to handle queued messages.
 * Messages are handled by taking it off the queue and committing it into a database.
 */
public class Consumer {
    // Input source.
    private static final RabbitMQ QUEUE = new RabbitMQ(1);
    // Output destination.
    private static final SwipesTable TABLE;

    static {
        try {
            TABLE = new SwipesTable(32);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Gson GSON = GsonSingleton.getInstance();

    /**
     * Register callback to RabbitMQ that deserializes SwipeDetails and submits write request to database.
     */
    public static void main(String[] args) {
        System.out.println("Remote queue and database connection are setup");
        QUEUE.handleMessage((s, delivery) -> {
            SwipeDetails swipe = GSON.fromJson(new String(delivery.getBody(), StandardCharsets.UTF_8), SwipeDetails.class);
            TABLE.submitSwipe(swipe);
        });
    }
}
