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

package io.virtualan.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import io.virtualan.annotation.VirtualService;
import io.virtualan.core.model.VirtualServiceKeyValue;

/**
 *  This ApiType class identify what type of REST service would be implemented.
 * 
 * @author  Elan Thangamani
 * 
 **/
@Component
public class ApiType {


    final static Logger log = LoggerFactory.getLogger(ApiType.class);


    private static VirtualServiceType getType(Class clazz) {
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            return VirtualServiceType.SPRING;
        } else if (clazz.isAnnotationPresent(Path.class)) {
            return VirtualServiceType.CXF_JAX_RS;
        }
        return null;
    }


    public static VirtualServiceType findApiType() {
        Map<String, Class> virtualInterfaces = new HashMap<>();
        try {
            Field f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            List<Class> classes = (List<Class>) f.get(classLoader);
            for (int i = 0; i < classes.size(); i++) {
                Class classzz = classes.get(i);
                try {
                    if (classzz.isAnnotationPresent(VirtualService.class)) {
                        VirtualServiceType apiType = getType(classzz);
                        if (apiType == null) {
                            for (Method method : classzz.getDeclaredMethods()) {
                                VirtualServiceKeyValue virtualServiceKeyValue =
                                        ApiMethod.getApiMethodParamAndURL(method);
                                if (virtualServiceKeyValue != null
                                        && virtualServiceKeyValue.getServiceType() != null) {
                                    log.info(" Virtualan Api Type would be : "
                                            + virtualServiceKeyValue.getServiceType().getType());
                                    return virtualServiceKeyValue.getServiceType();
                                }
                            }
                        } else {
                            log.info(" Virtualan Api Type would be : " + apiType.getType());
                            return apiType;
                        }
                    }
                } catch (ArrayStoreException e) {
                    // ignore it
                }

            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e1) {
            log.error("Unable to load from the class loader " + e1.getMessage());
        }
        log.error(
                "Unable to find Api Type: Service would not meet the Virtualan required criteria!!! ");
        return null;
    }



    public static List<String> findApis() {
        List<String> apis = new ArrayList<>();
        try {
            Field f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            List<Class> classes = (List<Class>) f.get(classLoader);
            for (int i = 0; i < classes.size(); i++) {
                Class classzz = classes.get(i);
                try {
                    if (classzz.isAnnotationPresent(VirtualService.class)) {
                        apis.add(classzz.getPackage().getName());
                    }
                } catch (ArrayStoreException e) {
                    // ignore it
                }

            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException e1) {
            log.error("Unable to load from the class loader " + e1.getMessage());
        }
        log.error(
                "Unable to find Api Type: Service would not meet the Virtualan required criteria!!! ");
        return apis;
    }


}
