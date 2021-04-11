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
import lombok.Data;


/**
 * Mock service request to virtualan.
 * 
 * 
 * @author  Elan Thangamani
 * 
 **/
@Data
public class MockServiceRequest {
    private String resource = null;
    private String operationId = null;
    private RequestType requestType = null;
    private String type = null;
    private Map<String, String> params = null;
    private Map<String, Class> paramsType = null;
    private Map<String, Object> parameters = null;
    private Map<String, String> headerParams = null;
    private Class inputObjectType = null;
    private Class responseObjectType;
    private Object input = null;
    private Object output = null;
    private Object rule = null;
    private ContentType contentType;
    private String excludeFields;

    public String getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    public Object getHeaderParam(String param) {
        return getHeaderParams().entrySet().stream()
            .filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
    }

    public Object getParam(String param) {
        Optional<Object> obj = Optional.ofNullable(getAvailableParam(param));
        if( !obj.isPresent()) {
            obj=  getParameters().entrySet().stream().filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
        }
        return obj.isPresent() ? obj.get() : null;
    }

    private Optional<String> getAvailableParam(String param) {
        return getParams().entrySet().stream()
            .filter(e -> param.equals(e.getKey())).map(Map.Entry::getValue).findFirst();
    }


    @Override
    public String toString() {
        return "MockServiceRequest{" +
            "resource='" + resource + '\'' +
            ", operationId='" + operationId + '\'' +
            ", requestType=" + requestType +
            ", type='" + type + '\'' +
            ", params=" + params +
            ", paramsType=" + paramsType +
            ", parameters=" + parameters +
            ", headerParams=" + headerParams +
            ", inputObjectType=" + inputObjectType +
            ", input=" + input +
            ", rule=" + rule +
            ", contentType=" + contentType +
            '}';
    }
}

