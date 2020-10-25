package io.virtualan.core;

import java.util.regex.Pattern;

public final class VirtualServiceConstants {

  private VirtualServiceConstants() {
  }

  public static final String  MATCH_API_TYPE = "[\\sa-zA-Z0-9]Api";

  public static final String  API_SUFFIX = "Api";

  public static final String  PARENT_ROOT = "Parent-Root";

  public static final String  CURLY_PATH = "Curly";

  public static final String  RXP_CURLY = "\\{(.*?)\\}";

  public static final Pattern pattern = Pattern.compile(RXP_CURLY, Pattern.MULTILINE);

}
