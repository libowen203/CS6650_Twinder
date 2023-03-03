package consumer;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SwipeCount {
    private ConcurrentHashMap<String, int[]> countBook;
    /**
     * creat a SwipeCount object to record the left and right swipes of a swiper.
     */
    public SwipeCount() {
        countBook = new ConcurrentHashMap<>();
    }

    /**
     * if leftOrRight is right, add right swipe, else add left swipe.
     * @param userId
     * @param leftOrRight
     */
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

    /**
     * Get the left and right swipes numbers of a swiper.
     * @param userId
     * @return
     */
    public int[] getCount(String userId) {
        if (!countBook.containsKey(userId)) {
            return new int[]{0,0};
        }
        return countBook.get(userId);
    }
}