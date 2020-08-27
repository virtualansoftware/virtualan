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


import io.virtualan.api.WSResource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.virtualan.api.ApiType;
import io.virtualan.core.model.*;
import javax.xml.bind.JAXBException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.api.VirtualServiceType;
import io.virtualan.core.util.BestMatchComparator;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceParamComparator;
import io.virtualan.core.util.VirtualServiceValidRequest;
import io.virtualan.core.util.XMLConverter;
import io.virtualan.custom.message.ResponseException;
import io.virtualan.requestbody.RequestBody;
import io.virtualan.requestbody.RequestBodyTypes;
import io.virtualan.service.VirtualService;
import org.springframework.ws.soap.SoapFaultException;

/**
 * This class is base utility service class to perform all virtual service operations
 *
 * @author Elan Thangamani
 *
 */

@Service("virtualServiceUtil")
public class VirtualServiceUtil {


    private static final Logger log = LoggerFactory.getLogger(VirtualServiceUtil.class);

    @Autowired
    private VirtualServiceValidRequest virtualServiceValidRequest;

    @Autowired
    private VirtualService virtualService;

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private VirtualServiceParamComparator virtualServiceParamComparator;

    @Autowired
    private XMLConverter xmlConverter;

    @Autowired
    private ObjectMapper objectMapper;

    private VirtualServiceType virtualServiceType;

    public VirtualServiceType getVirtualServiceType() {
        return virtualServiceType;
    }

    public void setVirtualServiceType(VirtualServiceType virtualServiceType) {
        if (virtualServiceType != null) {
            setVirtualServiceInfo(
                    virtualServiceInfoFactory.getVirtualServiceInfo(virtualServiceType.getType()));
            this.virtualServiceType = virtualServiceType;
        }

    }
    
    @PostConstruct
    @Order(1)
    public void init() throws ClassNotFoundException, JsonProcessingException,
            InstantiationException, IllegalAccessException {
        setVirtualServiceType(ApiType.findApiType());
        if (getVirtualServiceType() != null ) {
            virtualServiceInfo = getVirtualServiceInfo();
            virtualServiceInfo.loadVirtualServices();
            virtualServiceInfo.setResourceParent(virtualServiceInfo.loadMapper());
        } else if (getVirtualServiceType() == null ) {
            setVirtualServiceType(VirtualServiceType.NON_REST);
            virtualServiceInfo = getVirtualServiceInfo();
        }
    }

    @Autowired
    private VirtualServiceInfoFactory virtualServiceInfoFactory;

    private VirtualServiceInfo virtualServiceInfo;


    public VirtualServiceInfo getVirtualServiceInfo() {
        return virtualServiceInfo;
    }

    private void setVirtualServiceInfo(VirtualServiceInfo virtualServiceInfo) {
        this.virtualServiceInfo = virtualServiceInfo;
    }


