import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.SwipeInfo;
import rmqutility.RMQChannelFactory;
import rmqutility.RMQChannelPool;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet(name = "SwipeServlet", value = "/SwipeServlet")
public class SwipeServlet extends HttpServlet {
    private RMQChannelPool channelPool;
    private final int POOL_SIZE = 100;
    private final static String EXCHANGE_NAME = "swipeinfo";
    private final static String host = "35.160.124.120";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setUsername("admin");
            factory.setPassword("password");
            final Connection conn = factory.newConnection();
            RMQChannelFactory channelFactory = new RMQChannelFactory(conn);
            channelPool = new RMQChannelPool(POOL_SIZE, channelFactory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // check we have a URL!
        try {
            res.setContentType("text/plain");
            String urlPath = req.getPathInfo();
            if (urlPath == null || urlPath.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("missing parameters " + urlPath);
                return;
            }

            String[] urlParts = urlPath.split("/");
            // and now validate url path and return the response status code
            // (and maybe also some value if input is valid)
            String postData = req.getReader().lines().collect(Collectors.joining());
            SwipeInfo info = getSwipeInfo(postData);
            if (!isUrlPostValid(urlParts)) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write(urlPath + " url incorrect");
            } else if (info == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write(postData + " post data wrong format");
            } else {
                res.setStatus(HttpServletResponse.SC_CREATED);
                info.setLeftOrRight(urlParts[1]);
                // do any sophisticated processing with urlParts which contains all the url params
                sendMessageToQueue(info);
                res.getWriter().write("It works!");
            }
        } catch (Exception e) {
            try {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Exception occured" + e.getMessage());
            } catch (Exception ex) {

            }
        }
    }

    private SwipeInfo getSwipeInfo(String postData) {
        try {
            JsonObject post = new Gson().fromJson(postData, JsonObject.class);
            int swiperId = Integer.parseInt(post.get("swiper").getAsString());
            int swipeeId = Integer.parseInt(post.get("swipee").getAsString());
            String comment = post.get("comment").getAsString();
            if (post.keySet().size() != 3 || swiperId < 1 || swiperId > 5000
                    || swipeeId < 1 || swipeeId > 1000000 || comment.length() > 256) {
                return null;
            }
            return new SwipeInfo(String.valueOf(swiperId), String.valueOf(swipeeId), comment);
        } catch (Exception e) {
            return null;
        }
    }
    private boolean isUrlPostValid(String[] urlPath) {
        if (!urlPath[1].equals("left") && !urlPath[1].equals("right")) {
            return false;
        }
        return true;
    }

    private void sendMessageToQueue(SwipeInfo info) {
        Channel channel = null;
        try {
            channel = channelPool.borrowObject();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String message = "LeftOrRight: " + info.getLeftOrRight() + " SwiperId: " + info.getSwiper() + " SwipeeId: "+
                    info.getSwipee() + " comment: " + info.getComment();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            channelPool.returnObject(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
