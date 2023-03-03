package consumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class ConsumerThread implements Runnable{
    private static final String EXCHANGE_NAME = "swipeinfo";
    private final static String QUEUE_NAME = "twinderQ1";

    private Connection connection;
    private SwipeCount sc;
    public ConsumerThread(Connection connection, SwipeCount sc) {
        this.connection = connection;
        this.sc = sc;
    }
    @Override
    public void run() {
        try {
            final Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
            // max one message per receiver
            channel.basicQos(1);
            System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
                processMessage(message);
            };
            // process messages
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processMessage(String message) {
        String[] parts = message.split(" ");
        String leftOrRight = parts[1];
        String swiperId = parts[3];
        sc.addCount(swiperId, leftOrRight);
    }
}
