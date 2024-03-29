package model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Maidi Wang
 */
public class SwipeDetails {
    // Inclusive ranges.
    private static final int[] SWIPER_RANGE = {1, 5000};
    private static final int[] SWIPEE_RANGE = {1, 1000000};
    private static final int COMMENT_LENGTH = 256;
    private final String swiper;
    private final String swipee;
    private final String comment;

    public SwipeDetails(String swiper, String swipee, String comment) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
    }

    public static String getRandomSwipe() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return "left";
        }
        return "right";
    }

    public static String getRandomSwiper() {
        return String.valueOf(
                ThreadLocalRandom.current().nextInt(SWIPER_RANGE[0], SWIPER_RANGE[1] + 1)
        );
    }

    public static SwipeDetails getRandomSwipeDetails() {
        return new SwipeDetails(
                getRandomSwiper(),
                getRandomSwiper(),
                ThreadLocalRandom.current()
                        .ints('a', 'z')
                        .limit(COMMENT_LENGTH)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString());
    }
}
