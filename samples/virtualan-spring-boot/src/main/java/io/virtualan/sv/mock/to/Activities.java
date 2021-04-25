package io.virtualan.sv.mock.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/**
 * Activities
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:21.637-05:00[America/Chicago]")
public class Activities   {
  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("count")
  private Integer count;

  @JsonProperty("history")
  @Valid
  private List<Activity> history = null;

  public Activities offset(Integer offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Position in pagination.
   * @return offset
  */
  @ApiModelProperty(value = "Position in pagination.")


  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Activities limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Number of items to retrieve (100 max).
   * @return limit
  */
  @ApiModelProperty(value = "Number of items to retrieve (100 max).")


  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Activities count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Total number of items available.
   * @return count
  */
  @ApiModelProperty(value = "Total number of items available.")


  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Activities history(List<Activity> history) {
    this.history = history;
    return this;
  }

  public Activities addHistoryItem(Activity historyItem) {
    if (this.history == null) {
      this.history = new ArrayList<>();
    }
    this.history.add(historyItem);
    return this;
  }

  /**
   * Get history
   * @return history
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<Activity> getHistory() {
    return history;
  }

  public void setHistory(List<Activity> history) {
    this.history = history;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Activities activities = (Activities) o;
    return Objects.equals(this.offset, activities.offset) &&
        Objects.equals(this.limit, activities.limit) &&
        Objects.equals(this.count, activities.count) &&
        Objects.equals(this.history, activities.history);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offset, limit, count, history);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Activities {\n");
    
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    history: ").append(toIndentedString(history)).append("\n");
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

