package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Person
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class Person   {

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("dateOfBirth")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dateOfBirth;

  @JsonProperty("lastTimeOnline")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastTimeOnline;

  @JsonProperty("spokenLanguages")
  @Valid
  private Map<String, String> spokenLanguages = null;

  public Person firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
  */
  
  @Schema(name = "firstName", required = false)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Person lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
  */
  
  @Schema(name = "lastName", required = false)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Person username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @NotNull @Pattern(regexp = "[a-z0-9]{8,64}") @Size(min = 8, max = 64) 
  @Schema(name = "username", required = true)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Person dateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  /**
   * Get dateOfBirth
   * @return dateOfBirth
  */
  @Valid 
  @Schema(name = "dateOfBirth", required = false)
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Person lastTimeOnline(OffsetDateTime lastTimeOnline) {
    this.lastTimeOnline = lastTimeOnline;
    return this;
  }

  /**
   * Get lastTimeOnline
   * @return lastTimeOnline
  */
  @Valid 
  @Schema(name = "lastTimeOnline", accessMode = Schema.AccessMode.READ_ONLY, required = false)
  public OffsetDateTime getLastTimeOnline() {
    return lastTimeOnline;
  }

  public void setLastTimeOnline(OffsetDateTime lastTimeOnline) {
    this.lastTimeOnline = lastTimeOnline;
  }

  public Person spokenLanguages(Map<String, String> spokenLanguages) {
    this.spokenLanguages = spokenLanguages;
    return this;
  }

  public Person putSpokenLanguagesItem(String key, String spokenLanguagesItem) {
    if (this.spokenLanguages == null) {
      this.spokenLanguages = new HashMap<>();
    }
    this.spokenLanguages.put(key, spokenLanguagesItem);
    return this;
  }

  /**
   * Get spokenLanguages
   * @return spokenLanguages
  */
  
  @Schema(name = "spokenLanguages", required = false)
  public Map<String, String> getSpokenLanguages() {
    return spokenLanguages;
  }

  public void setSpokenLanguages(Map<String, String> spokenLanguages) {
    this.spokenLanguages = spokenLanguages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(this.firstName, person.firstName) &&
        Objects.equals(this.lastName, person.lastName) &&
        Objects.equals(this.username, person.username) &&
        Objects.equals(this.dateOfBirth, person.dateOfBirth) &&
        Objects.equals(this.lastTimeOnline, person.lastTimeOnline) &&
        Objects.equals(this.spokenLanguages, person.spokenLanguages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, username, dateOfBirth, lastTimeOnline, spokenLanguages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Person {\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    dateOfBirth: ").append(toIndentedString(dateOfBirth)).append("\n");
    sb.append("    lastTimeOnline: ").append(toIndentedString(lastTimeOnline)).append("\n");
    sb.append("    spokenLanguages: ").append(toIndentedString(spokenLanguages)).append("\n");
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

