package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

public class ConsumerThread implements Runnable{
    private final static String QUEUE_NAME = "twinderQ2";
    private static final String EXCHANGE_NAME = "swipeinfo";
    private Connection conn;
    private SwipeRight sr;

    /**
     * creat a thread with recorder.
     * @param conn connection to the rabbitmq.
     * @param sr right swipe recorder
     */
    public ConsumerThread(Connection conn, SwipeRight sr) {
        this.conn = conn;
        this.sr = sr;
    }

    @Override
    public void run() {
        try {
            final Channel channel = conn.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
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
            e.printStackTrace();
        }
    }

    /**
     * process message, the message can be: "swiperId 123 swipeeId 222 comment asdasdasd"
     * split by " " and get an array, use the information to store in the SwipeRight object.
     * @param message
     */
    private void processMessage(String message) {
        String[] parts = message.split(" ");
        String leftOrRight = parts[1];
        String swiperId = parts[3];
        String swipeeId = parts[5];
        if (leftOrRight.equals("right")) {
            sr.addPotentialMatch(swiperId, swipeeId);
        }
    }
}
