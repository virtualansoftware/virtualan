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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.virtualan.annotation.ApiVirtual;
import io.virtualan.api.ApiMethod;
import io.virtualan.api.ApiResource;
import io.virtualan.core.model.ResourceMapper;
import io.virtualan.core.model.VirtualServiceApiResponse;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;


/**
 * This interface is base for all VirtualService types
 *
 *
 * @author  Elan Thangamani
 * @see     io.virtualan.core.OpenApiVirtualServiceInfo
 * @see     io.virtualan.core.SpringVirtualServiceInfo
 */

public interface VirtualServiceInfo {

    @Slf4j
    final class LogHolder
    {}


    ObjectMapper getObjectMapper();

    void setObjectMapper(ObjectMapper objectMapper);

    ResourceMapper getResourceParent();

    void setResourceParent(ResourceMapper resourceParent);

    Map<String, Map<String, VirtualServiceRequest>> getMockLoadChoice();

    void setMockLoadChoice(Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice);

    void buildInput(Method method, VirtualServiceRequest mockLoadRequest)
        throws JsonProcessingException, InstantiationException, IllegalAccessException,
        ClassNotFoundException;


    default String getResourceDesc(Method method) {
        Operation[] apiOperationAnno = method.getAnnotationsByType(Operation.class);
        if (apiOperationAnno != null && apiOperationAnno.length > 0) {
            return apiOperationAnno[0].description();
        }
        return null;
    }
    abstract Map<String, Class> findVirtualServices();

