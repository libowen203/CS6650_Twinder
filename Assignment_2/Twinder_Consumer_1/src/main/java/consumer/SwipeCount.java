package consumer;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SwipeCount {
    private ConcurrentHashMap<String, int[]> countBook;

    public SwipeCount() {
        countBook = new ConcurrentHashMap<>();
    }

    public void addCount(String userId, String leftOrRight) {
        if (!countBook.containsKey(userId)) {
            countBook.put(userId, new int[]{0, 0});
        }
        int[] co = countBook.get(userId);
        if (leftOrRight.equals("left")) {
            co[0]++;
        }
        else {
            co[1]++;
        }
        countBook.put(userId, co);
    }

    public int[] getCount(String userId) {
        if (!countBook.containsKey(userId)) {
            return new int[]{0,0};
        }
        return countBook.get(userId);
    }
}