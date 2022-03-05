package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * PagedPersonsV2
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class PagedPersonsV2   {

  @JsonProperty("items")
  @Valid
  private List<Person> items = null;

  @JsonProperty("paging")
  private Paging paging;

  public PagedPersonsV2 items(List<Person> items) {
    this.items = items;
    return this;
  }

  public PagedPersonsV2 addItemsItem(Person itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid 
  @Schema(name = "items", required = false)
  public List<Person> getItems() {
    return items;
  }

  public void setItems(List<Person> items) {
    this.items = items;
  }

  public PagedPersonsV2 paging(Paging paging) {
    this.paging = paging;
    return this;
  }

  /**
   * Get paging
   * @return paging
  */
  @Valid 
  @Schema(name = "paging", required = false)
  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedPersonsV2 pagedPersonsV2 = (PagedPersonsV2) o;
    return Objects.equals(this.items, pagedPersonsV2.items) &&
        Objects.equals(this.paging, pagedPersonsV2.paging);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, paging);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedPersonsV2 {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    paging: ").append(toIndentedString(paging)).append("\n");
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

