package api;

import com.google.gson.Gson;
import model.ResponseMsg;
import model.SwipeDetails;
import service.RabbitMQ;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author Maidi Wang
 * This is the required servlet that implements the POST method for /swipe/{leftorright}/.
 */
@WebServlet(urlPatterns = "/swipe/*")
public class SwipeServlet extends HttpServlet {
    // Valid paths that might occur.
    private static final Map<String, Boolean> VALID_PATHS = Map.of(
            "/left", true,
            "/left/", true,
            "/right", false,
            "/right/", false);
    // Serialize to writer and from reader.
    private static final Gson GSON = new Gson();

    private static final RabbitMQ QUEUE = new RabbitMQ(4);

    /**
     * Only take care of POST requests.
     */
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
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("Problem occurred while parsing json body"), response.getWriter());
            e.printStackTrace();
            return;
        }
        details.left = VALID_PATHS.get(request.getPathInfo());

        QUEUE.postMessage(details);

        // Do nothing with the parsed json for now.
        response.setStatus(201);
    }
}
