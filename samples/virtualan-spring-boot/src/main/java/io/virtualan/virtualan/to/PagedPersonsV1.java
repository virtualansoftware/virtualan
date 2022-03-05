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
 * PagedPersonsV1
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class PagedPersonsV1   {

  @JsonProperty("items")
  @Valid
  private List<Person> items = null;

  @JsonProperty("totalItems")
  private Integer totalItems;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("pageSize")
  private Integer pageSize;

  @JsonProperty("currentPage")
  private Integer currentPage;

  public PagedPersonsV1 items(List<Person> items) {
    this.items = items;
    return this;
  }

  public PagedPersonsV1 addItemsItem(Person itemsItem) {
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

  public PagedPersonsV1 totalItems(Integer totalItems) {
    this.totalItems = totalItems;
    return this;
  }

  /**
   * Get totalItems
   * @return totalItems
  */
  
  @Schema(name = "totalItems", required = false)
  public Integer getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(Integer totalItems) {
    this.totalItems = totalItems;
  }

  public PagedPersonsV1 totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * @return totalPages
  */
  
  @Schema(name = "totalPages", required = false)
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public PagedPersonsV1 pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
  */
  
  @Schema(name = "pageSize", required = false)
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public PagedPersonsV1 currentPage(Integer currentPage) {
    this.currentPage = currentPage;
    return this;
  }

  /**
   * Get currentPage
   * @return currentPage
  */
  
  @Schema(name = "currentPage", required = false)
  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedPersonsV1 pagedPersonsV1 = (PagedPersonsV1) o;
    return Objects.equals(this.items, pagedPersonsV1.items) &&
        Objects.equals(this.totalItems, pagedPersonsV1.totalItems) &&
        Objects.equals(this.totalPages, pagedPersonsV1.totalPages) &&
        Objects.equals(this.pageSize, pagedPersonsV1.pageSize) &&
        Objects.equals(this.currentPage, pagedPersonsV1.currentPage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, totalItems, totalPages, pageSize, currentPage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedPersonsV1 {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    totalItems: ").append(toIndentedString(totalItems)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    currentPage: ").append(toIndentedString(currentPage)).append("\n");
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

