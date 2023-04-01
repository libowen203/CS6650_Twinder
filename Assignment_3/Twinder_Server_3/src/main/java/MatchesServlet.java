import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {
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
            System.out.println(urlPath);
            String[] urlParts = urlPath.split("/");
            if (!validateUserId(urlParts)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Invalid Inputs: " + urlPath);
                return;
            }
            System.out.println(urlParts[1]);
            try (Jedis jedis = pool.getResource()) {
                String key = urlParts[1] + "-pMatches";
                List<String> matches = jedis.lrange(key,0, 99);
                if (matches.size() == 0) {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    res.getWriter().write("User Not Found: " + urlPath);
                }
                else {
                    Gson gson = new Gson();
                    JsonObject jsonObject = new JsonObject();
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < matches.size(); i++) {
                        jsonArray.add(matches.get(i));
                    }
                    // Add the array to the object
                    jsonObject.add("matchList", jsonArray);
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
                res.getWriter().write("Exception occured" + e.getMessage() + " \n" + e.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){

    }

    public static boolean validateUserId(String[] urlParts) {
        if (urlParts.length != 2) {
            return false;
        }
        try {
            int id = Integer.parseInt(urlParts[1]);
            if (id < 1 || id > 5000) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
