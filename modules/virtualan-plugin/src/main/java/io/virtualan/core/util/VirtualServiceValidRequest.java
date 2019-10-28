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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.virtualan.core.util.rule.RuleEvaluator;
import io.virtualan.core.util.rule.ScriptExecutor;
import io.virtualan.mapson.Mapson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class VirtualServiceValidRequest {

    private static final Logger log = LoggerFactory.getLogger(VirtualServiceUtil.class);
    
    @Autowired
    private RuleEvaluator ruleEvaluator;
    
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
        if (apiResponse != null && apiResponse.getObjectType() != null) {
            objectMapper.readValue(mockTransferObject.getOutput(),
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
            buildRequestBody(mockServiceRequest, mockRequestResponse);
            final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
            if (numberAttrMatch != 0) {
            	count++;
            	
                final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                        mockRequestResponse, numberAttrMatch);
                System.out.println(numberAttrMatch +" : "+ mockRequestResponse.getKey().getAvailableParams().size());
                returnMockResponse.setExactMatch(numberAttrMatch == mockRequestResponse.getKey().getAvailableParams().size());
                	
                matchMap.put(count, returnMockResponse);
            }
        }
        return matchMap;
    }
    
    public Map<Integer, ReturnMockResponse> validBusinessRuleForInputObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if("RULE".equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                System.out.println("Rule key : " + mockRequestResponse.getKey().getRule());
                System.out.println("Rule Input : " + mockServiceRequest);
                System.out.println("Rule evaluated flag :" +ruleEvaluator.expressionEvaluator(mockServiceRequest, mockRequestResponse.getKey().getRule()));
                if(ruleEvaluator.expressionEvaluator(mockServiceRequest,mockRequestResponse.getKey().getRule())) {
                    final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                            mockRequestResponse, 1);
                    System.out.println("Successful expression Rule evaluated : ");
                    returnMockResponse.setExactMatch(true);
                    matchMap.put(count, returnMockResponse);
                }
            }
        }
        System.out.println("Rule evaluated Ended : " + matchMap);
        return matchMap;
    }
    
    public Map<Integer, ReturnMockResponse> checkScriptResponse(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            if("SCRIPT".equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                log.info("Script : " + mockRequestResponse.getKey().getRule());
                log.info("Script Input : " + mockServiceRequest);
                try {
                    MockResponse mockResponse = new MockResponse();
                    mockResponse = scriptExecutor.executeScript(mockServiceRequest, mockResponse, mockRequestResponse.getKey().getRule());
                    log.info("Script output expected : " + mockResponse);
                    if (mockResponse != null) {
                        
                        final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                                mockRequestResponse, 1);
                        returnMockResponse.setMockResponse(mockResponse);
                        System.out.println("Successful expression Rule evaluated : ");
                        returnMockResponse.setExactMatch(true);
                        matchMap.put(count, returnMockResponse);
                    }
                }catch (Exception e){
                    log.warn("Oh!!! check the groovy script... Script was not working as expected configuration? " + e.getMessage());
                }
                return  matchMap;
            }
        }
        System.out.println("Rule evaluated Ended : " + matchMap);
        return matchMap;
    }
    
    public Map<Integer, ReturnMockResponse> validObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        Map<String, String> actualMap = Mapson.buildMAPsonFromJson(mockServiceRequest.getInput().toString());
        
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            final RequestBody requestBody =
                    buildRequestBody(mockServiceRequest, mockRequestResponse);
            final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
            
            Map<String, String> expectedMap = Mapson.buildMAPsonFromJson(mockRequestResponse.getKey().getInput());
            if(areEqual(actualMap, expectedMap)) {
                count++;
                final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                        mockRequestResponse, numberAttrMatch);
                boolean isMatched = virtualServiceParamComparator.isAllParamPresent(mockServiceRequest,returnMockResponse);
                
                returnMockResponse.setExactMatch(isMatched);
                matchMap.put(count, returnMockResponse);
            }
        }
        return matchMap;
    }
    
    private boolean areEqual(Map<String, String> first, Map<String, String> second) {
        if (first.size() != second.size()) {
            return false;
        }
        return first.entrySet().stream().allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
    
    public Map<Integer, ReturnMockResponse> validForInputObject(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
    
            final int numberAttrMatch = virtualServiceParamComparator
                    .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
            if( ("RULE".equalsIgnoreCase(mockServiceRequest.getType()) || "RULE".equalsIgnoreCase(mockRequestResponse.getKey().getType()))) {
                if(mockServiceRequest.getRule() != null && mockServiceRequest.getRule().equals(mockRequestResponse.getKey().getRule())) {
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                            mockRequestResponse, numberAttrMatch);
                    returnMockResponse.setExactMatch(true);
                    matchMap.put(count, returnMockResponse);
                    return matchMap;
                }
            } else if( ("SCRIPT".equalsIgnoreCase(mockServiceRequest.getType()) || "SCRIPT".equalsIgnoreCase(mockRequestResponse.getKey().getType()))) {
                if(mockServiceRequest.getRule() != null && mockServiceRequest.getRule().equals(mockRequestResponse.getKey().getRule())
                        ||"SCRIPT".equalsIgnoreCase(mockRequestResponse.getKey().getType())) {
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                            mockRequestResponse, numberAttrMatch);
                    returnMockResponse.setExactMatch(true);
                    matchMap.put(count, returnMockResponse);
                    return matchMap;
                }
            }else if("RESPONSE".equalsIgnoreCase(mockRequestResponse.getKey().getType())){
                RequestBody requestBody =
                        buildRequestBody(mockServiceRequest, mockRequestResponse);
                if (numberAttrMatch != 0 && RequestBodyTypes
                        .fromString(mockServiceRequest.getInputObjectType().getTypeName())
                        .compareRequestBody(requestBody)) {
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(mockServiceRequest,
                            mockRequestResponse, numberAttrMatch);
                    returnMockResponse.setExactMatch(
                            mockRequestResponse.getKey().getAvailableParams().size() == 0 ? true : numberAttrMatch == mockRequestResponse.getKey().getAvailableParams().size()
                                    && RequestBodyTypes
                                    .fromString(mockServiceRequest.getInputObjectType().getTypeName())
                                    .compareRequestBody(requestBody));
                    matchMap.put(count, returnMockResponse);
                }
            }

        }
        return matchMap;
    }

    public Map<Integer, ReturnMockResponse> validForNoParam(
            final Map<MockRequest, MockResponse> mockDataSetupMap,
            MockServiceRequest mockServiceRequest) throws IOException {
        final Map<Integer, ReturnMockResponse> matchMap = new HashMap<>();
        int count = 0;
        for (final Map.Entry<MockRequest, MockResponse> mockRequestResponse : mockDataSetupMap
                .entrySet()) {
            final RequestBody requestBody =
                    buildRequestBody(mockServiceRequest, mockRequestResponse);

            if (RequestBodyTypes.fromString("NO_REQUEST_PARAM").compareRequestBody(requestBody)
                    && (mockServiceRequest.getHeaderParams() == null
                            || mockServiceRequest.getHeaderParams().isEmpty())) {

                if (mockRequestResponse.getKey().getAvailableParams().isEmpty()) {
                    final int numberAttrMatch = 1;
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(
                            mockServiceRequest, mockRequestResponse, numberAttrMatch);
                    returnMockResponse.setExactMatch(true);
                    matchMap.put(count, returnMockResponse);
                }

            } else {
                final int numberAttrMatch = virtualServiceParamComparator
                        .compareQueryParams(mockRequestResponse.getKey(), mockServiceRequest);
                if (numberAttrMatch != 0) {
                    count++;
                    final ReturnMockResponse returnMockResponse = returnMockResponse(
                            mockServiceRequest, mockRequestResponse, numberAttrMatch);
                    returnMockResponse.setExactMatch(numberAttrMatch == mockRequestResponse.getKey().getAvailableParams().size());
                    matchMap.put(count, returnMockResponse);
                }

            }
        }
        return matchMap;
    }

    private RequestBody buildRequestBody(MockServiceRequest mockServiceRequest,
            final Map.Entry<MockRequest, MockResponse> mockRequestResponse) {
        final RequestBody requestBody = new RequestBody();
        requestBody.setObjectMapper(getObjectMapper());
        requestBody.setExcludeList(mockRequestResponse.getKey().getExcludeSet());
        requestBody.setExpectedInput(mockRequestResponse.getKey().getInput());
        requestBody.setInputObjectType(mockServiceRequest.getInputObjectType());
        requestBody.setActualInput(mockServiceRequest.getInput());
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
