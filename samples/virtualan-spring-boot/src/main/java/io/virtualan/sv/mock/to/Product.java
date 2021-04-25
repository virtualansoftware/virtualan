package io.virtualan.sv.mock.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Product
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:21.637-05:00[America/Chicago]")
public class Product   {
  @JsonProperty("product_id")
  private String productId;

  @JsonProperty("description")
  private String description;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("capacity")
  private Integer capacity;

  @JsonProperty("image")
  private String image;

  public Product productId(String productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Unique identifier representing a specific product for a given latitude & longitude. For example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
   * @return productId
  */
  @ApiModelProperty(value = "Unique identifier representing a specific product for a given latitude & longitude. For example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.")


  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public Product description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of product.
   * @return description
  */
  @ApiModelProperty(value = "Description of product.")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Product displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Display name of product.
   * @return displayName
  */
  @ApiModelProperty(value = "Display name of product.")


  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Product capacity(Integer capacity) {
    this.capacity = capacity;
    return this;
  }

  /**
   * Capacity of product. For example, 4 people.
   * @return capacity
  */
  @ApiModelProperty(value = "Capacity of product. For example, 4 people.")


  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Product image(String image) {
    this.image = image;
    return this;
  }

  /**
   * Image URL representing the product.
   * @return image
  */
  @ApiModelProperty(value = "Image URL representing the product.")


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product product = (Product) o;
    return Objects.equals(this.productId, product.productId) &&
        Objects.equals(this.description, product.description) &&
        Objects.equals(this.displayName, product.displayName) &&
        Objects.equals(this.capacity, product.capacity) &&
        Objects.equals(this.image, product.image);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, description, displayName, capacity, image);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Product {\n");
    
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    capacity: ").append(toIndentedString(capacity)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
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

