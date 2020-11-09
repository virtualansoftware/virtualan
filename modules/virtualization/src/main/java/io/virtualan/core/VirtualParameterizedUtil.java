package io.virtualan.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.core.model.ContentType;
import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.model.ResponseParam;
import io.virtualan.core.model.ResponseProcessType;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceValidRequest;
import io.virtualan.core.util.VirtualXPaths;
import io.virtualan.core.util.XMLConverter;
import io.virtualan.mapson.Mapson;
import io.virtualan.params.Param;
import io.virtualan.params.ParamTypes;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VirtualParameterizedUtil {


  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private VirtualServiceValidRequest virtualServiceValidRequest;


  @Autowired
  private VirtualServiceUtil virtualServiceUtil;

  public Map<Integer, ResponseParam> handleParameterizedRequest(
      VirtualServiceRequest mockTransferObject) {
    MockServiceRequest mockServiceRequest = virtualServiceUtil.buildMockServiceRequest(mockTransferObject);
    final Map<MockRequest, MockResponse> mockDataSetupMap = virtualServiceUtil.readDynamicResponse(
        mockServiceRequest.getResource(), mockServiceRequest.getOperationId());
    return handleParameterizedRequest(
        mockDataSetupMap, mockServiceRequest);
  }

  public static void populateMapParams(Map<String, String> paramMap,
      Map<String, Object> contextObject) {
    for (Map.Entry<String, String> param : paramMap.entrySet()) {
      paramMap
          .put(param.getKey(), getActualValueForAll(getDelimiter(ContentType.JSON) ,param.getValue(), contextObject).toString());
    }
  }


  public Map<Integer, ReturnMockResponse> getParameterizedResponse(
      Map<MockRequest, MockResponse> mockDataSetupMap,
      MockServiceRequest mockServiceRequest) {
    Map<Integer, ReturnMockResponse> matchResponse = null;
    for (Map.Entry<MockRequest, MockResponse> entry : mockDataSetupMap.entrySet()) {
      Map map = processParamComparison(entry, mockServiceRequest);
      if (map != null) {
        return map;
      }
    }
    return matchResponse;
  }

  private Map processParamComparison(Entry<MockRequest, MockResponse> entry,
      MockServiceRequest mockServiceRequest) {
    if (ResponseProcessType.PARAMS.name().equalsIgnoreCase(entry.getKey().getType())) {
      JSONArray paramArray = new JSONArray(entry.getKey().getRule());
      for (int i = 0; i < paramArray.length(); i++) {
        try {
          HashMap context =
              new ObjectMapper().readValue(paramArray.getJSONObject(i).toString(), HashMap.class);
          if (isAnyMatchPresent(mockServiceRequest, entry, context)) {
            return getMatchingResponse(mockServiceRequest, entry, context);
          }
        } catch (Exception e) {
          log.warn(" {}", e.getMessage());
        }
      }
    }
    return null;
  }

  private boolean isAnyMatchPresent(MockServiceRequest mockServiceRequest,
      Entry<MockRequest, MockResponse> entry, HashMap context) throws JsonProcessingException {
    if (mockServiceRequest.getParams() != null &&
        mockServiceRequest.getInput() == null &&
        matchParameters(mockServiceRequest, entry, context)) {
      return true;
    }
    return performParameterized(mockServiceRequest, entry, context);
  }

  private boolean performParameterized(MockServiceRequest mockServiceRequest,
      Entry<MockRequest, MockResponse> entry, HashMap context) throws JsonProcessingException {
    if ((mockServiceRequest.getParameters() != null && !mockServiceRequest.getParameters()
        .isEmpty()) && mockServiceRequest.getInput() == null &&
        matchParameters(mockServiceRequest, entry, context)) {
      return true;
    } else if (mockServiceRequest.getParameters() != null && mockServiceRequest.getInput() != null
        &&
        (matchParameters(mockServiceRequest, entry, context) && getMatchByInput(mockServiceRequest,
            entry, context))) {
      return true;
    } else if (
        (mockServiceRequest.getParameters() == null || mockServiceRequest.getParameters().isEmpty())
            && mockServiceRequest.getInput() != null && getMatchByInput(mockServiceRequest, entry,
            context)) {
      return true;
    }
    return false;
  }

  private static SimpleEntry<String, String> getDelimiter(ContentType contentType) {
    String start_param = "<";
    String end_param = ">";
    if (ContentType.XML.equals(contentType)) {
      start_param = "{";
      end_param = "}";
    }
    return new SimpleEntry<>(start_param, end_param);
  }

  private boolean getMatchByInput(MockServiceRequest mockServiceRequest,
      Entry<MockRequest, MockResponse> entry, Map<String, Object> context)
      throws JsonProcessingException {
    Map<String, String> requestObjectMap = null;
    Map<String, String> requestActualValueParam = null;
    Map<String, String> replacedValueMap = null;
    Map.Entry<String, String> delimiter = getDelimiter(entry.getKey().getContentType());
    if(ContentType.XML.equals(entry.getKey().getContentType())) {
      List<String> existingPaths = VirtualXPaths.readXPaths(entry.getKey().getInput());
      List<String> input =  null;
      if(mockServiceRequest.getInput().toString().contains("</")) {
        input = VirtualXPaths.readXPaths(mockServiceRequest.getInput().toString());
      } else {
        input = VirtualXPaths.readXPaths(XMLConverter.objectToXML(
            mockServiceRequest.getInputObjectType(), mockServiceRequest.getInput()));
      }
      List<String> filterList =  existingPaths.stream().filter(
          x -> x.contains("{") && x
              .contains("}")).map(x -> x.substring(x.lastIndexOf(':'), x.lastIndexOf('='))).collect(
          Collectors.toList());
      List<String> filteredListWithValue = existingPaths.stream()
          .filter(  x -> x.contains("{") && x.contains("}")).
              map(x -> x.substring(x.lastIndexOf(':'))).map(x ->
              getActualValueForAll(delimiter,x, context).toString()).collect(Collectors.toList());

      List<String> matches = input.stream().filter(x -> x.lastIndexOf(':') < x.lastIndexOf('=')).filter(
          x -> filterList.contains(x.substring(x.lastIndexOf(':'), x.lastIndexOf('='))))
          .map(x -> x.substring(x.lastIndexOf(':'))).collect(
              Collectors.toList());

      return  matches.containsAll(filteredListWithValue);
    } else {
      requestObjectMap = Mapson.buildMAPsonFromJson(entry.getKey().getInput());
      if (isJSONValid(mockServiceRequest.getInput().toString())) {
        requestActualValueParam = Mapson
            .buildMAPsonFromJson(mockServiceRequest.getInput().toString());
      } else {
        requestActualValueParam = Mapson
            .buildMAPsonFromJson(
                objectMapper.writeValueAsString(mockServiceRequest.getInput()));
      }
      replacedValueMap = Mapson
          .buildMAPsonFromJson(getActualValueForAll(delimiter, entry.getKey().getInput(), context).toString());
      List<String> parameters = requestObjectMap.entrySet().stream().filter(
          x -> x.getValue().startsWith(delimiter.getKey()) && x.getValue()
              .endsWith(delimiter.getValue()))
          .map(Entry::getKey).collect(Collectors.toList());
      Map<String, String> matches = replacedValueMap.entrySet().stream().filter(
          x -> parameters.contains(x.getKey())).collect(
          Collectors.toMap(Entry::getKey, Entry::getValue));
      return requestActualValueParam.entrySet().containsAll(matches.entrySet());
    }
  }

  private boolean isJSONValid(String test) {
    try {
      new JSONObject(test);
    } catch (JSONException ex) {
      try {
        new JSONArray(test);
      } catch (JSONException ex1) {
        return false;
      }
    }
    return true;
  }

  public Map<Integer, ReturnMockResponse> getMatchingResponse(
      MockServiceRequest mockServiceRequest,
      Entry<MockRequest, MockResponse> entry, Map<String, Object> context) {
    Map<Integer, ReturnMockResponse> matchResponse;
    matchResponse = new HashMap<>();
    if(entry.getValue().getOutput() != null) {
      entry.getValue()
          .setOutput(getActualValueForAll(getDelimiter(entry.getKey().getContentType()),
              entry.getValue().getOutput(), context).toString());
    }
    final ReturnMockResponse returnMockResponse = virtualServiceValidRequest
        .returnMockResponse(mockServiceRequest,
            entry, 1);
    returnMockResponse.setExactMatch(true);
    matchResponse.put(1, returnMockResponse);
    return matchResponse;
  }

  public static Object getActualValueForAll(Map.Entry<String, String> delimiter,Object object, Map<String, Object> contextObject) {
    String key = object.toString();
    if (key.indexOf(delimiter.getKey()) != -1 && key.indexOf(delimiter.getValue()) != -1) {
      String idkey = key.substring(key.indexOf(delimiter.getKey()) + 1, key.indexOf(delimiter.getValue()));
      if (contextObject.containsKey(idkey)) {
        String prefix = delimiter.getKey().contains("{") ? "\\"  : "";
        String replaceValue = key.replaceAll(prefix+key.substring(key.indexOf(delimiter.getKey()), key.indexOf(delimiter.getValue()) + 1),
            contextObject.get(idkey).toString());
        if (replaceValue.indexOf(delimiter.getKey()) != -1 && replaceValue.indexOf(delimiter.getValue()) != -1) {
          return getActualValueForAll(delimiter, replaceValue, contextObject);
        }
        return replaceValue;
      } else {
        log.error("id key :" + idkey);
      }
    }
    return object;
  }


  public boolean matchParameters(MockServiceRequest mockServiceRequest,
      Entry<MockRequest, MockResponse> entry,
      Map<String, Object> context) {

    Map.Entry<String, String> delimiter = getDelimiter(entry.getKey().getContentType());
    List<String> requestMatchingParam = entry.getKey().getAvailableParams().stream()
        .filter(x -> x.getValue().startsWith(delimiter.getKey()) &&
            x.getValue().endsWith(delimiter.getValue()))
        .map(VirtualServiceKeyValue::getKey).collect(Collectors.toList());
    List<VirtualServiceKeyValue> replacedParamMap = new ArrayList<>();
    for (VirtualServiceKeyValue param : entry.getKey().getAvailableParams()) {
      replacedParamMap.add(new VirtualServiceKeyValue(param.getKey(),
          getActualValueForAll(delimiter, param.getValue(), context).toString()));
    }
    boolean matches = replacedParamMap.stream().filter(
        x -> requestMatchingParam.contains(x.getKey())).allMatch(
        y -> getObjectValue(mockServiceRequest.getParams().get(y.getKey()), y.getValue()));
    return !requestMatchingParam.isEmpty() && matches;
  }

  public boolean getObjectValue(Object actual, Object object) {
    try {
      Param param = new Param();
      param.setType(actual.getClass());
      param.setActualValue(actual.toString());
      param.setExpectedValue(object.toString());
      return ParamTypes.fromString(object.getClass().getCanonicalName()).compareParam(param);
    } catch (ClassCastException e) {
      return false;
    }
  }

  public Map<Integer, ResponseParam> handleParameterizedRequest(
      Map<MockRequest, MockResponse> mockDataSetupMap,
      MockServiceRequest mockServiceRequest) {
    Map<Integer, ResponseParam> responseMap = new HashMap<>();
    if (mockServiceRequest.getRule() != null) {
      JSONArray paramArray = new JSONArray(mockServiceRequest.getRule().toString());
      for (int i = 0; i < paramArray.length(); i++) {
        ResponseParam response = new ResponseParam();
        try {
          JSONObject map = paramArray.getJSONObject(i);
          Map<String, Object> context = map.toMap();
          checkRequest(mockDataSetupMap, mockServiceRequest, response, context);
          checkResponse(mockServiceRequest, response, context);
        } catch (Exception e) {
          response.getRecords().put("error", e.getMessage());
        }
        if (!response.getRecords().isEmpty()) {
          responseMap.put(i, response);
        }
      }
    } else {
      ResponseParam response = new ResponseParam();
      response.getRecords().put("error", "rule data is missing");
      responseMap.put(0, response);
    }
    return responseMap;
  }

  public void checkResponse(MockServiceRequest mockServiceRequest, ResponseParam response,
      Map<String, Object> context) throws InvalidMockResponseException {
    if (mockServiceRequest.getOutput() != null) {
      mockServiceRequest
          .setOutput(getActualValueForAll(getDelimiter(mockServiceRequest.getContentType()), mockServiceRequest.getOutput(), context));
      VirtualServiceRequest request = new VirtualServiceRequest();
      BeanUtils.copyProperties(mockServiceRequest, request);
      boolean isValidResponse = virtualServiceUtil.isMockResponseBodyValid(request);
      if (!isValidResponse) {
        response.getRecords().put(
            "response", "Invalid response!");
      }
    }
  }

  public Map<Integer, ReturnMockResponse> checkRequest(
      Map<MockRequest, MockResponse> mockDataSetupMap,
      MockServiceRequest mockServiceRequest, ResponseParam response, Map<String, Object> context)
      throws IOException, JAXBException {

    Map<Integer, ReturnMockResponse> returnMockResponseMap = new HashMap<>();

    boolean checkIfExists = false;
    if (mockServiceRequest.getParams() != null && mockServiceRequest.getInput() == null){
      populateMapParams(mockServiceRequest.getParams(), context);
      returnMockResponseMap = virtualServiceValidRequest
          .validForParam(mockDataSetupMap, mockServiceRequest);
      checkIfExists  = returnMockResponseMap == null || returnMockResponseMap.isEmpty();
    }

    if (mockServiceRequest.getInput() != null) {
      mockServiceRequest
          .setInput(getActualValueForAll(getDelimiter(mockServiceRequest.getContentType()), mockServiceRequest.getInput(), context));
      returnMockResponseMap = virtualServiceValidRequest
          .validObject(mockDataSetupMap, mockServiceRequest);
      checkIfExists  = returnMockResponseMap == null || returnMockResponseMap.isEmpty();
    }
    if (checkIfExists && mockDataSetupMap.entrySet().stream().
        anyMatch(x -> ResponseProcessType.PARAMS.name()
            .equalsIgnoreCase(x.getKey().getType().toLowerCase()))) {
      returnMockResponseMap = getParameterizedResponse(mockDataSetupMap, mockServiceRequest);
    }

    if (returnMockResponseMap != null && !returnMockResponseMap.isEmpty()) {
      response.getRecords()
          .put("request", "Mock already Present!");
    }
    return returnMockResponseMap;
  }

}
