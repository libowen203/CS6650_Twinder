package consumer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SwipeRight {
    private Map<String, Deque<String>> potential;

    /**
     * creat a SwipeRight object to recorde the most recent 100
     * right swipee of a swiper
     */
    public SwipeRight() {
        potential = new ConcurrentHashMap<>();
    }

    /**
     * add a swipee to the right swipee collection of a swiper
     * if the queue is full, poll and then add.
     * @param swiperId
     * @param swipee
     */
    public void addPotentialMatch(String swiperId, String swipee) {
        if (!potential.containsKey(swiperId)) {
            potential.put(swiperId, new ArrayDeque<>());
        }
        Deque<String> pm = potential.get(swiperId);
        if (pm.size() == 100) {
            pm.poll();
        }
        pm.add(swipee);
    }

    /**
     * Get the most recent right swipee of a swiper.
     * @param swiperId
     * @return
     */
    public List<String> getPotentialMatches(String swiperId) {
        if (!potential.containsKey(swiperId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(potential.get(swiperId));
    }
}
