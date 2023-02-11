package client_part2;

import com.ibm.hbpe.HistogramBasedPercentileEstimator;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Analyzer {
    /**
     * Analyze the min latency, max latency, mean latency, median latency, 99th percentile latency by
     * the given requests data.
     * @param filePath the path to the request data.
     * @return String[] contains min latency, max latency, mean latency, median latency, 99th percentile latency
     */
    public static String[] analyze(String filePath) {
        String[] res = null;
        List<long[]> data = readFile(filePath);
        long sum = 0;
        HistogramBasedPercentileEstimator hbpe = new HistogramBasedPercentileEstimator(1);
        for (long[] l : data) {
            sum += l[1];
            hbpe.addValue(l[1]);
        }
        Collections.sort(data, (a,b) -> Long.compare(a[1], b[1]));
        long min = data.get(0)[1];
        long max = data.get(data.size() - 1)[1];
        long mean = sum / data.size();
        long median = data.size() % 2 != 0 ? data.get(data.size() / 2)[1] :
                (data.get(data.size() / 2)[1] + data.get(data.size() / 2 + 1)[1]) / 2;
        int ind99 = (int) (0.99 * data.size());
        long per99 = data.get(ind99)[1];
        double hisPer99 = hbpe.getPercentile(99.0);
        res = new String[]{String.valueOf(mean), String.valueOf(median), String.valueOf(per99),
                String.valueOf(hisPer99), String.valueOf(min), String.valueOf(max)};
        return res;
    }

    /**
     * Compute the throughput per second by the given requests data and write the result to a csv file.
     * @param filePath the path to the requests data file.
     */
    public static void computeThroughputPerSecond(String filePath) {
        List<long[]> data = readFile(filePath);
        List<Long> endTime = new ArrayList<>();
        long min = Long.MAX_VALUE;
        for (long[] d : data) {
            min = Math.min(min, d[0]);
            endTime.add(d[0] + d[1]);
        }
        Collections.sort(endTime);
        HashMap<Integer, Integer> map = new HashMap<>();
        int sec = 0;
        long cur = min + 1000;
        for (Long t : endTime) {
            if (t < cur) {
                map.put(sec, map.getOrDefault(sec, 0) + 1);
            }
            else {
                map.put(sec, map.getOrDefault(sec, 0));
                cur += 1000;
                sec += 1;
            }

        }
        List<String[]> content = new ArrayList<>();
        for (int i = 0; i <= sec; i++) {
            if (map.containsKey(i)) {
                String[] ele = new String[]{String.valueOf(i), String.valueOf(map.get(i))};
                content.add(ele);
            }
        }
        Writer.writeThroughputSec("files/throughput_per_second.csv", content);
    }

    /**
     * Read the requests data csv file into an ArrayList<> for further process.
     * @param filePath the path to the requests data csv file
     * @return ArrayList<>
     */
    public static List<long[]> readFile(String filePath) {
        List<long[]> res = null;
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(filereader);
            List<String[]> data = csvReader.readAll();
            res = new ArrayList<>();
            for (String[] d : data) {
                long[] ele = new long[2];
                ele[0] = Long.parseLong(d[0]);
                ele[1] = Long.parseLong(d[2]);
                res.add(ele);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


}
