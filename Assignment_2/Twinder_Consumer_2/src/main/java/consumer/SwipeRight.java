package consumer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SwipeRight {
    private Map<String, Deque<String>> potential;
    public SwipeRight() {
        potential = new ConcurrentHashMap<>();
    }

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

    public List<String> getPotentialMatches(String swiperId) {
        if (!potential.containsKey(swiperId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(potential.get(swiperId));
    }
}
