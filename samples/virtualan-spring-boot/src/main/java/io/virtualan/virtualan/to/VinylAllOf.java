package io.virtualan.virtualan.to;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * VinylAllOf
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:58.264811300-06:00[America/Chicago]")
public class VinylAllOf   {

  @JsonProperty("albumName")
  private String albumName;

  @JsonProperty("artist")
  private String artist;

  public VinylAllOf albumName(String albumName) {
    this.albumName = albumName;
    return this;
  }

  /**
   * Get albumName
   * @return albumName
  */
  @NotNull 
  @Schema(name = "albumName", required = true)
  public String getAlbumName() {
    return albumName;
  }

  public void setAlbumName(String albumName) {
    this.albumName = albumName;
  }

  public VinylAllOf artist(String artist) {
    this.artist = artist;
    return this;
  }

  /**
   * Get artist
   * @return artist
  */
  @NotNull 
  @Schema(name = "artist", required = true)
  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VinylAllOf vinylAllOf = (VinylAllOf) o;
    return Objects.equals(this.albumName, vinylAllOf.albumName) &&
        Objects.equals(this.artist, vinylAllOf.artist);
  }

  @Override
  public int hashCode() {
    return Objects.hash(albumName, artist);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VinylAllOf {\n");
    sb.append("    albumName: ").append(toIndentedString(albumName)).append("\n");
    sb.append("    artist: ").append(toIndentedString(artist)).append("\n");
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

