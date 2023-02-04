import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters " + urlPath);
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)
        String postData = req.getReader().lines().collect(Collectors.joining());
        if (!isUrlPostValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write(urlPath + " url incorrect");
        }
        else if (!isPostDataValid(postData)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(postData + " post data wrong format");
        }
        else {
            res.setStatus(HttpServletResponse.SC_CREATED);
            // do any sophisticated processing with urlParts which contains all the url params
            res.getWriter().write("It works!");
        }
    }

    private boolean isPostDataValid(String raw) {
        try {
            JsonObject post = new Gson().fromJson(raw, JsonObject.class);
            int swiperId = Integer.parseInt(post.get("swiper").getAsString());
            int swipeeId = Integer.parseInt(post.get("swipee").getAsString());
            String comment = post.get("comment").getAsString();
            if (post.keySet().size() != 3 || swiperId < 1 || swiperId > 5000
            || swipeeId < 1 || swipeeId > 1000000 || comment.length() != 256) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private boolean isUrlPostValid(String[] urlPath) {
        if (!urlPath[1].equals("left") && !urlPath[1].equals("right")) {
            return false;
        }
        return true;
    }

}
