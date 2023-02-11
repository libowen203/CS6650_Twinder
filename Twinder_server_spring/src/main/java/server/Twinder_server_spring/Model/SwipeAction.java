package server.Twinder_server_spring.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwipeAction {
    @JsonProperty("swipee")
    private String swipee;
    @JsonProperty("swiper")
    private String swiper;
    @JsonProperty("comment")
    private String comment;

    public SwipeAction(String swiperId, String swipeeId, String comment) {
        this.swipee = swipeeId;
        this.swiper = swiperId;
        this.comment = comment;
    }

    public String getSwiperId() {
        return this.swiper;
    }
    public String getSwipeeId() {
        return this.swipee;
    }
    public String getComment() {
        return this.comment;
    }
}
