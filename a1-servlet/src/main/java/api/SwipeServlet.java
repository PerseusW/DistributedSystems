package api;

import com.google.gson.Gson;
import model.ResponseMsg;
import model.SwipeDetails;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author Maidi Wang
 * This is the required servlet that implements the POST method for /swipe/{leftorright}/.
 */
@WebServlet(urlPatterns = "/swipe/*")
public class SwipeServlet extends HttpServlet {
    // Valid paths that might occur.
    private static final Set<String> VALID_PATHS = Set.of("/left", "/left/", "/right", "/right/");
    // Serialize to writer and from reader.
    private static final Gson GSON = new Gson();

    /**
     * Only take care of POST requests.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!VALID_PATHS.contains(request.getPathInfo())) {
            response.setStatus(400);
            GSON.toJson(new ResponseMsg("URL sub-path must be left or right"), response.getWriter());
            return;
        }

        try {
            SwipeDetails details = GSON.fromJson(request.getReader(), SwipeDetails.class);
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

        // Do nothing with the parsed json for now.
        response.setStatus(201);
    }
}
