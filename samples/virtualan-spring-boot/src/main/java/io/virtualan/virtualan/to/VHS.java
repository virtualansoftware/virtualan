package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * VHS
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class VHS extends CollectingItem  {

  @JsonProperty("movieTitle")
  private String movieTitle;

  @JsonProperty("director")
  private String director;

  public VHS movieTitle(String movieTitle) {
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

  public VHS director(String director) {
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
    VHS VHS = (VHS) o;
    return Objects.equals(this.movieTitle, VHS.movieTitle) &&
        Objects.equals(this.director, VHS.director) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(movieTitle, director, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VHS {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

