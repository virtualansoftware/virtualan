package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * PagedCollectingItemsAllOf
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class PagedCollectingItemsAllOf   {

  @JsonProperty("items")
  @Valid
  private Set<CollectingItem> items = null;

  public PagedCollectingItemsAllOf items(Set<CollectingItem> items) {
    this.items = items;
    return this;
  }

  public PagedCollectingItemsAllOf addItemsItem(CollectingItem itemsItem) {
    if (this.items == null) {
      this.items = new LinkedHashSet<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid @Size(min = 10, max = 100) 
  @Schema(name = "items", required = false)
  public Set<CollectingItem> getItems() {
    return items;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setItems(Set<CollectingItem> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedCollectingItemsAllOf pagedCollectingItemsAllOf = (PagedCollectingItemsAllOf) o;
    return Objects.equals(this.items, pagedCollectingItemsAllOf.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedCollectingItemsAllOf {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

