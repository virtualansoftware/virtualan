/*
 *
 * Copyright 2018 Virtualan Contributors (https://virtualan.io)
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

package io.virtualan.controller;

import com.cedarsoftware.util.io.JsonObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.virtualan.autoconfig.ApplicationContextProvider;
import io.virtualan.core.InvalidMockResponseException;
import io.virtualan.core.VirtualParameterizedUtil;
import io.virtualan.core.VirtualServiceInfo;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.*;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.OpenApiGeneratorUtil;
import io.virtualan.core.util.VirtualanClassLoader;
import io.virtualan.core.util.VirtualanConfiguration;
import io.virtualan.core.util.rule.RuleEvaluator;
import io.virtualan.core.util.rule.ScriptExecutor;
import io.virtualan.requestbody.RequestBodyTypes;
import io.virtualan.service.VirtualService;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * This is a entry point to to record mock data in the Virtualan.
 * <p>
 * Virtualan-UI and Virtualan-OpenAPI would interact through this web services.
 * </p>
 *
 * @author Elan Thangamani
 */
@RestController("virtualServiceController")
@Slf4j
public class VirtualServiceController {

  /**
   * The constant VS_REQUEST_BODY_MISMATCH.
   */
  public static final String VS_REQUEST_BODY_MISMATCH = "VS_REQUEST_BODY_MISMATCH";
  @Autowired
  private static ConfigurableApplicationContext context;
  /**
   * The Locale.
   */
  Locale locale = LocaleContextHolder.getLocale();
  @Autowired
  private RuleEvaluator ruleEvaluator;
  @Autowired
  private ScriptExecutor scriptExecutor;
  @Autowired
  private Converter converter;
  @Autowired
  private VirtualService virtualService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private VirtualServiceUtil virtualServiceUtil;
  @Value("${virtualan.application.name:Mock Service}")
  private String applicationName;
  @Autowired
  private ApplicationContextProvider applicationContext;
  private File yamlFolder = VirtualanConfiguration.getYamlPath();
  @Autowired
  private OpenApiGeneratorUtil openApiGeneratorUtil;
  @Autowired
  private VirtualParameterizedUtil virtualParameterizedUtil;

  @Value("${virtualan.script.enabled:false}")
  private boolean scriptEnabled;


