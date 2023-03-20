package api;

import com.google.gson.Gson;
import model.GsonSingleton;
import model.ResponseMsg;
import model.SwipeDetails;
import service.RabbitMQ;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Maidi Wang
 * This is the required servlet that implements the POST method for /swipe/{leftorright}/.
 */
@WebServlet(urlPatterns = "/swipe/*")
public class SwipeServlet extends HttpServlet {
    // Valid paths that might occur.
    private static final Map<String, SwipeDetails.Swipe> VALID_PATHS = Map.of(
            "/left", SwipeDetails.Swipe.left,
            "/left/", SwipeDetails.Swipe.left,
            "/right", SwipeDetails.Swipe.right,
            "/right/", SwipeDetails.Swipe.right);

    private static final Gson GSON = GsonSingleton.getInstance();

    private static final RabbitMQ QUEUE = new RabbitMQ(4);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!VALID_PATHS.containsKey(request.getPathInfo())) {
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("URL sub-path must be left or right"), response.getWriter());
            return;
        }

        SwipeDetails details = null;
        try {
            details = GSON.fromJson(request.getReader(), SwipeDetails.class);
            if (details == null) {
                response.setStatus(400);
                GSON.toJson(new ResponseMsg("Json body cannot be null"), response.getWriter());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("Problem occurred while parsing json body"), response.getWriter());
            return;
        }
        details.setSwipe(VALID_PATHS.get(request.getPathInfo()));

        try {
            QUEUE.postMessage(details);
        } catch (InterruptedException e) {
            e.printStackTrace();
            response.setStatus(500);
            GSON.toJson(new ResponseMsg("RabbitMQ service down"), response.getWriter());
        }
        response.setStatus(201);
    }
}
