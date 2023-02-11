package client_part2;

/**
 * A class have synchronized functions to let threads count numbers.
 */
public class RequestCount {
    private int success = 0;
    private int failed = 0;

    /**
     * Increment the successful requests count.
     */
    public synchronized void incrementSuccessCount() {
        success++;
    }

    /**
     * increment the failed requests count.
     */
    public synchronized void incrementFailedCount() {
        failed++;
    }

    /**
     * Return the successful request count
     * @return
     */
    public synchronized int getSuccessCount() {
        return success;
    }

    /**
     * Return the failed request count
     * @return
     */
    public synchronized int getFailedCount() {
        return failed;
    }
}