  private ObjectMapper getObjectMapper() {
    objectMapper.findAndRegisterModules();
    objectMapper.setSerializationInclusion(Include.NON_NULL);

    return objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
    );
  }

  /**
   * Gets virtual service.
   *
   * @return the virtual service
   */
  public VirtualService getVirtualService() {
    return virtualService;
  }

  /**
   * Sets virtual service.
   *
   * @param virtualService the virtual service
   */
  public void setVirtualService(VirtualService virtualService) {
    this.virtualService = virtualService;
  }

  /**
   * Gets virtual service info.
   *
   * @return the virtual service info
   */
  public VirtualServiceInfo getVirtualServiceInfo() {
    return virtualServiceUtil.getVirtualServiceInfo();
  }

  /**
   * Application name string.
   *
   * @return the string
   */
  @GetMapping(value = "/virtualservices/app-name")
  public String applicationName() {
    return "{\"appName\":\"" + applicationName + "\"}";
  }

  /**
   * List all mock load request map.
   *
   * @return the map
   * @throws InstantiationException the instantiation exception
   * @throws IllegalAccessException the illegal access exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException            the io exception
   */
  @GetMapping(value = "/virtualservices/load")
  public Map<String, Map<String, VirtualServiceRequest>> listAllMockLoadRequest()
      throws InstantiationException, IllegalAccessException, ClassNotFoundException,
      IOException {
    return VirtualanConfiguration.getVirtualServiceRequestMap();
  }

  /**
   * List all mock load requests response entity.
   *
   * @return the response entity
   */
  @GetMapping(value = "/virtualservices")
  public ResponseEntity<List<VirtualServiceRequest>> listAllMockLoadRequests() {
    final List<VirtualServiceRequest> mockRestLoadRequests = virtualService.findAllMockRequests();
    if (mockRestLoadRequests.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    List<VirtualServiceRequest> response =
        mockRestLoadRequests.stream().map(x -> converter.convertAsJson(x))
            .collect(Collectors.toList());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Gets mock load request.
   *
   * @param id the id
   * @return the mock load request
   */
  @GetMapping(value = "/virtualservices/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VirtualServiceRequest> getMockLoadRequest(@PathVariable("id") long id) {
    VirtualServiceRequest mockLoadRequest = virtualService.findById(id);
    if (mockLoadRequest == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    mockLoadRequest = converter.convertAsJson(mockLoadRequest);
    return new ResponseEntity<>(mockLoadRequest, HttpStatus.OK);
  }

  /**
   * Create mock request response entity.
   *
   * @return the response entity
   */
  @PostMapping(value = "/virtualservices/apis")
  public Map<String, Map<String, VirtualServiceRequest>> createVirtualanApis(
      @ApiParam(value = "") @Valid @RequestPart(value = "openApiUrl", required = true) MultipartFile openApiUrl,
      @ApiParam(value = "Skip the  validation of yaml.", defaultValue = "true") @Valid @RequestPart(value = "skipValidation", required = false) String skipValidation)
      throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String dataload = openApiUrl.getOriginalFilename();
    String fileName = dataload.substring(0, dataload.lastIndexOf("."));
    File newFile = new File(yamlFolder + File.separator + fileName);
    if (!newFile.exists()) {
      newFile.mkdir();
    }
    VirtualanConfiguration.writeYaml(newFile + File.separator + dataload, openApiUrl.getInputStream());
    return openApiGeneratorUtil.generateRestApi(scriptEnabled, dataload, null, applicationContext.getClassLoader().getParent());
  }


  /**
   * remove virtual api by the name of the service.
   *
   * @return the response entity
   */
  @DeleteMapping(value = "/virtualservices/apis")
  public Map<String, Map<String, VirtualServiceRequest>> deleteVirtualanApis(
          @Valid @RequestParam(value = "apiName", required = false) String apiName)
          throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, IntrospectionException {
    return openApiGeneratorUtil.removeRestApi(scriptEnabled, apiName, null, applicationContext.getClassLoader().getParent());
  }


  /**
   * Create mock request response entity.
   *
   * @param virtualServiceRequest the virtual service request
   * @return the response entity
   */
  @PostMapping(value = "/virtualservices")
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceRequest virtualServiceRequest) {
    try {

      if(!scriptEnabled && virtualServiceRequest.getType() != null && (ResponseProcessType.SCRIPT.toString().equalsIgnoreCase(virtualServiceRequest.getType().toString())
              || ResponseProcessType.RULE.toString().equalsIgnoreCase(virtualServiceRequest.getType().toString()))) {
        return new ResponseEntity<>(
                "{\"message\":\""+messageSource.getMessage("VS_VALIDATION_FAILURE_REJECT", null, locale)+"\"}",
                null, HttpStatus.BAD_REQUEST);
      } else if (virtualServiceRequest.getType() == null) {
        virtualServiceRequest.setType(ResponseProcessType.RESPONSE.toString());
      }
      converter.convertJsonAsString(virtualServiceRequest);
      virtualServiceRequest.setRequestType(RequestType.REST.toString());
      validateExpectedInput(virtualServiceRequest);
      // find the operationId for the given Request. It required for the Automation test cases
      virtualServiceUtil.findOperationIdForService(virtualServiceRequest);

      if ("PARAMS".equalsIgnoreCase(virtualServiceRequest.getType())) {
        Map response = virtualParameterizedUtil.handleParameterizedRequest(virtualServiceRequest);
        if (!response.isEmpty()) {
          final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
              messageSource.getMessage("VS_PARAMS_DATA_ALREADY_EXISTS", null, locale));
          virtualServiceRequest = converter.convertAsJson(virtualServiceRequest);
          virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
          virtualServiceStatus.setResponseParam(response);
          return new ResponseEntity<>(virtualServiceStatus,
              HttpStatus.BAD_REQUEST);
        }
      } else {
        ResponseEntity responseEntity = validateRequestBody(virtualServiceRequest);
        if (responseEntity != null) {
          return responseEntity;
        } else {
          responseEntity = validateResponseBody(virtualServiceRequest);
          if (responseEntity != null) {
            return responseEntity;
          }
        }
        responseEntity = virtualServiceUtil.checkIfServiceDataAlreadyExists(virtualServiceRequest);

        if (responseEntity != null) {
          return responseEntity;
        }
      }
      VirtualServiceRequest mockTransferObject = virtualService
          .saveMockRequest(virtualServiceRequest);
      mockTransferObject = converter.convertAsJson(mockTransferObject);
      mockTransferObject.setMockStatus(
          new VirtualServiceStatus(messageSource.getMessage("VS_SUCCESS", null, locale)));
      return new ResponseEntity<>(mockTransferObject, HttpStatus.CREATED);

    } catch (final Exception e) {
      return new ResponseEntity<VirtualServiceStatus>(new VirtualServiceStatus(
          messageSource.getMessage("VS_UNEXPECTED_ERROR", null, locale) + e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }


  private ResponseEntity validateResponseBody(VirtualServiceRequest mockLoadRequest) {
    try {
      virtualServiceUtil.isMockResponseBodyValid(mockLoadRequest);
    } catch (NoSuchMessageException | InvalidMockResponseException e) {
      return new ResponseEntity<VirtualServiceStatus>(new VirtualServiceStatus(
          messageSource.getMessage("VS_RESPONSE_BODY_MISMATCH", null, locale)
              + e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  private ResponseEntity validateRequestBody(VirtualServiceRequest virtualServiceRequest) {
    if (virtualServiceUtil.getVirtualServiceInfo() != null) {

      Class inputObjectType = virtualServiceUtil.getVirtualServiceInfo()
          .getInputType(virtualServiceRequest);
      if (virtualServiceRequest.getInput() !=null &&
          VirtualanConfiguration.isValidJson(virtualServiceRequest.getInput().toString()) &&
          (inputObjectType == null || inputObjectType.isAssignableFrom(String.class))) {
        virtualServiceRequest.setInputObjectType(JsonObject.class);
        inputObjectType = JsonObject.class;
      }
      if (inputObjectType == null && (virtualServiceRequest.getInput() == null
          || virtualServiceRequest.getInput().toString().length() == 0)) {
        return null;
      } else if (virtualServiceRequest.getInput() != null
          && virtualServiceRequest.getInput().toString().length() > 0 && inputObjectType != null) {
        return getResponseEntity(virtualServiceRequest, inputObjectType);
      }
    }
    return null;
  }

  private ResponseEntity getResponseEntity(VirtualServiceRequest virtualServiceRequest,
      Class inputObjectType) {
    final io.virtualan.requestbody.RequestBody requestBody =
        new io.virtualan.requestbody.RequestBody();
    requestBody.setObjectMapper(getObjectMapper());
    requestBody.setInputRequest(virtualServiceRequest.getInput().toString());
    requestBody.setInputObjectType(inputObjectType);
    Object object = getInputObject(inputObjectType, requestBody);
    if (object == null) {
      return new ResponseEntity<VirtualServiceStatus>(
          new VirtualServiceStatus(messageSource
              .getMessage(VS_REQUEST_BODY_MISMATCH, null, locale)),
          HttpStatus.BAD_REQUEST);
    }

    if ("RULE".equalsIgnoreCase(virtualServiceRequest.getType())) {
      return getRuleEntity(virtualServiceRequest, inputObjectType,
          requestBody);

    } else if ("SCRIPT".equalsIgnoreCase(virtualServiceRequest.getType())) {
      return getScriptResponseEntity(virtualServiceRequest,
          inputObjectType, requestBody);
    }
    return null;
  }

  private ResponseEntity getScriptResponseEntity(VirtualServiceRequest virtualServiceRequest,
      Class inputObjectType, io.virtualan.requestbody.RequestBody requestBody) {
    try {
      MockServiceRequest mockServiceRequest = new MockServiceRequest();
      Object inputObject = getInputObject(inputObjectType, requestBody);
      mockServiceRequest.setInput(inputObject);
      mockServiceRequest.setParams(Converter.converter(virtualServiceRequest.getAvailableParams()));
      MockResponse mockResponse = new MockResponse();
      mockResponse = scriptExecutor
          .executeScript(mockServiceRequest, mockResponse, virtualServiceRequest.getRule());
      if (mockResponse == null) {
        return new ResponseEntity<VirtualServiceStatus>(
            new VirtualServiceStatus("Its not a valid mock response setup!!! Verify the script? ",
                messageSource
                    .getMessage(VS_REQUEST_BODY_MISMATCH, null, locale)),
            HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return new ResponseEntity<VirtualServiceStatus>(
          new VirtualServiceStatus(e.getMessage(), messageSource
              .getMessage(VS_REQUEST_BODY_MISMATCH, null, locale)),
          HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  private ResponseEntity getRuleEntity(VirtualServiceRequest virtualServiceRequest,
      Class inputObjectType, io.virtualan.requestbody.RequestBody requestBody) {
    Object object;
    try {
      MockServiceRequest mockServiceRequest = new MockServiceRequest();
      object = getInputObject(inputObjectType, requestBody);
      mockServiceRequest.setInput(object);
      mockServiceRequest.setParams(Converter.converter(virtualServiceRequest.getAvailableParams()));
      ruleEvaluator
          .expressionEvaluatorForMockCreation(mockServiceRequest, virtualServiceRequest.getRule());
    } catch (Exception e) {
      return new ResponseEntity<VirtualServiceStatus>(
          new VirtualServiceStatus(e.getMessage(), messageSource
              .getMessage(VS_REQUEST_BODY_MISMATCH, null, locale)),
          HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  private Object getInputObject(Class inputObjectType,
      io.virtualan.requestbody.RequestBody requestBody) {
    Object object;
    try {
      object = RequestBodyTypes.fromString(inputObjectType.getTypeName())
          .getValidMockRequestBody(requestBody);
    } catch (NoSuchMessageException | IOException e) {
      object = null;
    }
    return object;
  }


  private ResponseEntity validateExpectedInput(VirtualServiceRequest mockLoadRequest) {
    if (mockLoadRequest.getHttpStatusCode() == null || mockLoadRequest.getMethod() == null
        || mockLoadRequest.getType() == null
        || mockLoadRequest.getUrl() == null) {
      return new ResponseEntity<VirtualServiceStatus>(
          new VirtualServiceStatus(
              messageSource.getMessage("VS_CREATE_MISSING_INFO", null, locale)),
          HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  /**
   * Update mock request response entity.
   *
   * @param id              the id
   * @param mockLoadRequest the mock load request
   * @return the response entity
   */
  @PutMapping(value = "/virtualservices/{id}")
  public ResponseEntity<VirtualServiceRequest> updateMockRequest(@PathVariable("id") long id,
      @RequestBody VirtualServiceRequest mockLoadRequest)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException {

    final VirtualServiceRequest currentMockLoadRequest = virtualService.findById(id);
    if (currentMockLoadRequest == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // find the operationId for the given Request. It required for the Automation test cases
    virtualServiceUtil.findOperationIdForService(mockLoadRequest);

    currentMockLoadRequest.setInput(mockLoadRequest.getInput());
    currentMockLoadRequest.setOutput(mockLoadRequest.getOutput());
    currentMockLoadRequest.setOperationId(mockLoadRequest.getOperationId());

    virtualService.updateMockRequest(currentMockLoadRequest);
    return new ResponseEntity<>(currentMockLoadRequest, HttpStatus.OK);
  }


  /**
   * Delete mock request response entity.
   *
   * @param id the id
   * @return the response entity
   */
  @DeleteMapping(value = "/virtualservices/{id}")
  public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
    final VirtualServiceRequest mockLoadRequest = virtualService.findById(id);
    if (mockLoadRequest == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    virtualService.deleteMockRequestById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  /**
   * Read catalog response entity.
   *
   * @return the response entity
   */
  @GetMapping(value = "/api-catalogs")
  public ResponseEntity<List<String>> readCatalog() {
    final Set<String> fileList = new HashSet<>();
    List<String> lists = Arrays
        .asList("classpath:META-INF/resources/yaml/*", "classpath:META-INF/resources/wsdl/*", "classpath:yaml/*");
    fileList.add("VirtualService");
    for (String pathName : lists) {
      try {
        final Resource[] resources = getCatalogList(pathName);
        for (final Resource file : resources) {
            fileList.add(file.getFilename());
        }
      } catch (Exception e) {
        log.error("api-catalogs : {}", e.getMessage());
      }
    }
    if (fileList.isEmpty()) {
      log.error("Api-catalogs List was not available : ");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(new LinkedList<>(fileList.stream().sorted().collect(
          Collectors.toList())), HttpStatus.OK);
    }
  }


  /**
   * Read catalog response entity.
   *
   * @param name the name
   * @return the response entity
   */
  @GetMapping(value = "/api-catalogs/{name}")
  public ResponseEntity<List<String>> readCatalog(@PathVariable("name") String name) {
    final List<String> fileList = new LinkedList<>();
    try {
      if ("VirtualService".equalsIgnoreCase(name)) {
        fileList.add("virtualservices.yaml");
      }

      for (final Resource file : getCatalogs(name)) {
        fileList.add(file.getFilename());
      }

    } catch (final IOException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (fileList.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(fileList, HttpStatus.OK);
    }
  }


  private Resource[] getCatalogs(String name) throws IOException {
    final PathMatchingResourcePatternResolver resolver =
        new PathMatchingResourcePatternResolver(this.applicationContext.getClassLoader().getParent().getParent());
    Resource[] resources =  resolver
        .getResources("classpath:META-INF/resources/**/" + name + "/*.*");
    if (resources == null || resources.length ==0){
      resources =  resolver
              .getResources("classpath:yaml/" + name + "/*.*");
    }
    return resources;
  }

  private Resource[] getCatalogList(String path) throws IOException {
    final PathMatchingResourcePatternResolver resolver =
        new PathMatchingResourcePatternResolver(applicationContext.getClassLoader().getParent().getParent());
    return resolver.getResources(path);
  }

}
