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
 * PagedPersons
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class PagedPersons   {

  @JsonProperty("items")
  @Valid
  private Set<Person> items = null;

  @JsonProperty("totalItems")
  private Integer totalItems;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("pageSize")
  private Integer pageSize;

  @JsonProperty("currentPage")
  private Integer currentPage;

  public PagedPersons items(Set<Person> items) {
    this.items = items;
    return this;
  }

  public PagedPersons addItemsItem(Person itemsItem) {
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
  public Set<Person> getItems() {
    return items;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setItems(Set<Person> items) {
    this.items = items;
  }

  public PagedPersons totalItems(Integer totalItems) {
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

  public PagedPersons totalPages(Integer totalPages) {
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

  public PagedPersons pageSize(Integer pageSize) {
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

  public PagedPersons currentPage(Integer currentPage) {
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
    PagedPersons pagedPersons = (PagedPersons) o;
    return Objects.equals(this.items, pagedPersons.items) &&
        Objects.equals(this.totalItems, pagedPersons.totalItems) &&
        Objects.equals(this.totalPages, pagedPersons.totalPages) &&
        Objects.equals(this.pageSize, pagedPersons.pageSize) &&
        Objects.equals(this.currentPage, pagedPersons.currentPage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, totalItems, totalPages, pageSize, currentPage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedPersons {\n");
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