    private ObjectMapper getObjectMapper() {
        objectMapper.findAndRegisterModules();
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
                final String input = mockTransferObject.getInput();
                final String output = mockTransferObject.getOutput();

                Set<String> excludeSet = null;
                if (mockTransferObject.getExcludeList() != null) {
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
    
    public void findOperationIdForService(VirtualServiceRequest mockLoadRequest) {
        if (mockLoadRequest.getOperationId() == null && virtualServiceInfo != null) {
            final String resourceUrl = mockLoadRequest.getUrl().substring(1);
            final List<String> resouceSplitterList =
                    new LinkedList(Arrays.asList(resourceUrl.split("/")));
            if (resouceSplitterList.size() > 0) {
                final String operationId =
                        virtualServiceInfo.getOperationId(mockLoadRequest.getMethod(),
                                virtualServiceInfo.getResourceParent(), resouceSplitterList);
                mockLoadRequest.setOperationId(operationId);
                mockLoadRequest.setResource(resouceSplitterList.get(0));
            }
        }
    }
    
    public ResponseEntity checkIfServiceDataAlreadyExists(
            VirtualServiceRequest virtualServiceRequest) {
        final Long id = isMockAlreadyExists(virtualServiceRequest);
        if (id != null && id != 0) {
            final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
                    messageSource.getMessage("VS_DATA_ALREADY_EXISTS", null, locale));
            virtualServiceRequest.setId(id);
            virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
            return new ResponseEntity<VirtualServiceStatus>(virtualServiceStatus,
                    HttpStatus.BAD_REQUEST);
        }
        return null;
    }
    

    public Long isMockAlreadyExists(VirtualServiceRequest mockTransferObject) {

        try {
            final Map<MockRequest, MockResponse> mockDataSetupMap = readDynamicResponse(
                    mockTransferObject.getResource(), mockTransferObject.getOperationId());
            final MockServiceRequest mockServiceRequest = new MockServiceRequest();
            Class inputObjectType = getVirtualServiceInfo().getInputType(mockTransferObject);
            mockServiceRequest
                    .setHeaderParams(Converter.converter(mockTransferObject.getHeaderParams()));
            mockServiceRequest.setOperationId(mockTransferObject.getOperationId());
            mockServiceRequest.setInputObjectType(inputObjectType);
            mockServiceRequest.setType(mockTransferObject.getType());
            mockServiceRequest.setRule(mockTransferObject.getRule());
            mockServiceRequest
                    .setParams(Converter.converter(mockTransferObject.getAvailableParams()));
            mockServiceRequest.setResource(mockTransferObject.getResource());
    
            if (inputObjectType != null) {
                mockServiceRequest.setInput(getObjectMapper()
                        .readValue(mockTransferObject.getInput(), inputObjectType));
            } else if(mockTransferObject.getInput() != null){
                mockServiceRequest.setInput(mockTransferObject.getInput());
            }
            
            final Map<Integer, ReturnMockResponse> returnMockResponseMap =
                    isResponseExists(mockDataSetupMap, mockServiceRequest);

            if (returnMockResponseMap.size() > 0) {
                return isResposeExists(mockTransferObject, inputObjectType, mockServiceRequest,
                        returnMockResponseMap);
            }


        } catch (final Exception e) {
            VirtualServiceUtil.log.error("isMockAlreadyExists :: " + e.getMessage());
            e.printStackTrace();
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
        VirtualServiceUtil.log.info("Sorted list : " + returnMockResponseList);
        final ReturnMockResponse rMockResponse = returnMockResponseList.iterator().next();
        if (rMockResponse != null && rMockResponse.getHeaderResponse() != null) {

            final RequestBody requestBody = buildRequestBody(mockTransferObject, inputObjectType,
                    mockServiceRequest, rMockResponse);
            boolean isBodyMatch = false;
            if (inputObjectType != null) {
                isBodyMatch = RequestBodyTypes.fromString(inputObjectType.getTypeName())
                        .compareRequestBody(requestBody);
            } else {
                //isBodyMatch = mockTransferObject.getInput().equals()
            }
            return checkExistsInEachCatagory(mockTransferObject, mockServiceRequest, rMockResponse,
                    isBodyMatch);
        }
        return 0;
    }

    private RequestBody buildRequestBody(VirtualServiceRequest mockTransferObject,
            final Class inputObjectType, final MockServiceRequest mockServiceRequest,
            final ReturnMockResponse rMockResponse) {
        final RequestBody requestBody = new RequestBody();
        requestBody.setObjectMapper(getObjectMapper());
        requestBody.setExcludeList(rMockResponse.getMockRequest().getExcludeSet());
        requestBody.setExpectedInput(rMockResponse.getMockRequest().getInput());
        requestBody.setInputObjectType(inputObjectType);
        requestBody.setInputRequest(mockTransferObject.getInput());
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
        boolean isValid = true;
        try {
            final VirtualServiceRequest mockTransferObjectActual =
                    virtualServiceInfo.getResponseType(mockTransferObject);
            if (!mockTransferObjectActual.getResponseType().isEmpty() && virtualServiceValidRequest
                    .validResponse(mockTransferObjectActual, mockTransferObject)) {
                isValid = true;
            }
        } catch (final Exception e) {
            throw new InvalidMockResponseException(e);
        }
        return isValid;
    }
    
    public Map<Integer, ReturnMockResponse> validateBusinessRules(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
            return virtualServiceValidRequest.validBusinessRuleForInputObject(mockDataSetupMap,
            mockServiceRequest);
    }

    
    

    public Map<Integer, ReturnMockResponse> isResponseExists(
        final Map<MockRequest, MockResponse> mockDataSetupMap,
        MockServiceRequest mockServiceRequest) throws IOException, JAXBException {
        if ((mockServiceRequest.getParams() == null || mockServiceRequest.getParams().isEmpty())
                && mockServiceRequest.getInput() == null) {
            return virtualServiceValidRequest.validForNoParam(mockDataSetupMap,
                    mockServiceRequest);
        } else if ("RULE".equalsIgnoreCase(mockServiceRequest.getType()) || mockServiceRequest.getInput() != null) {
            return virtualServiceValidRequest.validForInputObject(mockDataSetupMap,
                    mockServiceRequest);
        } else if (mockServiceRequest.getParams() != null
                && mockServiceRequest.getParams().size() > 0) {
            return virtualServiceValidRequest.validForParam(mockDataSetupMap,
                    mockServiceRequest);
        }
        return null;
    }


    public Object returnResponse(Method method, MockServiceRequest mockServiceRequest)
        throws IOException, ResponseException, JSONException, JAXBException {
        VirtualServiceUtil.log
                .info(" mockServiceRequest.getResource() : " + mockServiceRequest.getResource());
        final Map<MockRequest, MockResponse> mockDataSetupMap = readDynamicResponse(
                mockServiceRequest.getResource(), mockServiceRequest.getOperationId());
    
        //Rule Execution
        Map<Integer, ReturnMockResponse> returnMockResponseMap =
                validateBusinessRules(mockDataSetupMap, mockServiceRequest);
        
        //No Rule conditions exists/met then run the script
        if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
           returnMockResponseMap = virtualServiceValidRequest.checkScriptResponse(mockDataSetupMap, mockServiceRequest);
        }
    
        //No script conditions exists/met then run the mock response
        if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
            returnMockResponseMap =
                    isResponseExists(mockDataSetupMap, mockServiceRequest);
        }
    
       
        VirtualServiceUtil.log.info("number of matches : " + returnMockResponseMap.size());
        ResponseEntity responseEntity = null;
        ReturnMockResponse rMockResponse = null;
        if (returnMockResponseMap.size() > 0) {
            final List<ReturnMockResponse> returnMockResponseList =
                    new ArrayList<>(returnMockResponseMap.values());
            Collections.sort(returnMockResponseList, new BestMatchComparator());
            VirtualServiceUtil.log.info("Sorted list : " + returnMockResponseList);
            rMockResponse = returnMockResponseList.stream()
                    .filter(x -> x.isExactMatch()).findAny().orElse(null);
            if(rMockResponse != null) {
	            rMockResponse = returnMockResponseList.iterator().next();
	            if(WSResource.isExists(method)){
	                return returnSoapResponse(method, rMockResponse);
              }
	            if (rMockResponse.getHeaderResponse() != null) {
	                responseEntity = buildResponseEntity(rMockResponse.getMockResponse(),
	                        rMockResponse.getHeaderResponse());
	            } else {
	                responseEntity = buildResponseEntity(rMockResponse.getMockResponse(), null);
	            }
	            if (responseEntity != null) {
	            	virtualService.updateUsageTime(rMockResponse.getMockRequest());
	                return returnResponse(method, responseEntity, responseEntity.getBody().toString());
	            }
        	}
        } else {
            VirtualServiceUtil.log.error(
                    " Unable to find matching for the given request >>> " + mockServiceRequest);
        }
        return mockResponseNotFoundorSet(method, mockDataSetupMap, mockServiceRequest);
    }




