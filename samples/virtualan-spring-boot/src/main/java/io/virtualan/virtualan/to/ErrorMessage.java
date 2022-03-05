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
 * ErrorMessage
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class ErrorMessage   {

  @JsonProperty("longMessage")
  private String longMessage;

  @JsonProperty("shortMessage")
  private String shortMessage;

  public ErrorMessage longMessage(String longMessage) {
    this.longMessage = longMessage;
    return this;
  }

  /**
   * Get longMessage
   * @return longMessage
  */
  
  @Schema(name = "longMessage", required = false)
  public String getLongMessage() {
    return longMessage;
  }

  public void setLongMessage(String longMessage) {
    this.longMessage = longMessage;
  }

  public ErrorMessage shortMessage(String shortMessage) {
    this.shortMessage = shortMessage;
    return this;
  }

  /**
   * Get shortMessage
   * @return shortMessage
  */
  
  @Schema(name = "shortMessage", required = false)
  public String getShortMessage() {
    return shortMessage;
  }

  public void setShortMessage(String shortMessage) {
    this.shortMessage = shortMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorMessage errorMessage = (ErrorMessage) o;
    return Objects.equals(this.longMessage, errorMessage.longMessage) &&
        Objects.equals(this.shortMessage, errorMessage.shortMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(longMessage, shortMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorMessage {\n");
    sb.append("    longMessage: ").append(toIndentedString(longMessage)).append("\n");
    sb.append("    shortMessage: ").append(toIndentedString(shortMessage)).append("\n");
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

