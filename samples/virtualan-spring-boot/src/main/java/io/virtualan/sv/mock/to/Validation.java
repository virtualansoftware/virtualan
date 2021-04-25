package io.virtualan.sv.mock.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Validation
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:24.280-05:00[America/Chicago]")
public class Validation   {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("sample1")
  private String sample1;

  @JsonProperty("sample2")
  private String sample2;

  @JsonProperty("sample3")
  private String sample3;

  public Validation id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(example = "1", value = "")


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Validation sample1(String sample1) {
    this.sample1 = sample1;
    return this;
  }

  /**
   * Sample one
   * @return sample1
  */
  @ApiModelProperty(example = "one", value = "Sample one")


  public String getSample1() {
    return sample1;
  }

  public void setSample1(String sample1) {
    this.sample1 = sample1;
  }

  public Validation sample2(String sample2) {
    this.sample2 = sample2;
    return this;
  }

  /**
   * Sample two
   * @return sample2
  */
  @ApiModelProperty(example = "two", value = "Sample two")


  public String getSample2() {
    return sample2;
  }

  public void setSample2(String sample2) {
    this.sample2 = sample2;
  }

  public Validation sample3(String sample3) {
    this.sample3 = sample3;
    return this;
  }

  /**
   * Sample three
   * @return sample3
  */
  @ApiModelProperty(example = "three", value = "Sample three")


  public String getSample3() {
    return sample3;
  }

  public void setSample3(String sample3) {
    this.sample3 = sample3;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Validation validation = (Validation) o;
    return Objects.equals(this.id, validation.id) &&
        Objects.equals(this.sample1, validation.sample1) &&
        Objects.equals(this.sample2, validation.sample2) &&
        Objects.equals(this.sample3, validation.sample3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sample1, sample2, sample3);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Validation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    sample1: ").append(toIndentedString(sample1)).append("\n");
    sb.append("    sample2: ").append(toIndentedString(sample2)).append("\n");
    sb.append("    sample3: ").append(toIndentedString(sample3)).append("\n");
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

