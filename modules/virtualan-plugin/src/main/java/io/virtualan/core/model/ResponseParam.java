package io.virtualan.core.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ResponseParam {
    Map<String, String> records = new HashMap<>();
}