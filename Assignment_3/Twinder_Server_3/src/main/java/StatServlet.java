import com.google.gson.Gson;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "StatServlet", value = "/StatServlet")
public class StatServlet extends HttpServlet {
    private final static String RADIS_HOSOT = "54.214.101.75";
    private JedisPool pool;
    @Override
    public void init() throws ServletException {
        super.init();
        this.pool = new JedisPool(RADIS_HOSOT, 6379);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        try {
            res.setContentType("text/plain");
            String urlPath = req.getPathInfo();
            String[] urlParts = urlPath.split("/");
            if (!MatchesServlet.validateUserId(urlParts)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Invalid Inputs: " + urlPath);
                return;
            }

            try (Jedis jedis = pool.getResource()) {
                Map<String, String> count = jedis.hgetAll(urlParts[1]);
                if (count.size() == 0) {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    res.getWriter().write("User Not Found: " + urlPath);
                }
                else {
                    String left = count.get("left");
                    String right = count.get("right");
                    Gson gson = new Gson();
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("numLlikes", left);
                    jsonObject.addProperty("numDislikes", right);
                    String jsonString = gson.toJson(jsonObject);

                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(jsonString);
                }

            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                res.getWriter().write("Exception occured" + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
