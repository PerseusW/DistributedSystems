package api;

import com.google.gson.Gson;
import database.SwipesTable;
import model.GsonSingleton;
import model.Matches;
import model.ResponseMsg;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/matches/*")
public class MatchServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // This should end up with ["", userId].
        String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length != 2) {
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("Unrecognized path, please only provide path as /{userId}/"), response.getWriter());
            return;
        }

        String userId = pathInfo[1];
        Matches matches = null;
        try {
            matches = TABLE.getMatchesFor(userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (matches == null) {
            response.setStatus(500);
            GSON.toJson(new ResponseMsg("Unable to retrieve matches for user"), response.getWriter());
            return;
        }

        response.setStatus(200);
        GSON.toJson(matches, response.getWriter());
    }
}
