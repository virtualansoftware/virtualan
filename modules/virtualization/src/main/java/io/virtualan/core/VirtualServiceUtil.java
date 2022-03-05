/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.core;


import com.cedarsoftware.util.io.JsonObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.api.VirtualServiceType;
import io.virtualan.api.WSResource;
import io.virtualan.autoconfig.ApplicationContextProvider;
import io.virtualan.core.model.ContentType;
import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.model.ResponseParam;
import io.virtualan.core.model.ResponseProcessType;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.model.VirtualServiceStatus;
import io.virtualan.core.soap.SoapFaultException;
import io.virtualan.core.util.*;
import io.virtualan.core.util.rule.ScriptExecutor;
import io.virtualan.custom.message.ResponseException;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.requestbody.RequestBody;
import io.virtualan.requestbody.RequestBodyTypes;
import io.virtualan.service.VirtualService;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * This class is base utility service class to perform all virtual service operations
 *
 * @author Elan Thangamani
 */

@Service("virtualServiceUtil")
@Slf4j
public class VirtualServiceUtil {


  private final Locale locale = LocaleContextHolder.getLocale();
  @Autowired
  private VirtualServiceValidRequest virtualServiceValidRequest;

  @Value("${virtualan.script.enabled:false}")
  private boolean scriptEnabled;

  @Autowired
  private VirtualService virtualService;
  @Autowired
  private ScriptExecutor scriptExecutor;
  @Autowired
  private Converter converter;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private VirtualServiceParamComparator virtualServiceParamComparator;
  @Autowired
  @Lazy
  private OpenApiGeneratorUtil openApiGeneratorUtil;
  @Autowired
  private ApplicationContextProvider applicationContext;
  @Autowired
  @Lazy
  private MessageUtil messageUtil;

  @Autowired
  @Lazy
  private VirtualParameterizedUtil virtualParameterizedUtil;

  @Autowired
  private XMLConverter xmlConverter;
  @Autowired
  private ObjectMapper objectMapper;
  private VirtualServiceType virtualServiceType;
  @Autowired
  private VirtualServiceInfoFactory virtualServiceInfoFactory;
  private VirtualServiceInfo virtualServiceInfo;

  public static Object getActualValue(Object object, Map<String, Object> contextObject) {
    String key = object.toString();
    if (key.indexOf('<') != -1) {
      String idkey = key.substring(key.indexOf('<') + 1, key.indexOf('>'));
      if (contextObject.containsKey(idkey)) {
        return key.replaceAll(key.substring(key.indexOf('<'), key.indexOf('>') + 1),
            contextObject.get(idkey).toString());
      }
    }
    return object;
  }

  public VirtualServiceType getVirtualServiceType() {
    return virtualServiceType;
  }

  public void setVirtualServiceType(VirtualServiceType virtualServiceType) {
    if (virtualServiceType != null) {
      setVirtualServiceInfo(
          virtualServiceInfoFactory.getVirtualServiceInfo(virtualServiceType.getType()));
      this.virtualServiceType = virtualServiceType;
    } else {
      setVirtualServiceInfo(
          virtualServiceInfoFactory.getVirtualServiceInfo(VirtualServiceType.SPRING.getType()));
      this.virtualServiceType = VirtualServiceType.SPRING;
    }

  }

  @PostConstruct
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public void init() throws ClassNotFoundException, JsonProcessingException,
          InstantiationException, IllegalAccessException, MalformedURLException, IntrospectionException {
    setVirtualServiceType(VirtualServiceType.SPRING);

    if (getVirtualServiceType() != null) {
      //Helper.addURLToClassLoader(VirtualanConfiguration.getPath().toURI().toURL(), appContext.getClassLoader().getParent());
      virtualServiceInfo = getVirtualServiceInfo();
      virtualServiceInfo.loadVirtualServices(scriptEnabled, applicationContext.getClassLoader());
      virtualServiceInfo.setResourceParent(virtualServiceInfo.loadMapper());
    } else if (getVirtualServiceType() == null) {
      setVirtualServiceType(VirtualServiceType.NON_REST);
      virtualServiceInfo = getVirtualServiceInfo();
    }
  }

  public VirtualServiceInfo getVirtualServiceInfo() {
    return virtualServiceInfo;
  }

  private void setVirtualServiceInfo(VirtualServiceInfo virtualServiceInfo) {
    this.virtualServiceInfo = virtualServiceInfo;
  }

