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
import java.util.function.Predicate;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.api.ApiType;
import io.virtualan.core.model.ResourceMapper;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.requestbody.RequestBodyTypes;



/**
 * This class provide support for cxf web services.
 *
 * @author  Elan Thangamani
 * 
 */

@Service("cxfVirtualServiceInfo")
public class CxfVirtualServiceInfo implements VirtualServiceInfo {

    private static final Logger log = LoggerFactory.getLogger(CxfVirtualServiceInfo.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiType apiType;

    ResourceMapper resourceParent;

    Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice;

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
    public void buildInput(Method method, VirtualServiceRequest mockLoadRequest) {
        int i = 0;
        List<VirtualServiceKeyValue> availableParams = new ArrayList();
        Annotation[][] annotations = method.getParameterAnnotations();
        Class[] parameterTypes = method.getParameterTypes();
        for (Annotation[] anns : annotations) {
            Class parameterType = parameterTypes[i++];
            if (anns != null && anns.length > 0) {
                String requestParamName = null;
                for (Annotation paramAnnotation : anns) {
                    if (paramAnnotation.annotationType().equals(QueryParam.class)) {
                        QueryParam requestParam = (QueryParam) paramAnnotation;
                        requestParamName = requestParam.value();
                    } else if (paramAnnotation.annotationType().equals(PathParam.class)) {
                        PathParam pathVariable = (PathParam) paramAnnotation;
                        requestParamName = pathVariable.value();
                    } else if (paramAnnotation.annotationType().equals(FormParam.class)) {
                        FormParam formVariable = (FormParam) paramAnnotation;
                        requestParamName = formVariable.value();
                    } else if (paramAnnotation.annotationType().equals(HeaderParam.class)) {
                        HeaderParam headerVariable = (HeaderParam) paramAnnotation;
                        requestParamName = headerVariable.value();
                    } else if (paramAnnotation.annotationType().equals(MatrixParam.class)) {
                        MatrixParam matrixVariable = (MatrixParam) paramAnnotation;
                        requestParamName = matrixVariable.value();
                    } else if (paramAnnotation.annotationType().equals(CookieParam.class)) {
                        CookieParam cookieVariable = (CookieParam) paramAnnotation;
                        requestParamName = cookieVariable.value();
                    } else if (paramAnnotation.annotationType().equals(Multipart.class)) {
                        Multipart multipartVariable = (Multipart) paramAnnotation;
                        requestParamName = multipartVariable.value();
                    } else if (requestParamName == null && parameterType != null) {
                        io.virtualan.requestbody.RequestBody requestBody =
                                new io.virtualan.requestbody.RequestBody();
                        requestBody.setInputObjectTypeName(parameterType.getTypeName());
                        requestBody.setInputObjectType(parameterType);
                        requestBody.setObjectMapper(objectMapper);
                        mockLoadRequest.setInputObjectType(parameterType);
                        try {
                            mockLoadRequest.setInput(RequestBodyTypes
                                    .fromString(requestBody.getInputObjectTypeName())
                                    .getDefaultMessageBody(requestBody));
                        } catch (IOException e) {
                            // TO-DO
                        }
                    }
                    if (requestParamName != null) {
                        availableParams.add(readParam(parameterType, requestParamName));
                        break;
                    }

                }
            }
        }
        mockLoadRequest.setAvailableParams(availableParams);
    }

    private VirtualServiceKeyValue readParam(Class parameterType, String requestParamValue) {
        VirtualServiceKeyValue virtualServiceKeyValue = new VirtualServiceKeyValue();
        virtualServiceKeyValue.setKey(requestParamValue);
        virtualServiceKeyValue.setType(parameterType);
        return virtualServiceKeyValue;
    }

    public static Predicate<VirtualServiceKeyValue> isParam(String key) {
        return value -> value.getKey().equalsIgnoreCase(key);
    }

}
