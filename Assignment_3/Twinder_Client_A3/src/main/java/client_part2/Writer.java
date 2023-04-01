package client_part2;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.List;

/**
 * A static Writer class for save the information to a file.
 */
public class Writer {
    /**
     * Save request information.
     * @param filePath A destination file path.
     * @param content A List<String[]> each String[] contains the request start time, interval, and latency.
     */
    public static synchronized void writeRequest(String filePath, List<String[]> content) {
        try {
            FileWriter outputfile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(content);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Save terminal output information.
     * @param content String[] contains number of threads, starttime, endtime, interval, successful requests
     *                throughput.
     */
    public static synchronized String writeStat(String[] content, String[] analyze, String[] getsAna) {
        String res = "";
        try(FileWriter fw = new FileWriter("files/stats.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw))
        {
            res = "NUM_THREAD :" + content[0] + "\n"
                    + "Start time: " + content[1] + "\n"
                    + "End time: " + content[2] + "\n"
                    + "Interval time: " + content[3] + "\n"
                    + "Successful requests " + content[4] + "\n"
                    + "Throughput : " + content[5] + "\n"
                    + "\n"
                    + "Mean: " + analyze[0] + "\n"
                    + "Median: " + analyze[1] + "\n"
                    + "99th percentile: " + analyze[2] + "\n"
                    + "99th percentile by Histogram: " + analyze[3] + "\n"
                    + "Min: " + analyze[4] + "\n"
                    + "Max: " + analyze[5]+ "\n"
                    + "\n"
                    + "Get Requests Analyzation \n"
                    + "Mean: " + getsAna[0] + "\n"
                    + "Median: " + getsAna[1] + "\n"
                    + "99th percentile: " + getsAna[2] + "\n"
                    + "99th percentile by Histogram: " + getsAna[3] + "\n"
                    + "Min: " + getsAna[4] + "\n"
                    + "Max: " + getsAna[5]+ "\n";

            writer.println(res);
            writer.println();
            writer.println("----------------------------------------------------------");
            writer.println();
            writer.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Save throughput per second.
     * @param content String[] contains second and throughtput
     */
    public static synchronized void writeThroughputSec(String filePath, List<String[]> content) {
        try {
            FileWriter outputfile = new FileWriter(filePath);

            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(content);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
