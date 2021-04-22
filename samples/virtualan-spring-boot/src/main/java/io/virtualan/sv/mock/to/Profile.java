package io.virtualan.sv.mock.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Profile
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:21.637-05:00[America/Chicago]")
public class Profile   {
  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("email")
  private String email;

  @JsonProperty("picture")
  private String picture;

  @JsonProperty("promo_code")
  private String promoCode;

  public Profile firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * First name of the Uber user.
   * @return firstName
  */
  @ApiModelProperty(value = "First name of the Uber user.")


  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Profile lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Last name of the Uber user.
   * @return lastName
  */
  @ApiModelProperty(value = "Last name of the Uber user.")


  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Profile email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Email address of the Uber user
   * @return email
  */
  @ApiModelProperty(value = "Email address of the Uber user")


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Profile picture(String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Image URL of the Uber user.
   * @return picture
  */
  @ApiModelProperty(value = "Image URL of the Uber user.")


  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public Profile promoCode(String promoCode) {
    this.promoCode = promoCode;
    return this;
  }

  /**
   * Promo code of the Uber user.
   * @return promoCode
  */
  @ApiModelProperty(value = "Promo code of the Uber user.")


  public String getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(String promoCode) {
    this.promoCode = promoCode;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Profile profile = (Profile) o;
    return Objects.equals(this.firstName, profile.firstName) &&
        Objects.equals(this.lastName, profile.lastName) &&
        Objects.equals(this.email, profile.email) &&
        Objects.equals(this.picture, profile.picture) &&
        Objects.equals(this.promoCode, profile.promoCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, email, picture, promoCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Profile {\n");
    
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    promoCode: ").append(toIndentedString(promoCode)).append("\n");
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

