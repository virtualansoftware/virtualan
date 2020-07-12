package org.openapitools.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * RiskFactorResponseDto
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-12T09:46:04.576-05:00[America/Chicago]")

public class RiskFactorResponseDto   {
  @JsonProperty("riskFactor")
  private Long riskFactor;

  public RiskFactorResponseDto riskFactor(Long riskFactor) {
    this.riskFactor = riskFactor;
    return this;
  }

  /**
   * Get riskFactor
   * @return riskFactor
  */
  @ApiModelProperty(value = "")


  public Long getRiskFactor() {
    return riskFactor;
  }

  public void setRiskFactor(Long riskFactor) {
    this.riskFactor = riskFactor;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskFactorResponseDto riskFactorResponseDto = (RiskFactorResponseDto) o;
    return Objects.equals(this.riskFactor, riskFactorResponseDto.riskFactor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(riskFactor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RiskFactorResponseDto {\n");
    
    sb.append("    riskFactor: ").append(toIndentedString(riskFactor)).append("\n");
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

