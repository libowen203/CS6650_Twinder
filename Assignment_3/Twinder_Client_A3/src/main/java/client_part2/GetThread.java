package client_part2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetThread implements Runnable{
    private String address;
    private List<String[]> gets;
    int count = 0;
    public GetThread(String address, List<String[]> gets) {
        this.address = address;
        this.gets = gets;
    }
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long cur1 = System.currentTimeMillis();
                for (int i = 0; i < 5; i++) {
                    String[] arr = new String[4];
                    long start = System.currentTimeMillis();
                    arr[0] = String.valueOf(start);
                    arr[1] = "get";
                    int rand = ThreadLocalRandom.current().nextInt(0, 2);
                    int status = sendGetRequest(rand);
                    long end = System.currentTimeMillis();
                    long interval = end - start;
                    arr[2] = String.valueOf(interval);
                    arr[3] = String.valueOf(status);
                    count++;
                    gets.add(arr);
                }
                long cur2 = System.currentTimeMillis();
                long dif = 1000 - (cur2 - cur1);
                if (dif > 10) {
                    Thread.sleep(dif);
                }
            }
        } catch (Exception e) {

        }

    }
    private int sendGetRequest(int rand) {
        int responseCode = 400;
        try {
            String request = "";
            if (rand == 0) {
                request = "stats";
            }
            else {
                request = "matches";
            }
            String randId = String.valueOf(ThreadLocalRandom.current().nextInt(1, 5001));
            String urlString = address + "/" + request + "/" +randId;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();;
        } catch (Exception e) {
        }
        return responseCode;
    }
}
