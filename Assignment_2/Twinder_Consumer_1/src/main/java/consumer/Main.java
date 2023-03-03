package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    private final static int THREAD_NUM = 100;
    private final static String host = "35.160.124.120";
    /**
     * connect to rabbitmq and run thraeds.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setUsername("admin");
            factory.setPassword("password");
            final Connection conn = factory.newConnection();
            SwipeCount sc = new SwipeCount();
            for (int i = 0; i < THREAD_NUM; i++) {
                new Thread(new ConsumerThread(conn, sc)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}