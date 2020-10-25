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

package io.virtualan.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.api.ApiType;
import io.virtualan.core.model.ResourceMapper;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.requestbody.RequestBodyTypes;

/**
 * This class provide support for spring web services.
 *
 * @author  Elan Thangamani
 * 
 */
@Service("springVirtualServiceInfo")
@Slf4j
public class SpringVirtualServiceInfo implements VirtualServiceInfo {

    @Autowired
    private ObjectMapper objectMapper;

    ResourceMapper resourceParent;

    Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice;

    @Autowired
    private ApiType apiType;


    public ApiType getApiType() {
        return apiType;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResourceMapper getResourceParent() {
        return resourceParent;
    }

    @Override
    public void setResourceParent(ResourceMapper resourceParent) {
        this.resourceParent = resourceParent;
    }

    @Override
    public Map<String, Map<String, VirtualServiceRequest>> getMockLoadChoice() {
        return mockLoadChoice;
    }

    @Override
    public void setMockLoadChoice(Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice) {
        this.mockLoadChoice = mockLoadChoice;
    }


    @Override
    public void buildInput(Method method, VirtualServiceRequest mockLoadRequest)
            throws
            ClassNotFoundException {
        int i = 0;
        List<VirtualServiceKeyValue> availableParams = new ArrayList();
        Annotation[][] annotations = method.getParameterAnnotations();
        Class[] parameterTypes = method.getParameterTypes();
        for (Annotation[] anns : annotations) {
            Class parameterType = parameterTypes[i++];
            for (Annotation paramAnnotation : anns) {
                if (paramAnnotation.annotationType().equals(RequestParam.class)) {
                    RequestParam requestParam = (RequestParam) paramAnnotation;
                    availableParams.add(new VirtualServiceKeyValue(requestParam.value(), null, "QUERY_PARAM"));
                }else if (paramAnnotation.annotationType().equals(PathVariable.class)) {
                    PathVariable pathVariable = (PathVariable) paramAnnotation;
                    availableParams.add(new VirtualServiceKeyValue(pathVariable.value(), null, "PATH_PARAM"));
                } else if (paramAnnotation.annotationType().equals(RequestBody.class)) {
                    io.virtualan.requestbody.RequestBody requestBody =
                            new io.virtualan.requestbody.RequestBody();
                    requestBody.setInputObjectTypeName(
                            Class.forName(parameterType.getName()).getTypeName());
                    requestBody.setInputObjectType(parameterType);
                    requestBody.setObjectMapper(objectMapper);
                    mockLoadRequest.setInputObjectType(Class.forName(parameterType.getName()));
                    try {
                        mockLoadRequest.setInput(
                                RequestBodyTypes.fromString(requestBody.getInputObjectTypeName())
                                        .getDefaultMessageBody(requestBody));
                    } catch (IOException e) {

                        // TO-DO
                    }
                }
            }
        }
        mockLoadRequest.setAvailableParams(availableParams);
    }
}
