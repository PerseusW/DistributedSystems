package model;

import com.google.gson.Gson;

/**
 * @author Maidi Wang
 */
public class SwipeDetails {

    public enum Swipe {
        left,
        right
    }

    private String swiper;
    private String swipee;
    private String comment;
    private Swipe swipe;

    public SwipeDetails(String swiper, String swipee, String comment, Swipe swipe) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
        this.swipe = swipe;
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

    public Swipe getSwipe() {
        return swipe;
    }

    public void setSwipe(Swipe swipe) {
        this.swipe = swipe;
    }

    @Override
    public String toString() {
        return "SwipeDetails{" +
                "swiper='" + swiper + '\'' +
                ", swipee='" + swipee + '\'' +
                ", comment='" + comment + '\'' +
                ", swipe=" + swipe +
                '}';
    }
}
