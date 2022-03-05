package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * VHSAllOf
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class VHSAllOf   {

  @JsonProperty("movieTitle")
  private String movieTitle;

  @JsonProperty("director")
  private String director;

  public VHSAllOf movieTitle(String movieTitle) {
    this.movieTitle = movieTitle;
    return this;
  }

  /**
   * Get movieTitle
   * @return movieTitle
  */
  @NotNull 
  @Schema(name = "movieTitle", required = true)
  public String getMovieTitle() {
    return movieTitle;
  }

  public void setMovieTitle(String movieTitle) {
    this.movieTitle = movieTitle;
  }

  public VHSAllOf director(String director) {
    this.director = director;
    return this;
  }

  /**
   * Get director
   * @return director
  */
  
  @Schema(name = "director", required = false)
  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VHSAllOf vhSAllOf = (VHSAllOf) o;
    return Objects.equals(this.movieTitle, vhSAllOf.movieTitle) &&
        Objects.equals(this.director, vhSAllOf.director);
  }

  @Override
  public int hashCode() {
    return Objects.hash(movieTitle, director);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VHSAllOf {\n");
    sb.append("    movieTitle: ").append(toIndentedString(movieTitle)).append("\n");
    sb.append("    director: ").append(toIndentedString(director)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