    private Object returnSoapResponse(Method method,  ReturnMockResponse rMockResponse)
        throws JAXBException {
            if (rMockResponse.getMockResponse().getOutput() != null) {
                if(ContentType.XML.equals(rMockResponse.getMockRequest().getContentType())) {
                    return XMLConverter.xmlToObject(method.getReturnType(), rMockResponse.getMockResponse().getOutput());
                }
                Type mySuperclass = null;

                try {
                    mySuperclass = method.getGenericReturnType();
                    return this.objectMapper.readValue(rMockResponse.getMockResponse().getOutput(), this.objectMapper.constructType(mySuperclass));
                } catch (Exception ex) {
                    log.error(" GenericReturnType  >>> mySuperclass " + mySuperclass);
                    throw new SoapFaultException("MOCK NOT FOUND ("+ex.getMessage()+")  GenericReturnType  >>> mySuperclass " + method.getReturnType());
                }
            } else {
                throw new SoapFaultException("MOCK NOT FOUND");
            }
    }

    private Object returnResponse(Method method, ResponseEntity responseEntity, String response)
            throws ResponseException {
        VirtualServiceUtil.log.info(" responseEntity.getHeaders() :" + responseEntity.getHeaders());
        if (response != null) {
            final String responseOut = xmlConverter.returnAsXml(method, responseEntity, response);
            if (method.getReturnType().equals(Response.class)) {
                return Response.status(responseEntity.getStatusCode().value()).entity(responseOut)
                    .build();
            } else if (method.getReturnType().equals(ResponseEntity.class)) {
                return new ResponseEntity(responseOut, responseEntity.getHeaders(),
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

         if (responseEntity != null) {
             if(WSResource.isExists(method)){
                 SoapFaultException SoapFaultException = new SoapFaultException(responseEntity.getBody().toString());
                 throw SoapFaultException;
             }
            final ResponseException responseException = new ResponseException();
            if (VirtualServiceType.CXF_JAX_RS.compareTo(getVirtualServiceType()) == 0) {
                responseException
                        .setResponse(Response.status(responseEntity.getStatusCode().value())
                                .entity(responseEntity.getBody()).build());
                throw new WebApplicationException(responseException.getResponse());
            } else if (VirtualServiceType.SPRING.compareTo(getVirtualServiceType()) == 0) {
                responseException.setResponseEntity(responseEntity);
                throw responseException;
            }
        } else {
            VirtualServiceUtil.log.error("Response entity is null");
        }
        return null;
    }


    // covert response as XML if the accept is xml
    private Object mockResponseNotFoundorSet(Method method,
            Map<MockRequest, MockResponse> mockDataSetupMap, MockServiceRequest mockServiceRequest)
            throws NoSuchMessageException, ResponseException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // TO-DO
        if (mockDataSetupMap.size() > 0) {
            return returnResponse(method,
                    new ResponseEntity(
                            messageSource.getMessage("VS_RESPONSE_NOT_FOUND", null, locale),
                            headers, HttpStatus.INTERNAL_SERVER_ERROR),
                    null);
        } else {
            VirtualServiceUtil.log.error("Mock Response was not defined for the given input");
            return returnResponse(method,
                    new ResponseEntity(messageSource.getMessage("VS_DATA_NOT_SET", null, locale),
                            headers, HttpStatus.INTERNAL_SERVER_ERROR),
                    null);
        }
    }

    private ResponseEntity buildResponseEntity(MockResponse mockResponse,
            Map<String, String> headerMap) {
        final HttpHeaders headers = buildHeader(mockResponse, headerMap);
        return new ResponseEntity(mockResponse.getOutput(), headers,
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

        }
        return headers;
    }
}
