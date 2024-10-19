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

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * This is Mock  request data for virtualan.
 * 
 * 
 * @author  Elan Thangamani
 * 
 **/

public class MockRequest {
    private long virtualServiceId;
    private long usageCount;
    private String input;
    private String rule;
    private String type;
    private ResponseProcessType responseProcessType;
    private Set excludeSet;
    //Kafka Outbound Topic
    private String method;
    private ContentType contentType;
    private List<VirtualServiceKeyValue> availableParams;
    private List<VirtualServiceKeyValue> headerParams;
    public MockRequest(){

    }
    public MockRequest(String input, Set excludeSet, List<VirtualServiceKeyValue> availableParams) {
        this.input = input;
        this.excludeSet = excludeSet;
        this.availableParams = availableParams;
    }

    @Override
    public String toString() {
        return "MockRequest{" +
            "virtualServiceId=" + virtualServiceId +
            ", usageCount=" + usageCount +
            ", input='" + input + '\'' +
            ", rule='" + rule + '\'' +
            ", type='" + type + '\'' +
            ", excludeSet=" + excludeSet +
            ", method='" + method + '\'' +
            ", contentType='" + contentType + '\'' +
            ", availableParams=" + availableParams +
            ", headerParams=" + headerParams +
            '}';
    }

    public long getVirtualServiceId() {
        return virtualServiceId;
    }

    public void setVirtualServiceId(long virtualServiceId) {
        this.virtualServiceId = virtualServiceId;
    }

    public long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(long usageCount) {
        this.usageCount = usageCount;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ResponseProcessType getResponseProcessType() {
        return responseProcessType;
    }

    public void setResponseProcessType(ResponseProcessType responseProcessType) {
        this.responseProcessType = responseProcessType;
    }

    public Set getExcludeSet() {
        return excludeSet;
    }

    public void setExcludeSet(Set excludeSet) {
        this.excludeSet = excludeSet;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
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
}
