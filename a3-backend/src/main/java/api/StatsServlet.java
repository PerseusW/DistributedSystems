package api;

import com.google.gson.Gson;
import database.SwipesTable;
import model.GsonSingleton;
import model.MatchStats;
import model.Matches;
import model.ResponseMsg;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/stats/*")
public class StatsServlet extends HttpServlet {

    private static final Gson GSON = GsonSingleton.getInstance();

    private static final SwipesTable TABLE;

    static {
        try {
            TABLE = new SwipesTable(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This should end up with ["", userId].
        String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length != 2) {
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("Unrecognized path, please only provide path as /{userId}/"), response.getWriter());
            return;
        }

        String userId = pathInfo[1];
        MatchStats stats = null;
        try {
            stats = TABLE.getMatchStatsFor(userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (stats == null) {
            response.setStatus(500);
            GSON.toJson(new ResponseMsg("Unable to retrieve stats for user"), response.getWriter());
            return;
        }

        response.setStatus(200);
        GSON.toJson(stats, response.getWriter());
    }
}
