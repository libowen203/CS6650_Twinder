package client;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {
    RequestCount counter;

    public ClientThread(RequestCount counter) {
        this.counter = counter;
    }

    public void run() {

        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:8080/Twinder_war_exploded/");
        SwipeApi swipeApi = new SwipeApi(client);
        SwipeDetails body = new SwipeDetails();
        for (int j = 0; j < 1; j++) {
            String leftOrRight = ThreadLocalRandom.current().nextInt(0, 2) == 0 ? "left" : "right";
            String swiperId = String.valueOf(ThreadLocalRandom.current().nextInt(1, 5001));
            String swipeeId = String.valueOf(ThreadLocalRandom.current().nextInt(1, 1000001));
            String comment = randomComment();
            body.setSwiper("acc");
            body.setSwipee(swipeeId);
            body.setComment(comment);
            for (int i = 0; i < 6; i++) {
                try {
                    ApiResponse res = swipeApi.swipeWithHttpInfo(body, leftOrRight);
                    if (res.getStatusCode() == 201) {
                        counter.incrementSuccessCount();
                        break;
                    } else {
                        counter.incrementFailedCount();
                    }
                } catch (ApiException e) {
                    if (i == 4) {
                        counter.incrementFailedCount();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String randomComment() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            builder.append((char) (ThreadLocalRandom.current().nextInt(97, 123)));
        }
        return builder.toString();
    }
}
