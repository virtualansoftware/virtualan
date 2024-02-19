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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.core.model.ResourceMapper;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * This class provide support for cxf web services.
 *
 * @author  Elan Thangamani
 * 
 */

@Service("nonRestVirtualServiceInfo")
@Slf4j
public class NonRestVirtualServiceInfo implements VirtualServiceInfo {

    @Autowired
    private ObjectMapper objectMapper;


    ResourceMapper resourceParent;

    Map<String, Map<String, VirtualServiceRequest>> mockLoadChoice;

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
        log.info("not used");
    }

    @Override
    public Map<String, Class> findVirtualServices() {
        return null;
    }


    public static Predicate<VirtualServiceKeyValue> isParam(String key) {
        return value -> value.getKey().equalsIgnoreCase(key);
    }

}
