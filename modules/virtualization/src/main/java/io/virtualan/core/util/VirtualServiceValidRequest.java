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
package io.virtualan.core.util;

import com.cedarsoftware.util.io.JsonObject;
import io.virtualan.core.model.ContentType;
import io.virtualan.core.model.ResponseProcessType;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.virtualan.core.util.rule.RuleEvaluator;
import io.virtualan.core.util.rule.ScriptExecutor;
import io.virtualan.mapson.Mapson;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.model.VirtualServiceApiResponse;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.requestbody.RequestBody;
import io.virtualan.requestbody.RequestBodyTypes;

/**
 * Retrieve valid response.
 *
 *
 * @author Elan Thangamani
 *
 **/

@Service("virtualServiceValidRequest")
@Slf4j
public class VirtualServiceValidRequest {

    @Autowired
    private RuleEvaluator ruleEvaluator;

    @Autowired
    private VirtualServiceUtil virtualServiceUtil;

    @Autowired
    private ScriptExecutor scriptExecutor;
    
    @Autowired
    private VirtualServiceParamComparator virtualServiceParamComparator;

    @Autowired
    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        objectMapper.findAndRegisterModules();
        return objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        // ,DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
        );
    }

    public boolean validResponse(VirtualServiceRequest mockTransferObjectActual,
            VirtualServiceRequest mockTransferObject) throws ClassNotFoundException, IOException {
        final VirtualServiceApiResponse apiResponse = mockTransferObjectActual.getResponseType()
                .get(mockTransferObject.getHttpStatusCode());
        if (apiResponse != null && apiResponse.getObjectType() != null
                && mockTransferObject.getOutput() != null
        && !(apiResponse.getObjectType() instanceof String)) {
            objectMapper.readValue(mockTransferObject.getOutput().toString(),
                    Class.forName(apiResponse.getObjectType()));
        }
        return true;
    }

    public final Map<Integer, ReturnMockResponse> validForParam(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if(!ResponseProcessType.PARAMS.name().equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                buildRequestBody(mockServiceRequest, mockRequestResponse);
                final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
                if (numberAttrMatch != 0) {
                    count++;

                    final ReturnMockResponse returnMockResponse = returnMockResponse(
                        mockServiceRequest,
                        mockRequestResponse, numberAttrMatch);
                    log.debug("{} : {}", numberAttrMatch,
                        mockRequestResponse.getKey().getAvailableParams().size());
                    returnMockResponse.setExactMatch(
                        numberAttrMatch == mockRequestResponse.getKey().getAvailableParams()
                            .size());

                    matchMap.put(count, returnMockResponse);
                }
            }
        }
        return matchMap;
    }
    
    public Map<Integer, ReturnMockResponse> validBusinessRuleForInputObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest)  {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if("RULE".equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                log.debug("Rule key : {}" , mockRequestResponse.getKey().getRule());
                log.debug("Rule Input : {}" , mockServiceRequest);
                log.debug("Rule evaluated flag : {}" , ruleEvaluator.expressionEvaluator(mockServiceRequest, mockRequestResponse.getKey().getRule()));
                if(ruleEvaluator.expressionEvaluator(mockServiceRequest,mockRequestResponse.getKey().getRule())) {
                    final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                            mockRequestResponse, 1);
                    log.debug("Successful expression Rule evaluated : ");
                    returnMockResponse.setExactMatch(true);
                    matchMap.put(count, returnMockResponse);
                }
            }
        }
        log.debug("Rule evaluated Ended : {}" , matchMap);
        return matchMap;
    }



    public Map<Integer, ReturnMockResponse> checkScriptResponse(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException, ScriptErrorException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if(ResponseProcessType.SCRIPT.name().equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                log.debug("Script : {}" , mockRequestResponse.getKey().getRule());
                log.debug("Script Input {}: " , mockServiceRequest);
                try {
                    MockResponse mockResponse = new MockResponse();
                    mockResponse = scriptExecutor.executeScript(mockServiceRequest, mockResponse, mockRequestResponse.getKey().getRule());
                    log.debug("Script output expected : {}" , mockResponse);
                    if (mockResponse != null) {
                        
                        final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                                mockRequestResponse, 1);
                        returnMockResponse.setMockResponse(mockResponse);
                        log.debug("Successful expression Rule evaluated : ");
                        returnMockResponse.setExactMatch(true);
                        matchMap.put(count, returnMockResponse);
                    }
                }catch (Exception e){
                    log.warn("Oh!!! check the groovy script... Script was not working as expected configuration? " + e.getMessage());
                    throw new ScriptErrorException("Oh!!! check the groovy script... Script was not working as expected configuration? " + e.getMessage());
                }
                return  matchMap;
            }
        }
        log.debug("Rule evaluated Ended : " + matchMap);
        return matchMap;
    }
    
    public Map<Integer, ReturnMockResponse> validObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException, JAXBException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        if(ContentType.XML.equals(mockServiceRequest.getContentType())) {
            return virtualServiceUtil.isResponseExists(mockDataSetupMap, mockServiceRequest);
        } else {
            String jsonString =
                (mockServiceRequest.getInputObjectType() != null &&
                    mockServiceRequest.getInputObjectType().equals(mockServiceRequest.getInput().getClass())) ?
                    getObjectMapper().writeValueAsString(mockServiceRequest.getInput())
                    : mockServiceRequest.getInput().toString();
            Map<String, String> actualMap = Mapson
                .buildMAPsonFromJson(jsonString);
            for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
                buildRequestBody(mockServiceRequest, mockRequestResponse);
                final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);

                String expectedJSON = null;
                if(ContentType.XML.equals(mockRequestResponse.getKey().getContentType())) {
                     Object object = XMLConverter.xmlToObject(mockServiceRequest.getInputObjectType(),
                        mockRequestResponse.getKey().getInput());
                    expectedJSON = getObjectMapper().writeValueAsString(object);
                } else {
                    expectedJSON = mockRequestResponse.getKey().getInput();
                }
                Map<String, String> expectedMap = Mapson
                    .buildMAPsonFromJson(expectedJSON);
                if (areEqual(actualMap, expectedMap, mockRequestResponse.getKey().getExcludeSet())) {
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(
                        mockServiceRequest,
                        mockRequestResponse, numberAttrMatch);
                    boolean isMatched = virtualServiceParamComparator
                        .isAllParamPresent(mockServiceRequest, returnMockResponse);

                    returnMockResponse.setExactMatch(isMatched);
                    matchMap.put(count, returnMockResponse);
                }
            }
        }
        return matchMap;
    }
    

    private boolean areEqual(Map<String, String> first, Map<String, String> second, Set<String> excludeFields) {
        if (first.size() != second.size()) {
            return false;
        }
        if(excludeFields != null) {
            first.entrySet()
                .removeIf(e -> excludeFields.stream().anyMatch(x -> e.getKey().contains(x)));
        }
        return first.entrySet().stream().allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
    
    public Map<Integer, ReturnMockResponse> validForInputObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException, JAXBException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
            if( (ResponseProcessType.RULE.name().equalsIgnoreCase(mockServiceRequest.getType()) || ResponseProcessType.SCRIPT.name().equalsIgnoreCase(mockServiceRequest.getType())
                || ResponseProcessType.SCRIPT.name().equalsIgnoreCase(mockRequestResponse.getKey().getType()) ||
                ResponseProcessType.RULE.name().equalsIgnoreCase(mockRequestResponse.getKey().getType()))
                && mockServiceRequest.getRule() != null && mockServiceRequest.getRule().equals(mockRequestResponse.getKey().getRule())) {
                    count++;
                    return getScriptResponseCount(mockServiceRequest, matchMap, count,
                        mockRequestResponse, numberAttrMatch);
            }else if(ResponseProcessType.RESPONSE.name().equalsIgnoreCase(mockRequestResponse.getKey().getType())){
                count = getResponseCount(mockServiceRequest, matchMap, count, mockRequestResponse,
                    numberAttrMatch);
            }

        }
        return matchMap;
    }

    public int getResponseCount(MockServiceRequest mockServiceRequest,
        Map<Integer, ReturnMockResponse> matchMap, int count,
        Entry<MockRequest, MockResponse> mockRequestResponse, int numberAttrMatch)
        throws IOException, JAXBException {
        RequestBody requestBody =
                buildRequestBody(mockServiceRequest, mockRequestResponse);
        if (numberAttrMatch != 0 && RequestBodyTypes
                    .fromString(mockServiceRequest.getInputObjectType().getTypeName())
                    .compareRequestBody(requestBody)) {
            count++;
            getResponseCount(mockServiceRequest, matchMap, count, mockRequestResponse,
                numberAttrMatch,
                requestBody);
        }
        return count;
    }

    public Map<Integer, ReturnMockResponse> getScriptResponseCount(
        MockServiceRequest mockServiceRequest, Map<Integer, ReturnMockResponse> matchMap, int count,
        Entry<MockRequest, MockResponse> mockRequestResponse, int numberAttrMatch) {
        final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
            mockRequestResponse, numberAttrMatch);
        returnMockResponse.setExactMatch(true);
        matchMap.put(count, returnMockResponse);
        return matchMap;
    }

    public void getResponseCount(MockServiceRequest mockServiceRequest,
        Map<Integer, ReturnMockResponse> matchMap, int count,
        Entry<MockRequest, MockResponse> mockRequestResponse, int numberAttrMatch,
        RequestBody requestBody) throws IOException, JAXBException {
        final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                mockRequestResponse, numberAttrMatch);
        returnMockResponse.setExactMatch(
                mockRequestResponse.getKey().getAvailableParams().isEmpty()  ? mockRequestResponse.getKey().getAvailableParams().isEmpty() :
                    numberAttrMatch == mockRequestResponse.getKey().getAvailableParams().size()
                        && RequestBodyTypes
                        .fromString(mockServiceRequest.getInputObjectType().getTypeName())
                        .compareRequestBody(requestBody));
        matchMap.put(count, returnMockResponse);
    }

    public Map<Integer, ReturnMockResponse> validForNoParam(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException, JAXBException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if(!ResponseProcessType.PARAMS.name().equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                final RequestBody requestBody =
                    buildRequestBody(mockServiceRequest, mockRequestResponse);
                if (RequestBodyTypes.fromString("NO_REQUEST_PARAM").compareRequestBody(requestBody)
                    && (mockServiceRequest.getHeaderParams() == null
                    || mockServiceRequest.getHeaderParams().isEmpty())) {
                    count = getNoParamMatch(mockServiceRequest, matchMap, count,
                        mockRequestResponse);

                } else {
                    count = getMatch(mockServiceRequest, matchMap, count, mockRequestResponse);

                }
            }
        }
        return matchMap;
    }

    public int getNoParamMatch(MockServiceRequest mockServiceRequest,
        Map<Integer, ReturnMockResponse> matchMap, int count,
        Entry<MockRequest, MockResponse> mockRequestResponse) {
        if (mockRequestResponse.getKey().getAvailableParams().isEmpty()) {
            final int numberAttrMatch = 1;
            count++;
            final ReturnMockResponse returnMockResponse = returnMockResponse(
                mockServiceRequest, mockRequestResponse, numberAttrMatch);
            returnMockResponse.setExactMatch(true);
            matchMap.put(count, returnMockResponse);
        }
        return count;
    }

    public int getMatch(MockServiceRequest mockServiceRequest,
        Map<Integer, ReturnMockResponse> matchMap, int count,
        Entry<MockRequest, MockResponse> mockRequestResponse) {
        final int numberAttrMatch = virtualServiceParamComparator
            .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
        if (numberAttrMatch != 0) {
            count++;
            final ReturnMockResponse returnMockResponse = returnMockResponse(
                mockServiceRequest, mockRequestResponse, numberAttrMatch);
            returnMockResponse.setExactMatch(
                mockRequestResponse.getKey().getAvailableParams().size() == 0 ? true : numberAttrMatch == mockRequestResponse.getKey().getAvailableParams()
                    .size());
            matchMap.put(count, returnMockResponse);
        }
        return count;
    }

    private RequestBody buildRequestBody(MockServiceRequest mockServiceRequest,
            final Map.Entry<MockRequest, MockResponse> mockRequestResponse) {
        final RequestBody requestBody = new RequestBody();
        requestBody.setObjectMapper(getObjectMapper());
        requestBody.setExcludeList(mockRequestResponse.getKey().getExcludeSet());
        requestBody.setExpectedInput(mockRequestResponse.getKey().getInput());
        if (mockServiceRequest.getInput() !=null &&
            VirtualanConfiguration.isValidJson(mockServiceRequest.getInput().toString()) &&
            mockServiceRequest.getInputObjectType() != null
            && mockServiceRequest.getInputObjectType().isAssignableFrom(String.class) ) {
            mockServiceRequest.setInputObjectType(JsonObject.class);
        }
        requestBody.setInputObjectType(mockServiceRequest.getInputObjectType());
        requestBody.setActualInput(mockServiceRequest.getInput());
        requestBody.setContentType(mockRequestResponse.getKey().getContentType());
        return requestBody;
    }

    public ReturnMockResponse returnMockResponse(MockServiceRequest mockServiceRequest,
            final Map.Entry<MockRequest, MockResponse> mockRequestResponse,
            final int numberAttrMatch) {
        final ReturnMockResponse returnMockResponse = new ReturnMockResponse();
        returnMockResponse.setMockResponse(mockRequestResponse.getValue());
        returnMockResponse.setHeaderResponse(mockServiceRequest.getHeaderParams());
        returnMockResponse.setMockRequest(mockRequestResponse.getKey());
        returnMockResponse.setNumberAttrMatch(numberAttrMatch);
        return returnMockResponse;
    }

}
