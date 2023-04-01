package model;

public class SwipeInfo {
    private String swiper;
    private String swipee;
    private String comment;

    private String leftOrRight;
    public SwipeInfo(String swiper, String swipee, String comment) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
    }

    public String getSwiper() {
        return swiper;
    }

    public String getSwipee() {
        return swipee;
    }

    public String getComment() {
        return comment;
    }

    public String getLeftOrRight() {
        return leftOrRight;
    }

    public void setLeftOrRight(String leftOrRight) {
        this.leftOrRight = leftOrRight;
    }
}