  private ObjectMapper getObjectMapper() {
    objectMapper.findAndRegisterModules();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    return objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        // ,DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
    );
  }

  public Map<String, String> getHttpStatusMap() {
    final Map<String, String> map = new LinkedHashMap<>();
    for (final HttpStatus status : HttpStatus.values()) {
      map.put(String.valueOf(status.value()), status.name());
    }
    return map;
  }

  public Map<MockRequest, MockResponse> readDynamicResponse(String resource, String operationId) {
    final Map<MockRequest, MockResponse> mockResponseMap = new HashMap<>();
    try {
      final List<VirtualServiceRequest> mockTransferObjectList =
          virtualService.readByOperationId(resource, operationId);
      for (final VirtualServiceRequest mockTransferObject : mockTransferObjectList) {
        final String input =
            mockTransferObject.getInput() != null ? mockTransferObject.getInput().toString() : null;
        final String output =
            mockTransferObject.getOutput() != null ? mockTransferObject.getOutput().toString()
                : null;

        Set<String> excludeSet = null;
        if (mockTransferObject.getExcludeList() != null && !mockTransferObject.getExcludeList().isEmpty() ) {
          excludeSet = new HashSet<>(
              Arrays.asList(mockTransferObject.getExcludeList().split(",")));
        }
        final MockRequest mockRequest = new MockRequest();
        mockRequest.setVirtualServiceId(mockTransferObject.getId());
        mockRequest.setUsageCount(mockTransferObject.getUsageCount());
        mockRequest.setInput(input);
        mockRequest.setContentType(mockTransferObject.getContentType());
        mockRequest.setRule(mockTransferObject.getRule());
        mockRequest.setType(mockTransferObject.getType());
        mockRequest.setExcludeSet(excludeSet);
        mockRequest.setAvailableParams(mockTransferObject.getAvailableParams());
        mockRequest.setMethod(mockTransferObject.getMethod());
        if (mockTransferObject.getType() != null && !mockTransferObject.getType().isEmpty()) {
          mockRequest.setResponseProcessType(
              ResponseProcessType.valueOf(mockTransferObject.getType().toUpperCase()));
        }
        final MockResponse mockResponse =
            new MockResponse(output, mockTransferObject.getHttpStatusCode());
        mockResponse.setHeaderParams(mockTransferObject.getHeaderParams());
        mockResponseMap.put(mockRequest, mockResponse);
      }
    } catch (final Exception e) {
      VirtualServiceUtil.log.error("Rest Mock API Response for " + operationId
          + " has not loaded : " + e.getMessage());
    }
    return mockResponseMap;
  }

  public void findOperationIdForService(VirtualServiceRequest mockLoadRequest)
      throws ClassNotFoundException, JsonProcessingException, InstantiationException, IllegalAccessException {
    if (mockLoadRequest.getOperationId() == null && virtualServiceInfo != null) {
      final String resourceUrl = mockLoadRequest.getUrl().substring(1);
      final List<String> resouceSplitterList =
          new LinkedList<>(Arrays.asList(resourceUrl.split("/")));
      if (!resouceSplitterList.isEmpty()) {
        final String operationId =
            virtualServiceInfo.getOperationId(mockLoadRequest.getMethod(),
                virtualServiceInfo.getResourceParent(), resouceSplitterList);
        if (operationId != null) {
          mockLoadRequest.setOperationId(operationId);
          mockLoadRequest.setResource(resouceSplitterList.get(0));
        } else {
          openApiGeneratorUtil.generateRestApi(scriptEnabled, null, mockLoadRequest, applicationContext.getClassLoader().getParent());
          log.warn(" Manually Resource registered " + mockLoadRequest.getMethod());
        }
      }
    }

  }

  public ResponseEntity<VirtualServiceStatus> checkIfServiceDataAlreadyExists(
      VirtualServiceRequest virtualServiceRequest) throws IOException, JAXBException {
    final Object response = isMockAlreadyExists(virtualServiceRequest);
    if (response instanceof Long) {
      final ResponseEntity<VirtualServiceStatus> virtualServiceStatus = getVirtualServiceStatusResponseEntity(
          virtualServiceRequest, (Long) response);
      if (virtualServiceStatus != null) {
        return virtualServiceStatus;
      }
    }
    return null;
  }

  public ResponseEntity<VirtualServiceStatus> getVirtualServiceStatusResponseEntity(
      VirtualServiceRequest virtualServiceRequest, Long response) {
    if (response != 0) {
      final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
          messageSource.getMessage("VS_DATA_ALREADY_EXISTS", null, locale));
      virtualServiceRequest.setId(response);
      virtualServiceRequest = converter.convertAsJson(virtualServiceRequest);
      virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
      return new ResponseEntity<>(virtualServiceStatus,
          HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  public boolean isValidJson(String jsonStr) {
    Object json = new JSONTokener(jsonStr).nextValue();
    if (json instanceof JSONObject || json instanceof JSONArray) {
      return true;
    } else {
      return false;
    }
  }

  public Object isMockAlreadyExists(VirtualServiceRequest mockTransferObject)
      throws IOException, JAXBException {

    try {
      final Map<MockRequest, MockResponse> mockDataSetupMap = readDynamicResponse(
          mockTransferObject.getResource(), mockTransferObject.getOperationId());
      MockServiceRequest mockServiceRequest = buildMockServiceRequest(mockTransferObject);

      //validate if it is a valid script
      if (mockServiceRequest.getRule() != null) {
        scriptExecutor.executeScript(mockServiceRequest, new MockResponse(),
            mockServiceRequest.getRule().toString());
      }

      if (mockServiceRequest.getInputObjectType() != null
          && mockTransferObject.getInput() != null) {
        if (isValidJson(mockTransferObject.getInput().toString()) && mockServiceRequest
            .getInputObjectType().isAssignableFrom(String.class)) {
          mockTransferObject.setInputObjectType(JsonObject.class);
        }
        mockServiceRequest.setInput(getObjectMapper()
            .readValue(mockTransferObject.getInput().toString(),
                mockServiceRequest.getInputObjectType()));

      } else if (mockTransferObject.getInput() != null) {
        mockServiceRequest.setInput(mockTransferObject.getInput().toString());
      }

      final Map<Integer, ReturnMockResponse> returnMockResponseMap =
          virtualServiceValidRequest.isResponseExists(mockDataSetupMap, mockServiceRequest);

      if (returnMockResponseMap.size() > 0) {
        return isResposeExists(mockTransferObject, mockServiceRequest.getInputObjectType(),
            mockServiceRequest,
            returnMockResponseMap);
      }


    } catch (final Exception e) {
      VirtualServiceUtil.log.error("isMockAlreadyExists :: " + e.getMessage());
      throw e;
    }
    return null;
  }

  private long isResposeExists(VirtualServiceRequest mockTransferObject,
      final Class inputObjectType, final MockServiceRequest mockServiceRequest,
      final Map<Integer, ReturnMockResponse> returnMockResponseMap)
      throws IOException, JAXBException {
    final List<ReturnMockResponse> returnMockResponseList =
        new ArrayList<>(returnMockResponseMap.values());
    Collections.sort(returnMockResponseList, new BestMatchComparator());
    VirtualServiceUtil.log.debug("Sorted list : " + returnMockResponseList);
    final ReturnMockResponse rMockResponse = returnMockResponseList.iterator().next();
    if (rMockResponse != null && rMockResponse.getHeaderResponse() != null) {
      final RequestBody requestBody = buildRequestBody(mockTransferObject, inputObjectType,
          rMockResponse);
      boolean isBodyMatch = false;
      if (inputObjectType != null) {
        isBodyMatch = RequestBodyTypes.fromString(inputObjectType.getTypeName())
            .compareRequestBody(requestBody);
      }
      return checkExistsInEachCatagory(mockTransferObject, mockServiceRequest, rMockResponse,
          isBodyMatch);
    }
    return 0;
  }

  private RequestBody buildRequestBody(VirtualServiceRequest mockTransferObject,
      final Class inputObjectType, final ReturnMockResponse rMockResponse) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setObjectMapper(getObjectMapper());
    requestBody.setExcludeList(rMockResponse.getMockRequest().getExcludeSet());
    requestBody.setExpectedInput(rMockResponse.getMockRequest().getInput());
    requestBody.setInputObjectType(inputObjectType);
    requestBody.setInputRequest(
        mockTransferObject.getInput() != null ? mockTransferObject.getInput().toString() : null);
    requestBody.setContentType(rMockResponse.getMockRequest().getContentType());
    return requestBody;
  }

  private long checkExistsInEachCatagory(VirtualServiceRequest mockTransferObject,
      final MockServiceRequest mockServiceRequest, final ReturnMockResponse rMockResponse,
      boolean isBodyMatch) {
    if (mockServiceRequest.getParams() == null || mockServiceRequest.getParams().isEmpty()) {
      return checkExistsInEachCatagoryForNoParam(mockServiceRequest, rMockResponse);
    } else if (mockServiceRequest.getParams() != null
        && mockServiceRequest.getParams().size() > 0
        && mockTransferObject.getInput() != null) {
      if (rMockResponse.getMockRequest().getAvailableParams().size() == mockServiceRequest
          .getParams().size() && isBodyMatch) {
        return checkExistsInEachCatagoryForNoParam(mockServiceRequest, rMockResponse);
      }
    } else if (mockServiceRequest.getParams() != null
        && mockServiceRequest.getParams().size() > 0) {
      return checkExistsInEachCatagoryForParam(mockServiceRequest, rMockResponse);
    } else if (mockTransferObject.getInput() != null && isBodyMatch) {
      return rMockResponse.getMockRequest().getVirtualServiceId();
    }
    return 0;
  }

  private long checkExistsInEachCatagoryForParam(final MockServiceRequest mockServiceRequest,
      final ReturnMockResponse rMockResponse) {
    if (rMockResponse.getMockRequest().getAvailableParams().size() == mockServiceRequest
        .getParams().size()
        && virtualServiceParamComparator.isAllParamPresent(mockServiceRequest,
        rMockResponse)) {
      return rMockResponse.getMockRequest().getVirtualServiceId();
    }
    return 0;
  }

  private long checkExistsInEachCatagoryForNoParam(final MockServiceRequest mockServiceRequest,
      final ReturnMockResponse rMockResponse) {
    if (virtualServiceParamComparator.isAllParamPresent(mockServiceRequest, rMockResponse)) {
      return rMockResponse.getMockRequest().getVirtualServiceId();
    }
    return 0;
  }

  // REDO - Is this validation needed?
  public boolean isMockResponseBodyValid(VirtualServiceRequest mockTransferObject)
      throws InvalidMockResponseException {
    try {
      if (ContentType.XML.equals(mockTransferObject.getContentType())) {
        XMLConverter.xmlToObject(mockTransferObject.getResponseObjectType(),
            mockTransferObject.getOutput().toString());
      } else {
        VirtualServiceRequest
            mockTransferObjectActual = virtualServiceInfo.getResponseType(mockTransferObject);
        if (mockTransferObjectActual != null && mockTransferObjectActual.getResponseType() != null
            &&
            !mockTransferObjectActual.getResponseType().isEmpty()) {
          virtualServiceValidRequest
              .validResponse(mockTransferObjectActual, mockTransferObject);
        }
      }
    } catch (final Exception e) {
      throw new InvalidMockResponseException(e);
    }
    return true;
  }

  public Map<Integer, ReturnMockResponse> validateBusinessRules(
      final Map<MockRequest, MockResponse> mockDataSetupMap,
      MockServiceRequest mockServiceRequest) {
    return virtualServiceValidRequest.validBusinessRuleForInputObject(mockDataSetupMap,
        mockServiceRequest);
  }


  MockServiceRequest buildMockServiceRequest(VirtualServiceRequest mockTransferObject) {

    MockServiceRequest mockServiceRequest = new MockServiceRequest();

    if (mockTransferObject.getInputObjectType() == null) {
      Class inputObjectType = getVirtualServiceInfo().getInputType(mockTransferObject);
      if(inputObjectType == null) {
        mockServiceRequest.setInputObjectType(JsonObject.class);
        mockTransferObject.setInputObjectType(JsonObject.class);
      }else {
        mockServiceRequest.setInputObjectType(inputObjectType);
      }
    } else {
      mockServiceRequest.setInputObjectType(mockTransferObject.getInputObjectType());
    }
    if (mockTransferObject.getInput() != null &&
        VirtualanConfiguration.isValidJson(mockTransferObject.getInput().toString()) &&
        mockServiceRequest.getInputObjectType().isAssignableFrom(String.class)) {
      mockServiceRequest.setInputObjectType(JsonObject.class);
      mockTransferObject.setInputObjectType(JsonObject.class);
    }
    mockServiceRequest.setResponseObjectType(mockTransferObject.getResponseObjectType());
    mockServiceRequest
        .setHeaderParams(Converter.converter(mockTransferObject.getHeaderParams()));
    mockServiceRequest.setOperationId(mockTransferObject.getOperationId());
    mockServiceRequest.setContentType(mockTransferObject.getContentType());
    mockServiceRequest.setType(mockTransferObject.getType());
    mockServiceRequest.setRule(mockTransferObject.getRule());
    mockServiceRequest
        .setParams(Converter.converter(mockTransferObject.getAvailableParams()));
    mockServiceRequest.setResource(mockTransferObject.getResource());
    mockServiceRequest.setInput(mockTransferObject.getInput());
    mockServiceRequest.setOutput(mockTransferObject.getOutput());
    return mockServiceRequest;
  }


  public Object returnResponse(Method method, MockServiceRequest mockServiceRequest)
      throws IOException, JAXBException {
    VirtualServiceUtil.log
        .info(" mockServiceRequest.getResource() : " + mockServiceRequest.getResource());
    final Map<MockRequest, MockResponse> mockDataSetupMap = readDynamicResponse(
        mockServiceRequest.getResource(), mockServiceRequest.getOperationId());

    //Rule Execution
    Map<Integer, ReturnMockResponse> returnMockResponseMap =
        validateBusinessRules(mockDataSetupMap, mockServiceRequest);

    if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
      returnMockResponseMap = virtualParameterizedUtil
          .getParameterizedResponse(mockDataSetupMap, mockServiceRequest);
    }

    //No Rule conditions exists/met then run the script
    if (returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
      try {
        returnMockResponseMap = virtualServiceValidRequest
            .checkScriptResponse(mockDataSetupMap, mockServiceRequest);
      } catch (ScriptErrorException e) {
        log.error("Error  in Script configuration :" + e.getMessage());
      }
    }

    //No script conditions exists/met then run the mock response
    if (returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
      returnMockResponseMap =
          virtualServiceValidRequest.isResponseExists(mockDataSetupMap, mockServiceRequest);
    }

    VirtualServiceUtil.log.debug("number of matches : {}",
        returnMockResponseMap != null ? returnMockResponseMap.size() : "Not found");
    ReturnMockResponse rMockResponse = null;
    if (returnMockResponseMap != null && !returnMockResponseMap.isEmpty()) {
      final List<ReturnMockResponse> returnMockResponseList =
          new ArrayList<>(returnMockResponseMap.values());
      Collections.sort(returnMockResponseList, new BestMatchComparator());
      VirtualServiceUtil.log.debug("Sorted list : " + returnMockResponseList);
      rMockResponse = returnMockResponseList.stream()
          .filter(ReturnMockResponse::isExactMatch).findAny().orElse(null);
      if (rMockResponse != null) {
        return getResponse(method, returnMockResponseList);
      }
    } else {
      VirtualServiceUtil.log.error(
          " Unable to find matching for the given request >>> " + mockServiceRequest);
    }
    return mockResponseNotFoundorSet(method, mockDataSetupMap);
  }

  public Object getResponse(Method method, List<ReturnMockResponse> returnMockResponseList)
      throws JAXBException {
    ReturnMockResponse rMockResponse;
    ResponseEntity<String> responseEntity;
    rMockResponse = returnMockResponseList.iterator().next();
    if (WSResource.isExists(method)) {
      return returnSoapResponse(method, rMockResponse);
    }
    if (rMockResponse.getHeaderResponse() != null) {
      responseEntity = buildResponseEntity(rMockResponse.getMockResponse(),
          rMockResponse.getHeaderResponse());
    } else {
      responseEntity = buildResponseEntity(rMockResponse.getMockResponse(), null);
    }
    virtualService.updateUsageTime(rMockResponse.getMockRequest());
    return returnResponse(method, responseEntity, responseEntity.getBody());
  }


  private Object returnSoapResponse(Method method, ReturnMockResponse rMockResponse)
      throws JAXBException {
    if (rMockResponse.getMockResponse().getOutput() != null) {
      if (ContentType.XML.equals(rMockResponse.getMockRequest().getContentType())) {
        return XMLConverter
            .xmlToObject(method.getReturnType(), rMockResponse.getMockResponse().getOutput());
      }
      Type mySuperclass = null;

      try {
        mySuperclass = method.getGenericReturnType();
        return this.objectMapper.readValue(rMockResponse.getMockResponse().getOutput(),
            this.objectMapper.constructType(mySuperclass));
      } catch (Exception ex) {
        log.error(" GenericReturnType  >>> mySuperclass " + mySuperclass);
        throw new SoapFaultException(
            "MOCK NOT FOUND (" + ex.getMessage() + ")  GenericReturnType  >>> mySuperclass "
                + method.getReturnType());
      }
    } else {
      throw new SoapFaultException("MOCK NOT FOUND");
    }
  }

  private Object returnResponse(Method method, ResponseEntity<String> responseEntity,
      String response) {
    VirtualServiceUtil.log.debug(" responseEntity.getHeaders() :" + responseEntity.getHeaders());
    if (response != null) {
      final String responseOut = xmlConverter.returnAsXml(method, responseEntity, response);
      if (method.getReturnType().equals(ResponseParam.class)) {
        return Response.status(responseEntity.getStatusCode().value()).entity(responseOut)
            .build();
      } else if (method.getReturnType().equals(ResponseEntity.class)) {
        return new ResponseEntity<>(responseOut, responseEntity.getHeaders(),
            responseEntity.getStatusCode());
      }
      Type mySuperclass = null;
      try {
        mySuperclass = method.getGenericReturnType();
        objectMapper.readValue(response, objectMapper.constructType(mySuperclass));
        return objectMapper.readValue(response, objectMapper.constructType(mySuperclass));
      } catch (final Exception e) {
        VirtualServiceUtil.log
            .error(" GenericReturnType  >>> mySuperclass " + mySuperclass);
      }
    }

    if (WSResource.isExists(method)) {
      String faultMsg = "MOC SERVER ERROR";
      if (responseEntity.getBody() != null) {
        faultMsg = responseEntity.getBody();
      }
      throw new SoapFaultException(faultMsg);
    }
    final ResponseException responseException = new ResponseException();
    if (VirtualServiceType.CXF_JAX_RS.compareTo(getVirtualServiceType()) == 0) {
      responseException
          .setResponse(Response.status(responseEntity.getStatusCode().value())
              .entity(responseEntity.getBody()).build());
      throw new WebApplicationException(responseException.getResponse());
    } else if (VirtualServiceType.SPRING.compareTo(getVirtualServiceType()) == 0 || true) {
      responseException.setResponseEntity(responseEntity);
      throw responseException;
    }
    return null;
  }


  // covert response as XML if the accept is xml
  private Object mockResponseNotFoundorSet(Method method,
      Map<MockRequest, MockResponse> mockDataSetupMap) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON); // TO-DO
    if (mockDataSetupMap.size() > 0) {
      return returnResponse(method,
          new ResponseEntity<>(
              messageSource.getMessage("VS_RESPONSE_NOT_FOUND", null, locale),
              headers, HttpStatus.INTERNAL_SERVER_ERROR),
          null);
    } else {
      VirtualServiceUtil.log.error("Mock Response was not defined for the given input");
      return returnResponse(method,
          new ResponseEntity<>(messageSource.getMessage("VS_DATA_NOT_SET", null, locale),
              headers, HttpStatus.INTERNAL_SERVER_ERROR),
          null);
    }
  }

  private ResponseEntity<String> buildResponseEntity(MockResponse mockResponse,
      Map<String, String> headerMap) {
    final HttpHeaders headers = buildHeader(mockResponse, headerMap);
    return new ResponseEntity<>(mockResponse.getOutput(), headers,
        HttpStatus.valueOf(Integer.parseInt(mockResponse.getHttpStatusCode())));
  }

  private HttpHeaders buildHeader(MockResponse mockResponse, Map<String, String> headerMap) {
    final HttpHeaders headers = new HttpHeaders();
    try {
      if (MediaType.valueOf(headerMap.get("accept")).includes(MediaType.ALL)) {
        headers.setContentType(MediaType.APPLICATION_JSON);
      } else {
        headers.setContentType(MediaType.valueOf(headerMap.get("accept")));
      }
      for (final VirtualServiceKeyValue keyValuePair : mockResponse.getHeaderParams()) {
        headers.add(keyValuePair.getKey(), keyValuePair.getValue());
      }
    } catch (final Exception e) {
      log.warn("buildHeader unexpected {}", e.getMessage());
    }
    return headers;
  }
}
