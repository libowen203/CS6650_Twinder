package client_part2;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    final static int NUM_THREAD = 600;
    final static int TOTAL_REQUEST = 500000;
    final static String SERVER_URL = "http://server-alb-466139350.us-west-2.elb.amazonaws.com:8080/Twinder_Server_2_war";

    /**
     * Use a thread pool of fixed size of NUM_THREAD and have a TOTAL_REQUEST number, each thread is going to send
     * TOTAL_REQUEST / NUM_THREAD number of requests to the server (SERVER_URL)
     * After all finished, the result would be printed in the terminal.
     * The analyzer would analyze the latency stats and write a throughput per second file.
     * @param args
     */
    public static void main(String[] args) {
        RequestCount counter = new RequestCount();
        ExecutorService service = Executors.newFixedThreadPool(NUM_THREAD);
        LocalDateTime start = LocalDateTime.now();
        System.out.println("Start: " + start);

        try {
            FileWriter fw = new FileWriter("files/requests.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < NUM_THREAD; i++) {
            int num = TOTAL_REQUEST / NUM_THREAD;
            if (i == NUM_THREAD - 1) {
                num += TOTAL_REQUEST % NUM_THREAD;
            }
            service.execute(new ClientThread(counter, num, SERVER_URL));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalDateTime end = LocalDateTime.now();
        long interval = ChronoUnit.SECONDS.between(start, end);
        double throughPut = counter.getSuccessCount() / interval;

        String[] content = {String.valueOf(NUM_THREAD), start.toString(), end.toString(), String.valueOf(interval), String.valueOf(counter.getSuccessCount()), String.valueOf(throughPut)};
        String[] analyze = Analyzer.analyze("files/requests.csv");
        String ana = Writer.writeStat(content,analyze);
        System.out.println(ana);
        Analyzer.computeThroughputPerSecond("files/requests.csv");
    }
}