    default Map<String, Map<String, VirtualServiceRequest>> loadVirtualServices(boolean scriptEnabled)
        throws ClassNotFoundException, JsonProcessingException, InstantiationException,
        IllegalAccessException {
        Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice = getMockLoadChoice();
        if (mockLoadChoice == null) {
            mockLoadChoice = new TreeMap<>();
            for (Map.Entry<String, Class> virtualServiceEntry : findVirtualServices().entrySet()) {
                Map<String, VirtualServiceRequest> mockAPILoadChoice =
                    buildVirtualServiceInfo(scriptEnabled, virtualServiceEntry);
                if (!mockAPILoadChoice.isEmpty()) {
                    Map<String, Map<String, VirtualServiceRequest>> resourceGroup =
                        mockAPILoadChoice.entrySet().stream().filter(x -> !"virtualanendpoint".equalsIgnoreCase(x.getKey()))
                            .collect(Collectors.groupingBy(f -> f.getValue().getResource(),
                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    mockLoadChoice.putAll(resourceGroup);
                }
            }
        }
        setMockLoadChoice(mockLoadChoice);
        return mockLoadChoice;
    }

    default Map<String, VirtualServiceRequest> buildVirtualServiceInfo(boolean scriptEnabled,
        Map.Entry<String, Class> virtualServiceEntry) throws JsonProcessingException,
        InstantiationException, IllegalAccessException, ClassNotFoundException {
        Map<String, VirtualServiceRequest> mockAPILoadChoice =
            new LinkedHashMap<>();
        for (Method method : virtualServiceEntry.getValue().getDeclaredMethods()) {
            ApiVirtual[] annotInstance = method.getAnnotationsByType(ApiVirtual.class);
            if (annotInstance != null && annotInstance.length > 0) {
                VirtualServiceRequest mockReturn = buildServiceDetails(scriptEnabled, virtualServiceEntry, method);
                if (mockReturn != null) {
                    mockAPILoadChoice.put(method.getName(), mockReturn);
                }
            }
        }
        return mockAPILoadChoice;
    }

    default Class getInputType(VirtualServiceRequest mockTransferInput) {
        Class inputType = null;

        if (mockTransferInput.getResource() == null) {
            mockTransferInput.setResource(ApiResource.getResourceByURL(mockTransferInput.getUrl()));
        }
        if (mockTransferInput.getOperationId() != null) {
            VirtualServiceRequest mockTransferActual = getMockLoadChoice()
                .get(mockTransferInput.getResource()).get(mockTransferInput.getOperationId());
            if (mockTransferActual != null) {
                inputType = mockTransferActual.getInputObjectType();
            }
        } else {
            String resourceUrl =
                mockTransferInput.getUrl().substring(1);
            List<String> resouceSplitterList =
                new LinkedList(Arrays.asList(resourceUrl.split("/")));
            if (!resouceSplitterList.isEmpty()) {
                String operationId = getOperationId(mockTransferInput.getMethod(),
                    getResourceParent(), resouceSplitterList);
                VirtualServiceRequest mockTransferActual =
                    getMockLoadChoice().get(mockTransferInput.getResource()).get(operationId);
                if (mockTransferActual != null) {
                    inputType = mockTransferActual.getInputObjectType();
                }
            }
        }
        return inputType;
    }

    default Map<String, VirtualServiceApiResponse> buildOpenAPIResponseType(Method method) {
        Map<String, VirtualServiceApiResponse> responseType = new HashMap<>();
        ApiResponses[] apiResponsesAnno = method.getAnnotationsByType(ApiResponses.class);
        if (apiResponsesAnno != null) {
            for (ApiResponses apiResponses : apiResponsesAnno) {
                for (ApiResponse apiResponse : apiResponses.value()) {
                    try {
                        if(!responseType.containsKey(String.valueOf(apiResponse.responseCode()))) {
                            responseType.put(String.valueOf(apiResponse.responseCode()),
                                    new VirtualServiceApiResponse(String.valueOf(apiResponse.responseCode()),
                                            null, null, apiResponse.description()));
//TODO                            responseType
//                                .put(String.valueOf(apiResponse.responseCode()),
//                                    new VirtualServiceApiResponse(
//                                        String.valueOf(apiResponse.responseCode()),
//                                        apiResponse.response().getCanonicalName(),
//                                        getObjectMapper().writerWithDefaultPrettyPrinter()
//                                            .writeValueAsString(Class
//                                                .forName(apiResponse.response()
//                                                    .getCanonicalName())
//                                                .getDeclaredConstructor().newInstance()),
//                                        apiResponse.description()));
                        }
                    } catch (Exception e) {
                        responseType.put(String.valueOf(apiResponse.responseCode()),
                            new VirtualServiceApiResponse(String.valueOf(apiResponse.responseCode()),
                                null, null, apiResponse.description()));
                    }
                }
            }

        }
        return responseType;
    }

    default String getOperationId(String httpVerb, ResourceMapper resourceParent,
        List<String> resouceSplitterList) {
        if (resouceSplitterList.isEmpty()) {
            return resourceParent.getOperationId(httpVerb);
        }
        String resource = resouceSplitterList.get(0);
        ResourceMapper mapper = resourceParent.findResource(resource);
        if (mapper != null) {
            return getOperationId(httpVerb, mapper,
                resouceSplitterList.subList(1, resouceSplitterList.size()));
        } else {
            return getOperationId(httpVerb, resourceParent.findResource(VirtualServiceConstants.CURLY_PATH),
                resouceSplitterList.subList(1, resouceSplitterList.size()));
        }

    }

    // RE THINK ABOUT IT
    default VirtualServiceRequest getResponseType(VirtualServiceRequest mockTransferInput) {
        if(mockTransferInput != null && mockTransferInput.getUrl() != null) {
            int index = mockTransferInput.getUrl().indexOf('/', 1) == -1
                ? mockTransferInput.getUrl().length() : mockTransferInput.getUrl().indexOf('/', 1);
            if (mockTransferInput.getResource() == null) {
                mockTransferInput.setResource(mockTransferInput.getUrl().substring(1, index));
            }
        }
        if (mockTransferInput != null && mockTransferInput.getOperationId() != null) {
            return getMockLoadChoice().get(mockTransferInput.getResource())
                .get(mockTransferInput.getOperationId());
        }
        return null;
    }



    default Map<String, String> getTypes(boolean scriptEnabled) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("RESPONSE", "Response");
        map.put("PARAMS", "Params");
        if(scriptEnabled) {
            map.put("RULE", "Rule");
            map.put("SCRIPT", "Script");
        }
        return map;
    }


    default Map<String, String> getHttpStatusMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (HttpStatus status : HttpStatus.values()) {
            map.put(String.valueOf(status.value()), status.name());
        }
        return map;
    }

    default ResourceMapper loadMapper() {
        Set<ResourceMapper> resourceMapperList = new LinkedHashSet<>();
        ResourceMapper resourceParent = new ResourceMapper(VirtualServiceConstants.PARENT_ROOT, resourceMapperList);
        for (Entry<String, Map<String, VirtualServiceRequest>> obj : getMockLoadChoice()
            .entrySet()) {
            for (Entry<String, VirtualServiceRequest> requestMockObject : obj.getValue()
                .entrySet()) {
                String resource = requestMockObject.getValue().getUrl().substring(1
                );
                List<String> resouceSplitterList =
                    new LinkedList(Arrays.asList(resource.split("/")));
                if (!resouceSplitterList.isEmpty()) {
                    ResourceMapper mapperChild = buildHierarchyObject(
                        requestMockObject.getValue().getMethod(), resourceParent,
                        resouceSplitterList, requestMockObject.getKey());
                    resourceParent.addResourceMapper(mapperChild);
                }
            }
        }
        return resourceParent;
    }

