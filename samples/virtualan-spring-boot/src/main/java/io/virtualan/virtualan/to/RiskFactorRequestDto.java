package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * RiskFactorRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:59.311440500-06:00[America/Chicago]")
public class RiskFactorRequestDto   {

  @JsonProperty("birthday")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate birthday;

  @JsonProperty("postalCode")
  private String postalCode;

  public RiskFactorRequestDto birthday(LocalDate birthday) {
    this.birthday = birthday;
    return this;
  }

  /**
   * Get birthday
   * @return birthday
  */
  @Valid 
  @Schema(name = "birthday", required = false)
  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public RiskFactorRequestDto postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
  */
  
  @Schema(name = "postalCode", required = false)
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskFactorRequestDto riskFactorRequestDto = (RiskFactorRequestDto) o;
    return Objects.equals(this.birthday, riskFactorRequestDto.birthday) &&
        Objects.equals(this.postalCode, riskFactorRequestDto.postalCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(birthday, postalCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RiskFactorRequestDto {\n");
    sb.append("    birthday: ").append(toIndentedString(birthday)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
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

