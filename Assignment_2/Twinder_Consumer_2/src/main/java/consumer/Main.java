package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    // thread number.
    private final static int THREAD_NUM = 100;
    private final static String host = "35.160.124.120";

    /**
     * connect to rabbitmq and run thraeds.
     * @param argv
     * @throws Exception
     */
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername("admin");
        factory.setPassword("password");
        final Connection connection = factory.newConnection();
        SwipeRight sr = new SwipeRight();
        for (int i = 0; i < THREAD_NUM; i++) {
            new Thread(new ConsumerThread(connection, sr)).start();
        }
    }
}