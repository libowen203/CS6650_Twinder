package client;

public class RequestCount {
    private int success = 0;
    private int failed = 0;

    public synchronized void incrementSuccessCount() {
        success++;
    }

    public synchronized void incrementFailedCount() {
        failed++;
    }

    public synchronized int getSuccessCount() {
        return success;
    }
    public synchronized int getFailedCount() {
        return failed;
    }
}