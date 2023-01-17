package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SwipeDetails
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-01-16T06:16:37.918Z[GMT]")


public class SwipeDetails   {
  @JsonProperty("swiper")
  private String swiper = null;

  @JsonProperty("swipee")
  private String swipee = null;

  @JsonProperty("comment")
  private String comment = null;

  public SwipeDetails swiper(String swiper) {
    this.swiper = swiper;
    return this;
  }

  /**
   * Get swiper
   * @return swiper
   **/
  @Schema(example = "233", description = "")
  
    public String getSwiper() {
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public SwipeDetails swipee(String swipee) {
    this.swipee = swipee;
    return this;
  }

  /**
   * Get swipee
   * @return swipee
   **/
  @Schema(example = "21345", description = "")
  
    public String getSwipee() {
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public SwipeDetails comment(String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment
   * @return comment
   **/
  @Schema(example = "you are not my type, loser", description = "")
  
    public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SwipeDetails swipeDetails = (SwipeDetails) o;
    return Objects.equals(this.swiper, swipeDetails.swiper) &&
        Objects.equals(this.swipee, swipeDetails.swipee) &&
        Objects.equals(this.comment, swipeDetails.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(swiper, swipee, comment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SwipeDetails {\n");
    
    sb.append("    swiper: ").append(toIndentedString(swiper)).append("\n");
    sb.append("    swipee: ").append(toIndentedString(swipee)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
