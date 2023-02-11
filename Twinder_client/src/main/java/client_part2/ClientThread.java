package client_part2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A thread class for sending request to the server.
 */
public class ClientThread extends Thread {
    private RequestCount counter;
    private int numRequest;
    private List<String[]> record;
    private String url;

    /**
     * ClientThread constructor.
     * @param counter A counter to record the num of successful requests and failed requests.
     */
    public ClientThread(RequestCount counter, int numRequest, String url) {
        this.counter = counter;
        this.numRequest = numRequest;
        this.url = url;
        record = new ArrayList<>();
    }

    /**
     * Creaet a client and swipeApi, send a number of requests, if one request failed try
     * another 5 times until succeed, if failed still, it a failed request.
     */
    public void run() {
        ApiClient client = new ApiClient();
        client.setBasePath(url);
        SwipeApi swipeApi = new SwipeApi(client);
        SwipeDetails body = new SwipeDetails();
        for (int j = 0; j < numRequest; j++) {
            String leftOrRight = ThreadLocalRandom.current().nextInt(0, 2) == 0 ? "left" : "right";
            String swiperId = String.valueOf(ThreadLocalRandom.current().nextInt(1, 5001));
            String swipeeId = String.valueOf(ThreadLocalRandom.current().nextInt(1, 1000001));
            String comment = randomComment();
            body.setSwiper(swiperId);
            body.setSwipee(swipeeId);
            body.setComment(comment);
            for (int i = 0; i < 7; i++) {
                String[] arr = new String[4];
                long start = System.currentTimeMillis();
                arr[0] = String.valueOf(start);
                arr[1] = "post";
                try {
                    ApiResponse res = swipeApi.swipeWithHttpInfo(body, leftOrRight);
                    long end = System.currentTimeMillis();
                    long interval = end - start;
                    arr[2] = String.valueOf(interval);
                    arr[3] = String.valueOf(res.getStatusCode());
                    record.add(arr);
                    if (res.getStatusCode() == 201) {
                        counter.incrementSuccessCount();
                        break;
                    } else {
                        counter.incrementFailedCount();
                    }
                } catch (ApiException e) {
                    if (e.getCode() != 201) {
                        long end = System.currentTimeMillis();
                        long interval = end - start;
                        arr[2] = String.valueOf(interval);
                        arr[3] = String.valueOf(e.getCode());
                        record.add(arr);
                    }
                    e.printStackTrace();
                    if (i == 6) {
                        counter.incrementFailedCount();
                    }
                }
            }
        }
        Writer.writeRequest("files/requests.csv", record);
    }

    /**
     * Generate a random String representing the comment.
     *
     * @return A random String have lower english characters with length < 257.
     */
    public String randomComment() {
        StringBuilder builder = new StringBuilder();
        int num = ThreadLocalRandom.current().nextInt(1, 257);
        for (int i = 0; i < num; i++) {
            builder.append((char) (ThreadLocalRandom.current().nextInt(97, 123)));
        }
        return builder.toString();
    }
}
