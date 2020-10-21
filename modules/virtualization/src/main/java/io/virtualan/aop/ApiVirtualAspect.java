/*
 * Copyright 2020 Virtualan Contributors (https://virtualan.io)
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


package io.virtualan.aop;

import io.virtualan.api.WSResource;
import io.virtualan.core.model.RequestType;
import io.virtualan.core.VirtualServiceInfo;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.annotation.VirtualService;
import io.virtualan.api.ApiResource;
import io.virtualan.api.VirtualServiceType;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.custom.message.ResponseException;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;


/**
 * 
 * This Aspect is core class to intercept @VirtualService and @ApiVirtual annotation to produce the response for the virtualized methods. 
 *  
 * Annotate class with @VirtualService and @ApiVirtual and make that service as virtualized service  
 *
 * @author  Elan Thangamani
 * 
 */
@Aspect
@Component
public class ApiVirtualAspect {

    private static Logger log = LoggerFactory.getLogger(VirtualServiceInfo.class);
    @Autowired
    HttpServletRequest request;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VirtualServiceUtil virtualServiceUtil;

    public VirtualServiceUtil getVirtualServiceUtil() {
        return virtualServiceUtil;
    }

    public String addQueryParamValue(Object value) {
        return String.join(",", (java.util.List) value);
    }

    @Pointcut("@annotation(io.virtualan.annotation.ApiVirtual)")
    public void apiVirtualServicePointcut() {
    }

    @Around("apiVirtualServicePointcut()")
    public Object aroundAddAdvice(ProceedingJoinPoint thisJoinPoint)
        throws ResponseException, IOException, SOAPException, JAXBException, JSONException{
        MockServiceRequest mockServiceRequest = new MockServiceRequest();

        Object[] args = thisJoinPoint.getArgs();
        MethodSignature methodSignature =
                (MethodSignature) thisJoinPoint.getStaticPart().getSignature();
        Method method = methodSignature.getMethod();

        Class targetClass = thisJoinPoint.getTarget().getClass();

        SimpleEntry<Boolean, Class> isVirtualan = isVirtualService(targetClass);
        if (isVirtualan.getKey()) {
            String parentPath = null;
            if (WSResource.isExists(method)){
                SimpleEntry<String, String> path =  WSResource.getResourceParent(method);
                mockServiceRequest.setResource(path.getValue());
                mockServiceRequest.setOperationId(method.getName());
                mockServiceRequest.setRequestType(RequestType.SOAP);
                readWSInputParam(args, methodSignature, mockServiceRequest);
            }else {
                parentPath = ApiResource.getResourceParent(isVirtualan.getValue());
                if(mockServiceRequest.getResource() == null) {
                    mockServiceRequest.setResource(ApiResource.getResource(method));
                } else {
                    mockServiceRequest.setResource(parentPath);
                }
                mockServiceRequest.setOperationId(method.getName());
                readInputParam(args, methodSignature, mockServiceRequest);

                Map<String, String> headersInfo = getHeadersInfo();
                mockServiceRequest.setHeaderParams(headersInfo);
            }
        }
        return getVirtualServiceUtil().returnResponse(method, mockServiceRequest);
    }

    public SimpleEntry<Boolean, Class> isVirtualService(Class<?> claszzz) {
        if(claszzz.isAnnotationPresent(VirtualService.class) ) {
            return new SimpleEntry<>(true, claszzz);
        } else {
            for (Class clazz : claszzz.getInterfaces()) {
                if (clazz.isAnnotationPresent(VirtualService.class)) {
                    return new SimpleEntry<>(true, clazz);
                }
            }
        }
        return new SimpleEntry<>(false, Object.class);
    }


    private Map<String, String> getHeadersInfo() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


    private void readWSInputParam(Object[] args, MethodSignature methodSignature,
        MockServiceRequest mockServiceRequest) {
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            if (parameterAnnotations[argIndex] != null
                && parameterAnnotations[argIndex].length > 0) {
                String requestParamName = null;
                for (Annotation annotation : parameterAnnotations[argIndex]) {
                    if (annotation instanceof RequestPayload) {
                        try {
                            mockServiceRequest.setInputObjectType(Class.forName(
                                (methodSignature.getParameterTypes()[argIndex]).getName()));
                            mockServiceRequest.setInput(args[argIndex]);
                        } catch (ClassNotFoundException e) {
                            log.error(e.getMessage());
                        }
                        mockServiceRequest.setInput(args[argIndex]);
                    }
                }
            }
        }

    }


    // TODO - Code Array List
    private void readInputParam(Object[] args, MethodSignature methodSignature,
            MockServiceRequest mockServiceRequest) {
        Map<String, String> paramMap = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
    
        Map<String, Class> paramMapType = new HashMap<>();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        assert args.length == parameterAnnotations.length;

        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            if (parameterAnnotations[argIndex] != null
                    && parameterAnnotations[argIndex].length > 0) {
                String requestParamName = null;
                for (Annotation annotation : parameterAnnotations[argIndex]) {
                    if (annotation instanceof RequestParam) {
                        RequestParam requestParam = (RequestParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof PathVariable) {
                        PathVariable requestParam = (PathVariable) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof RequestBody) {
                         try {
                            mockServiceRequest.setInputObjectType(Class.forName(
                                    (methodSignature.getParameterTypes()[argIndex]).getName()));
                            mockServiceRequest.setInput(args[argIndex]);
                        } catch (ClassNotFoundException e) {
                             log.error(e.getMessage());
                        }
                        mockServiceRequest.setInput(args[argIndex]);

                    } else if (annotation instanceof QueryParam) {
                        QueryParam requestParam = (QueryParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof PathParam) {
                        PathParam requestParam = (PathParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof FormParam) {
                        FormParam requestParam = (FormParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof HeaderParam) {
                        HeaderParam requestParam = (HeaderParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof MatrixParam) {
                        MatrixParam requestParam = (MatrixParam) annotation;
                        requestParamName = requestParam.value();
                    } else if (annotation instanceof CookieParam) {
                        CookieParam requestParam = (CookieParam) annotation;
                        requestParamName = requestParam.value();
//                    } else if (annotation instanceof Multipart) {
//                        Multipart requestParam = (Multipart) annotation;
//                        requestParamName = requestParam.value();
                    } else if (requestParamName == null && VirtualServiceType.CXF_JAX_RS
                            .equals(getVirtualServiceUtil().getVirtualServiceType())) {
                        try {
                            if (mockServiceRequest.getInputObjectType() == null) {
                                mockServiceRequest.setInputObjectType(Class.forName(
                                        (methodSignature.getParameterTypes()[argIndex]).getName()));
                                mockServiceRequest.setInput(args[argIndex]);
                            }
                        } catch (ClassNotFoundException e) {
                            log.error(e.getMessage());
                        }
                        break;
                    }
                    if (requestParamName != null) {
                        if ((args[argIndex]) instanceof List) {
                            paramMap.put(requestParamName, addQueryParamValue(args[argIndex]));
                            parameters.put(requestParamName,addQueryParamValue(args[argIndex]));
                        } else {
                            paramMap.put(requestParamName, String.valueOf(args[argIndex]));
                            parameters.put(requestParamName, args[argIndex]);
                        }
                        if (args[argIndex] != null) {
                            paramMapType.put(requestParamName, (args[argIndex]).getClass());
                        }
                        break;
                    }
                }
            }
        }

        mockServiceRequest.setParams(paramMap);
        mockServiceRequest.setParameters(parameters);
        mockServiceRequest.setParamsType(paramMapType);
    }

}
