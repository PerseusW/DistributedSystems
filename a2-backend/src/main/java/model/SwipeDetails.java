package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Maidi Wang
 * Do not touch, fields are needed for serialization.
 */
public class SwipeDetails implements Serializable {
    public Boolean left;
    public String swiper;
    public String swipee;
    public String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwipeDetails that = (SwipeDetails) o;
        return Objects.equals(left, that.left) && Objects.equals(swiper, that.swiper) && Objects.equals(swipee, that.swipee) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, swiper, swipee, comment);
    }

    @Override
    public String toString() {
        return "SwipeDetails{" +
                "left=" + left +
                ", swiper='" + swiper + '\'' +
                ", swipee='" + swipee + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
