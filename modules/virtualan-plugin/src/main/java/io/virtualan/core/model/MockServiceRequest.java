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

import java.util.Map;
import java.util.Optional;


/**
 * Mock service request to virtualan.
 * 
 * 
 * @author  Elan Thangamani
 * 
 **/
public class MockServiceRequest {
    String resource = null;
    String operationId = null;
    RequestType requestType = null;
    String type = null;
    Map<String, String> params = null;
    Map<String, Class> paramsType = null;
    Map<String, Object> parameters = null;
    Map<String, String> headerParams = null;
    Class inputObjectType = null;
    Object input = null;
    Object rule = null;
    
    public Object getRule() {
        return rule;
    }
    
    public void setRule(Object rule) {
        this.rule = rule;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Map<String, Class> getParamsType() {
        return paramsType;
    }

    public void setParamsType(Map<String, Class> paramsType) {
        this.paramsType = paramsType;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(Map<String, String> headerParams) {
        this.headerParams = headerParams;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Object getHeaderParam(String param) {
        return getHeaderParams().entrySet().stream()
            .filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
    }

    public Object getParam(String param) {
        Optional<Object> obj = Optional.ofNullable(getAvailableParam(param));
        if( obj == null) {
            obj=  getParameters().entrySet().stream().filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
        }
        return obj.isPresent() ? obj.get() : null;
    }

    public Optional<String> getAvailableParam(String param) {
        return getParams().entrySet().stream()
            .filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
    }



    public Class getInputObjectType() {
        return inputObjectType;
    }

    public void setInputObjectType(Class inputObjectType) {
        this.inputObjectType = inputObjectType;
    }

    @Override
    public String toString() {
        return "MockServiceRequest [resource=" + resource + ", operationId=" + operationId
                + ", params=" + params + ", paramsType=" + paramsType + ", headerParams="
                + headerParams + ", inputObjectType=" + inputObjectType + ", inputObject="
                + input + "]";
    }
}

