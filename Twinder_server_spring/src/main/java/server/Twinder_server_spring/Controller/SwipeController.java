package server.Twinder_server_spring.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.Twinder_server_spring.Model.SwipeAction;

@RestController
public class SwipeController {
    @RequestMapping(value = "/swipe/{leftorright}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> persistPerson(@PathVariable String leftorright, @RequestBody SwipeAction swipe) {
        try {
            if (!leftorright.equals("left") && !leftorright.equals("right")) {
                return new ResponseEntity<>("url error", HttpStatus.NOT_FOUND);
            }
            if (!isPostDataValid(swipe)) {
                return new ResponseEntity<>(swipe.getSwiperId() + " " + swipe.getSwipeeId() + " "
                        + swipe.getComment() + " post data wrong format", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("It Worked", HttpStatus.CREATED);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Exception Occured", HttpStatus.NOT_FOUND);
    }

    public boolean isPostDataValid(SwipeAction swipe) {
        try {
            int swiperId = Integer.parseInt(swipe.getSwiperId());
            int swipeeId = Integer.parseInt(swipe.getSwipeeId());
            String comment = swipe.getComment();
            if (swiperId < 1 || swiperId > 5000 || swipeeId < 1 || swipeeId > 1000000 || comment.length() > 256) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
