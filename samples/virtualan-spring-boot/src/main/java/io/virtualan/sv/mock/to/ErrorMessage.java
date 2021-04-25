package io.virtualan.sv.mock.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ErrorMessage
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:22.578-05:00[America/Chicago]")
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
  @ApiModelProperty(value = "")


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
  @ApiModelProperty(value = "")


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

