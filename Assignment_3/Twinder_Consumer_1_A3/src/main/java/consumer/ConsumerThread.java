package consumer;

import com.rabbitmq.client.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConsumerThread implements Runnable{
    private final static String QUEUE_NAME = "swipedata";

    private Connection connection;
    private final int prefetchCount = 100;

    private Jedis jedis;
    private List<String> batch = new ArrayList<>();

    /**
     * creat a thread with recorder.
     * @param connection connection to the rabbitmq.
     */

    public ConsumerThread(Connection connection, JedisPool pool) {
        this.connection = connection;
        this.jedis = pool.getResource();
    }
    @Override
    public void run() {
        try {
            final Channel channel = connection.createChannel();
            Map<String, Object> args = new HashMap<>();
            args.put("x-queue-type", "quorum");
            channel.queueDeclare(QUEUE_NAME, true, false, false, args);
            System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

            channel.basicQos(prefetchCount);
            channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    batch.add(message);
                    if (batch.size() >= prefetchCount) {
                        System.out.println("insert into DB...");
                        jedisBatchWrite(batch);
                        channel.basicAck(envelope.getDeliveryTag(), true);
                        batch.clear();
                    }
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void jedisBatchWrite(List<String> batch) {
        try {
            Pipeline pipeline = jedis.pipelined();
            for (String message : batch) {
                String[] parts = message.split(" ");
                String leftOrRight = parts[1];
                String swiperId = parts[3];
                String swipeeId = parts[5];
                pipeline.hincrBy(swiperId, leftOrRight, 1);
                if (leftOrRight.equals("right")) {
                    String pmatches = swiperId + "-pMatches";
                    pipeline.lpush(pmatches, swipeeId);
                }
            }
            pipeline.sync();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

//    public void jedisUpdate(String message) {
//        String[] parts = message.split(" ");
//        String leftOrRight = parts[1];
//        String swiperId = parts[3];
//        String swipeeId = parts[5];
//        jedis.hincrBy(swiperId, leftOrRight, 1);
//        if (leftOrRight.equals("right")) {
//            String pmatches = swiperId + "-pMatches";
//            jedis.rpush(pmatches, swipeeId);
//        }
//    }

}