    default ResourceMapper buildHierarchyObject(String httpVerb, ResourceMapper resourceParent,
        List<String> resouceSplitterList, String operationId) {
        String resource = resouceSplitterList.get(0);
        String actualResource = resouceSplitterList.get(0);
        final Matcher matcher = VirtualServiceConstants.pattern.matcher(resouceSplitterList.get(0));
        if (matcher.find()) {
            resource = VirtualServiceConstants.CURLY_PATH;
        }
        if (resouceSplitterList.size() == 1) {
            ResourceMapper resourceMapper = resourceParent.findResource(resource);
            if (resourceMapper == null) {
                resourceMapper = new ResourceMapper(resource);
                resourceMapper.setActualResource(actualResource);
            }
            resourceMapper.setOperationId(httpVerb, operationId);
            return resourceMapper;
        } else if (resourceParent.findResource(resource) != null) {
            ResourceMapper resourceChild = resourceParent.findResource(resource);
            ResourceMapper resourceMapperReturn = buildHierarchyObject(httpVerb, resourceChild,
                resouceSplitterList.subList(1, resouceSplitterList.size()), operationId);
            resourceChild.addResourceMapper(resourceMapperReturn);
            return resourceChild;
        } else {
            Set<ResourceMapper> mapperSet = new LinkedHashSet<>();
            ResourceMapper resourceMapper = new ResourceMapper(resource, mapperSet);
            resourceMapper.setActualResource(actualResource);
            ResourceMapper resourceMapperReturn = buildHierarchyObject(httpVerb, resourceMapper,
                resouceSplitterList.subList(1, resouceSplitterList.size()), operationId);
            resourceMapper.addResourceMapper(resourceMapperReturn);

            return resourceMapper;
        }
    }

    default Map<String, VirtualServiceApiResponse> buildResponseType(Method method)
        throws JsonProcessingException, InstantiationException, IllegalAccessException,
        ClassNotFoundException {
        Map<String, VirtualServiceApiResponse> responseType = buildOpenAPIResponseType(method);
        if (responseType == null || responseType.isEmpty()) {
            responseType = new HashMap<>();
            String defaultResponse = "Default";
            // Check for generic return types
            if (method.getGenericReturnType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType =
                    (ParameterizedType) method.getGenericReturnType();
                for (Type type : parameterizedType.getActualTypeArguments()) {
                    try {
                        responseType.put(defaultResponse,
                            new VirtualServiceApiResponse(defaultResponse, type.getTypeName(),
                                getObjectMapper().writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(Class
                                        .forName(type.getTypeName()).getDeclaredConstructor().newInstance()),
                                null));
                    } catch (Exception e) {
                        responseType.put(defaultResponse,
                            new VirtualServiceApiResponse(defaultResponse, null, null, null));
                    }
                }
            }
        }
        return responseType;
    }

    default VirtualServiceRequest buildServiceDetails(boolean scriptEnabled, Entry<String, Class> virtualServiceEntry,
        Method method) throws JsonProcessingException, InstantiationException,
        IllegalAccessException, ClassNotFoundException {

        String rootResource = ApiResource.getResourceParent(virtualServiceEntry.getValue());
        VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
        virtualServiceRequest.setDesc(getResourceDesc(method));
        virtualServiceRequest.setResponseType(buildResponseType(method));
        virtualServiceRequest.setOperationId(method.getName());
        virtualServiceRequest.setTypes(getTypes(scriptEnabled));
        virtualServiceRequest.setHttpStatusMap(getHttpStatusMap());
        VirtualServiceKeyValue virtualServiceKeyValue = ApiMethod.getApiMethodParamAndURL(method);
        if(rootResource != null) {
            virtualServiceRequest.setUrl("/"+rootResource + virtualServiceKeyValue.getValue());
            virtualServiceRequest.setResource(rootResource);
        } else {
            virtualServiceRequest.setUrl(virtualServiceKeyValue.getValue());
            virtualServiceRequest
                .setResource(ApiResource.getResourceByURL(virtualServiceKeyValue.getValue()));
        }
        virtualServiceRequest.setMethod(virtualServiceKeyValue.getKey());
        buildInput(method, virtualServiceRequest);
        return virtualServiceRequest;
    }

}
