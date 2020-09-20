package io.virtualan.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SoapService {

  private String localPart;
  private String ns;
  private String method;
  private String requestClassName;
  private String responseClassName;
  private String description;

}
