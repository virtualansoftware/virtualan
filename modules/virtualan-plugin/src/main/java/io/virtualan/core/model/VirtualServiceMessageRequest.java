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
@JsonInclude(Include.NON_NULL)
@Data
public class VirtualServiceMessageRequest {

    private long id;
    private String requestTopicOrQueueName;
    private String httpStatusCode;
    private String brokerUrl;
    private String type;
    private String requestType;
    private long usageCount;
    private ContentType contentType;
    private int priority;
    private String responseTopicOrQueueName;
    private Class inputObjectType;
    private String outputObjectType;
    private String input;
    private String rule;
    private String output;
    private List<VirtualServiceKeyValue> availableParams = new ArrayList<>();
    private List<VirtualServiceKeyValue> headerParams  = new ArrayList<>();
    private Map<String, VirtualServiceApiResponse> responseType;
    private String excludeList;
    private String resource;
    private String desc;
    private VirtualServiceStatus mockStatus;
    private java.util.Calendar lastUsedDateTime;


    public Object getHeaderParam(String param) {
        return getHeaderParams().stream().filter(x -> x.getKey().equalsIgnoreCase(param)).map(x -> x.getValue());
    }

    public Object getAvailableParam(String param) {
        return getAvailableParams().stream().filter(x -> x.getKey().equalsIgnoreCase(param)).map(x -> x.getValue());
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

    public VirtualServiceMessageRequest(long id,  String input, String output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }


    public VirtualServiceMessageRequest() {}


    public List<VirtualServiceKeyValue> getAvailableParams() {
        return availableParams;
    }

    public void setAvailableParams(List<VirtualServiceKeyValue> availableParams) {
        this.availableParams = availableParams;
    }

    @Override
    public String toString() {
        return "VirtualServiceMessageRequest{" +
            "id=" + id +
            ", requestTopicOrQueueName='" + requestTopicOrQueueName + '\'' +
            ", httpStatusCode='" + httpStatusCode + '\'' +
            ", brokerUrl='" + brokerUrl + '\'' +
            ", type='" + type + '\'' +
            ", requestType='" + requestType + '\'' +
            ", usageCount=" + usageCount +
            ", contentType=" + contentType +
            ", priority=" + priority +
            ", responseTopicOrQueueName='" + responseTopicOrQueueName + '\'' +
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
            ", httpStatusMap=" + httpStatusMap +
            '}';
    }
}
