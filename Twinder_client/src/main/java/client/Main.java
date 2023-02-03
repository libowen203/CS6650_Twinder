package client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    final static int POOL_SIZE = 500;
    final static int NUM_REQUEST = 500000;
    public static void main(String[] args) {
        RequestCount counter = new RequestCount();
        ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
        LocalDateTime start = LocalDateTime.now();
        System.out.println(start);
        for (int i = 0; i < NUM_REQUEST; i++) {
            service.execute(new ClientThread(counter));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalDateTime end = LocalDateTime.now();
        System.out.println(end);
        System.out.println(counter.getSuccessCount());
        System.out.println(counter.getFailedCount());
        long interval = ChronoUnit.SECONDS.between(start, end);
        System.out.println(interval);
        double throughPut = counter.getSuccessCount() / interval;
        System.out.println(throughPut);
    }
}