package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



public class Main {

    private final static int CONSUME_NUM = 8;
    private final static String RABBIT_HOST = "54.245.141.23";
    private final static String RADIS_HOSOT = "54.214.101.75";
    /**
     * connect to rabbitmq and run thraeds.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        try {
            JedisPool pool = new JedisPool(RADIS_HOSOT, 6379);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_HOST);
            factory.setUsername("admin");
            factory.setPassword("password");
            final Connection conn = factory.newConnection();
            for (int i = 0; i < CONSUME_NUM; i++) {
                new Thread(new ConsumerThread(conn, pool)).start();
            }
//            check if all request is written into redis.
//            try (Jedis jedis = pool.getResource();) {
//                int sum = 0;
//                for (int i = 1; i <= 5000; i++) {
//                    String key = String.valueOf(i);
//                    sum += Integer.parseInt(jedis.hget(key, "left"));
//                    sum += Integer.parseInt(jedis.hget(key, "right"));
//                    System.out.println(i + " " +sum);
//                }
//                System.out.println(sum);
//            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}