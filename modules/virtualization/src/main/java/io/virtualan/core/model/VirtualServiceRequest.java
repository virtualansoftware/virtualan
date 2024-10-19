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
import java.util.Calendar;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(long usageCount) {
        this.usageCount = usageCount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class getInputObjectType() {
        return inputObjectType;
    }

    public void setInputObjectType(Class inputObjectType) {
        this.inputObjectType = inputObjectType;
    }

    public Class getResponseObjectType() {
        return responseObjectType;
    }

    public void setResponseObjectType(Class responseObjectType) {
        this.responseObjectType = responseObjectType;
    }

    public String getOutputObjectType() {
        return outputObjectType;
    }

    public void setOutputObjectType(String outputObjectType) {
        this.outputObjectType = outputObjectType;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public List<VirtualServiceKeyValue> getAvailableParams() {
        return availableParams;
    }

    public void setAvailableParams(List<VirtualServiceKeyValue> availableParams) {
        this.availableParams = availableParams;
    }

    public List<VirtualServiceKeyValue> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(List<VirtualServiceKeyValue> headerParams) {
        this.headerParams = headerParams;
    }

    public Map<String, VirtualServiceApiResponse> getResponseType() {
        return responseType;
    }

    public void setResponseType(Map<String, VirtualServiceApiResponse> responseType) {
        this.responseType = responseType;
    }

    public String getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(String excludeList) {
        this.excludeList = excludeList;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VirtualServiceStatus getMockStatus() {
        return mockStatus;
    }

    public void setMockStatus(VirtualServiceStatus mockStatus) {
        this.mockStatus = mockStatus;
    }

    public Calendar getLastUsedDateTime() {
        return lastUsedDateTime;
    }

    public void setLastUsedDateTime(Calendar lastUsedDateTime) {
        this.lastUsedDateTime = lastUsedDateTime;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getTypes() {
        return types;
    }

    public void setTypes(Map<String, String> types) {
        this.types = types;
    }

    public Map<String, String> getHttpStatusMap() {
        return httpStatusMap;
    }

    public void setHttpStatusMap(Map<String, String> httpStatusMap) {
        this.httpStatusMap = httpStatusMap;
    }

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
