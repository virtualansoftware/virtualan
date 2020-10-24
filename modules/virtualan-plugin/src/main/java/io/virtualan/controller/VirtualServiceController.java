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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.virtualan.core.model.RequestType;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.rule.RuleEvaluator;
import io.virtualan.core.util.rule.ScriptExecutor;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.core.InvalidMockResponseException;
import io.virtualan.core.VirtualServiceInfo;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.model.VirtualServiceStatus;
import io.virtualan.requestbody.RequestBodyTypes;
import io.virtualan.service.VirtualService;


/**
 * This is a entry point to to record mock data in the Virtualan.
 *
 * Virtualan-UI and Virtualan-OpenAPI would interact through this web services.
 *
 * @author Elan Thangamani
 *
 **/
@RestController("virtualServiceController")
public class VirtualServiceController {

    private static final Logger log = LoggerFactory.getLogger(VirtualServiceController.class);

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


    Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private VirtualServiceUtil virtualServiceUtil;

    private ObjectMapper getObjectMapper() {
        objectMapper.findAndRegisterModules();
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        return objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            // ,DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
        );
    }


    public VirtualService getVirtualService() {
        return virtualService;
    }

    public void setVirtualService(VirtualService virtualService) {
        this.virtualService = virtualService;
    }

    public VirtualServiceInfo getVirtualServiceInfo() {
        return virtualServiceUtil.getVirtualServiceInfo();
    }

    @Value("${virtualan.application.name:Mock Service}")
    private String applicationName;

    @RequestMapping(value = "/virtualservices/app-name", method = RequestMethod.GET)
    public String applicationName() {
        return "{\"appName\":\"" + applicationName + "\"}";
    }

    @RequestMapping(value = "/virtualservices/load", method = RequestMethod.GET)
    public Map<String, Map<String, VirtualServiceRequest>> listAllMockLoadRequest()
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        IOException {
        return virtualServiceUtil.getVirtualServiceInfo() != null ? virtualServiceUtil.getVirtualServiceInfo().loadVirtualServices()
            : new HashMap<>();
    }


    @RequestMapping(value = "/virtualservices", method = RequestMethod.GET)
    public ResponseEntity<List<VirtualServiceRequest>> listAllMockLoadRequests() {
        final List<VirtualServiceRequest> mockRestLoadRequests = virtualService.findAllMockRequests();
        if (mockRestLoadRequests.isEmpty()) {
            return new ResponseEntity<List<VirtualServiceRequest>>(HttpStatus.NO_CONTENT);
        }
        List<VirtualServiceRequest> response =
            mockRestLoadRequests.stream().map(x  -> converter.convertAsJson(x)).collect(Collectors.toList());
        return new ResponseEntity<List<VirtualServiceRequest>>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/virtualservices/{id}", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VirtualServiceRequest> getMockLoadRequest(@PathVariable("id") long id)
        throws JsonProcessingException {
        VirtualServiceRequest mockLoadRequest = virtualService.findById(id);
        if (mockLoadRequest == null) {
            return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NOT_FOUND);
        }
        mockLoadRequest = converter.convertAsJson(mockLoadRequest);
        return new ResponseEntity<VirtualServiceRequest>(mockLoadRequest, HttpStatus.OK);
    }

    @RequestMapping(value = "/virtualservices", method = RequestMethod.POST)
    public ResponseEntity createMockRequest(
        @RequestBody VirtualServiceRequest virtualServiceRequest) {
        try {
            converter.convertJsonAsString(virtualServiceRequest);
            virtualServiceRequest.setRequestType(RequestType.REST.toString());
            validateExpectedInput(virtualServiceRequest);
            // find the operationId for the given Request. It required for the Automation test cases
            virtualServiceUtil.findOperationIdForService(virtualServiceRequest);
            ResponseEntity responseEntity  = validateRequestBody(virtualServiceRequest);
            if (responseEntity != null) {
                return responseEntity;
            }  else {
                responseEntity = validateResponseBody(virtualServiceRequest);
                if (responseEntity != null) {
                    return responseEntity;
                }
            }
            responseEntity = virtualServiceUtil.checkIfServiceDataAlreadyExists(virtualServiceRequest);

            if (responseEntity != null) {
                return responseEntity;
            }

            VirtualServiceRequest mockTransferObject = virtualService.saveMockRequest(virtualServiceRequest);
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
            final Class inputObjectType = virtualServiceUtil.getVirtualServiceInfo().getInputType(virtualServiceRequest);
            if (inputObjectType == null && (virtualServiceRequest.getInput() == null
                || virtualServiceRequest.getInput().toString().length() == 0)) {
                return null;
            } else if (virtualServiceRequest.getInput() != null
                && virtualServiceRequest.getInput().toString().length() > 0 && inputObjectType != null) {
                final io.virtualan.requestbody.RequestBody requestBody =
                    new io.virtualan.requestbody.RequestBody();
                requestBody.setObjectMapper(getObjectMapper());
                requestBody.setInputRequest(virtualServiceRequest.getInput().toString());
                requestBody.setInputObjectType(inputObjectType);
                Object object = null;
                try {
                    object = RequestBodyTypes.fromString(inputObjectType.getTypeName())
                        .getValidMockRequestBody(requestBody);
                } catch (NoSuchMessageException | IOException e) {
                    e.printStackTrace();
                    object = null;
                }
                if (object == null) {
                    return new ResponseEntity<VirtualServiceStatus>(
                        new VirtualServiceStatus(messageSource
                            .getMessage("VS_REQUEST_BODY_MISMATCH", null, locale)),
                        HttpStatus.BAD_REQUEST);
                }

                if ("RULE".equalsIgnoreCase(virtualServiceRequest.getType())) {
                    try {
                        MockServiceRequest mockServiceRequest = new MockServiceRequest();
                        try {
                            object = RequestBodyTypes.fromString(inputObjectType.getTypeName())
                                .getValidMockRequestBody(requestBody);
                        } catch (NoSuchMessageException | IOException e) {
                            e.printStackTrace();
                            object = null;
                        }
                        mockServiceRequest.setInput(object);
                        mockServiceRequest.setParams(Converter.converter(virtualServiceRequest.getAvailableParams()));
                        ruleEvaluator.expressionEvaluatorForMockCreation(mockServiceRequest, virtualServiceRequest.getRule());
                    } catch (Exception e) {
                        return new ResponseEntity<VirtualServiceStatus>(
                            new VirtualServiceStatus(e.getMessage(), messageSource
                                .getMessage("VS_REQUEST_BODY_MISMATCH", null, locale)),
                            HttpStatus.BAD_REQUEST);
                    }

                } else if ("SCRIPT".equalsIgnoreCase(virtualServiceRequest.getType())) {
                    try {
                        MockServiceRequest mockServiceRequest = new MockServiceRequest();
                        try {
                            object = RequestBodyTypes.fromString(inputObjectType.getTypeName())
                                .getValidMockRequestBody(requestBody);
                        } catch (NoSuchMessageException | IOException e) {
                            e.printStackTrace();
                            object = null;
                        }
                        mockServiceRequest.setInput(object);
                        mockServiceRequest.setParams(Converter.converter(virtualServiceRequest.getAvailableParams()));
                        MockResponse mockResponse = new MockResponse();
                        mockResponse = scriptExecutor.executeScript (mockServiceRequest, mockResponse, virtualServiceRequest.getRule());
                        if(mockResponse == null){
                            return new ResponseEntity<VirtualServiceStatus>(
                                new VirtualServiceStatus("Its not a valid mock response setup!!! Verify the script? ", messageSource
                                    .getMessage("VS_REQUEST_BODY_MISMATCH", null, locale)),
                                HttpStatus.BAD_REQUEST);
                        } else {
                            //Validate Mock Set up data for script and rule
                        }
                    } catch (Exception e) {
                        return new ResponseEntity<VirtualServiceStatus>(
                            new VirtualServiceStatus(e.getMessage(), messageSource
                                .getMessage("VS_REQUEST_BODY_MISMATCH", null, locale)),
                            HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return null;
    }



    private ResponseEntity validateExpectedInput(VirtualServiceRequest mockLoadRequest) {
        if (mockLoadRequest.getHttpStatusCode() == null || mockLoadRequest.getMethod() == null || mockLoadRequest.getType() == null
            || mockLoadRequest.getUrl() == null) {
            return new ResponseEntity<VirtualServiceStatus>(
                new VirtualServiceStatus(
                    messageSource.getMessage("VS_CREATE_MISSING_INFO", null, locale)),
                HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @RequestMapping(value = "/virtualservices/{id}", method = RequestMethod.PUT)
    public ResponseEntity<VirtualServiceRequest> updateMockRequest(@PathVariable("id") long id,
        @RequestBody VirtualServiceRequest mockLoadRequest) {

        final VirtualServiceRequest currentMockLoadRequest = virtualService.findById(id);
        if (currentMockLoadRequest == null) {
            return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NOT_FOUND);
        }

        // find the operationId for the given Request. It required for the Automation test cases
        virtualServiceUtil.findOperationIdForService(mockLoadRequest);

        currentMockLoadRequest.setInput(mockLoadRequest.getInput());
        currentMockLoadRequest.setOutput(mockLoadRequest.getOutput());
        currentMockLoadRequest.setOperationId(mockLoadRequest.getOperationId());

        virtualService.updateMockRequest(currentMockLoadRequest);
        return new ResponseEntity<VirtualServiceRequest>(currentMockLoadRequest, HttpStatus.OK);
    }


    @RequestMapping(value = "/virtualservices/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
        final VirtualServiceRequest MockLoadRequest = virtualService.findById(id);
        if (MockLoadRequest == null) {
            return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NOT_FOUND);
        }
        virtualService.deleteMockRequestById(id);
        return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/api-catalogs", method = RequestMethod.GET)
    public ResponseEntity<List<String>> readCatalog() {
        final Set<String> fileList = new HashSet<>();
        try {
            List<String> lists  = Arrays.asList("classpath:META-INF/resources/yaml/*/");
            fileList.add("VirtualService");
            for(String pathName  :  lists){
                final Resource[] resources = getCatalogList(pathName);
                for (final Resource file : resources) {
                    final String[] names = file.toString().split("/");
                    if (names.length > 1) {
                        fileList.add(names[names.length - 2]);
                    }
                }
            }
        } catch (final IOException e) {
            VirtualServiceController.log.error("api-catalogs : " + e.getMessage());
            return new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND);
        }
        if (fileList.isEmpty()) {
            VirtualServiceController.log.error("Api-catalogs List was not available : ");
            return new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<List<String>>(new LinkedList<>(fileList.stream().sorted().collect(
                Collectors.toList())), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/api-catalogs/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> readCatalog(@PathVariable("name") String name) {
        final List<String> fileList = new LinkedList<>();
        try {
            if("VirtualService".equalsIgnoreCase(name)){
                fileList.add("virtualservices.yaml");
            }

            for (final Resource file : getCatalogs(name)) {
                fileList.add(file.getFilename());
            }

        } catch (final IOException e) {
            return new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND);
        }
        if (fileList.isEmpty()) {
            return new ResponseEntity<List<String>>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<List<String>>(fileList, HttpStatus.OK);
        }
    }


    private Resource[] getCatalogs(String name) throws IOException {
        final ClassLoader classLoader = MethodHandles.lookup().getClass().getClassLoader();
        final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver(classLoader);
        return resolver.getResources("classpath:META-INF/resources/**/" + name + "/*.*");
    }

    private Resource[] getCatalogList(String path) throws IOException {
        final ClassLoader classLoader = MethodHandles.lookup().getClass().getClassLoader();

        final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver(classLoader);
        return resolver.getResources(path);
    }


}
