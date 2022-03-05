package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * CollectingItem
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "itemType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = VHS.class, name = "VHS"),
  @JsonSubTypes.Type(value = Vinyl.class, name = "Vinyl"),
})
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class CollectingItem   {

  /**
   * Gets or Sets itemType
   */
  public enum ItemTypeEnum {
    VINYL("Vinyl"),
    
    VHS("VHS");

    private String value;

    ItemTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ItemTypeEnum fromValue(String value) {
      for (ItemTypeEnum b : ItemTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("itemType")
  private ItemTypeEnum itemType;

  @JsonProperty("imageId")
  private String imageId;

  @JsonProperty("maxPrice")
  private Double maxPrice;

  public CollectingItem itemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
    return this;
  }

  /**
   * Get itemType
   * @return itemType
  */
  @NotNull 
  @Schema(name = "itemType", required = true)
  public ItemTypeEnum getItemType() {
    return itemType;
  }

  public void setItemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
  }

  public CollectingItem imageId(String imageId) {
    this.imageId = imageId;
    return this;
  }

  /**
   * Get imageId
   * @return imageId
  */
  
  @Schema(name = "imageId", required = false)
  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public CollectingItem maxPrice(Double maxPrice) {
    this.maxPrice = maxPrice;
    return this;
  }

  /**
   * Get maxPrice
   * minimum: 0
   * maximum: 1E+4
   * @return maxPrice
  */
  @DecimalMin(value = "0", inclusive = false) @DecimalMax("1E+4") 
  @Schema(name = "maxPrice", required = false)
  public Double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(Double maxPrice) {
    this.maxPrice = maxPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectingItem collectingItem = (CollectingItem) o;
    return Objects.equals(this.itemType, collectingItem.itemType) &&
        Objects.equals(this.imageId, collectingItem.imageId) &&
        Objects.equals(this.maxPrice, collectingItem.maxPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemType, imageId, maxPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectingItem {\n");
    sb.append("    itemType: ").append(toIndentedString(itemType)).append("\n");
    sb.append("    imageId: ").append(toIndentedString(imageId)).append("\n");
    sb.append("    maxPrice: ").append(toIndentedString(maxPrice)).append("\n");
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

