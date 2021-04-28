/*
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



package io.virtualan.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Virtual Service Request from external system.
 * 
 * @author  Elan Thangamani
 * 
 **/
@Data
@JsonInclude(Include.NON_NULL)
public class VirtualServiceRequest {

    private long id;
    private String operationId;
    private String httpStatusCode;
    private String url;
    private String type;
    private String requestType;
    private long usageCount;
    private int priority;
    private String method;
    private Class inputObjectType;
    private Class responseObjectType;
    private String outputObjectType;
    private Object input;
    private String rule;
    private Object output;
    private List<VirtualServiceKeyValue> availableParams = new ArrayList<>();
    private List<VirtualServiceKeyValue> headerParams  = new ArrayList<>();
    private Map<String, VirtualServiceApiResponse> responseType;
    private String excludeList;
    private String resource;
    private String desc;
    private VirtualServiceStatus mockStatus;
    private java.util.Calendar lastUsedDateTime;
    private ContentType contentType;
    private Map<String, String> types;


    public Object getHeaderParam(String param) {
        return headerParams.stream().filter(x -> x.getKey().equalsIgnoreCase(param)).map(
            VirtualServiceKeyValue::getValue);
    }

    public Object getAvailableParam(String param) {
        return availableParams.stream().filter(x -> x.getKey().equalsIgnoreCase(param)).map(
            VirtualServiceKeyValue::getValue);
    }

    public String groovyTemplateObj() {
        return " def executeScript(mockServiceRequest, responseObject) { \n" +
            "     int age = getAge(mockServiceRequest.getInput().getBirthday()); \n" +
            "    String postalCode = mockServiceRequest.getInput().getPostalCode(); \n" +
            "    int riskFactor = computeRiskFactor(age, postalCode); \n" +
            "    responseObject.setHttpStatusCode('200'); \n" +
            "    responseObject.setOutput(String.valueOf(riskFactor)); \n" +
            "    return responseObject.builder();\n" +
            " }} \n";
    }
    private Map<String, String> httpStatusMap;

    public VirtualServiceRequest(long id, String operationId, String input, String output) {
        this.id = id;
        this.operationId = operationId;
        this.input = input;
        this.output = output;
    }

    public VirtualServiceRequest() {}

    @Override
    public String toString() {
        return "VirtualServiceRequest{" +
            "id=" + id +
            ", operationId='" + operationId + '\'' +
            ", httpStatusCode='" + httpStatusCode + '\'' +
            ", url='" + url + '\'' +
            ", type='" + type + '\'' +
            ", requestType='" + requestType + '\'' +
            ", usageCount=" + usageCount +
            ", priority=" + priority +
            ", method='" + method + '\'' +
            ", inputObjectType=" + inputObjectType +
            ", outputObjectType='" + outputObjectType + '\'' +
            ", input='" + input + '\'' +
            ", rule='" + rule + '\'' +
            ", output='" + output + '\'' +
            ", availableParams=" + availableParams +
            ", headerParams=" + headerParams +
            ", responseType=" + responseType +
            ", excludeList='" + excludeList + '\'' +
            ", resource='" + resource + '\'' +
            ", desc='" + desc + '\'' +
            ", mockStatus=" + mockStatus +
            ", lastUsedDateTime=" + lastUsedDateTime +
            ", contentType=" + contentType +
            ", httpStatusMap=" + httpStatusMap +
            '}';
    }
}
