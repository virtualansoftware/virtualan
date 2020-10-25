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

import io.virtualan.core.model.VirtualServiceKeyValue;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import java.lang.reflect.Method;

/**
 *  This ApiMethod class identify the rest operation type for the selected operation.
 * 
 * @author  Elan Thangamani
 * 
 **/
public class ApiMethod {
    private ApiMethod(){}

    public static String getApiMethodParam(Method method) {
        return getApiMethodParamAndURL(method).getKey();
    }

    public static VirtualServiceKeyValue getApiMethodParamAndURL(Method method) {
        VirtualServiceKeyValue virtualServiceKeyValue = getSpringMethod(method);
        if (virtualServiceKeyValue != null) {
            return virtualServiceKeyValue;
        }
        virtualServiceKeyValue = getGetMethod(method);
        if (virtualServiceKeyValue != null) {
            return virtualServiceKeyValue;
        }
        virtualServiceKeyValue = getDeleteMethod(method);
        if (virtualServiceKeyValue != null) {
            return virtualServiceKeyValue;
        }
        virtualServiceKeyValue = getPutMethod(method);
        if (virtualServiceKeyValue != null) {
            return virtualServiceKeyValue;
        }
        virtualServiceKeyValue = getPostMethod(method);
        if (virtualServiceKeyValue != null) {
            return virtualServiceKeyValue;
        }
        return new VirtualServiceKeyValue("GET", null);
    }

    private static VirtualServiceKeyValue getSpringMethod(Method method) {
        VirtualServiceKeyValue virtualServiceKeyValue = null;
        RequestMapping[] annotInstance = method.getAnnotationsByType(RequestMapping.class);
        if (annotInstance != null && annotInstance.length > 0) {
            RequestMapping requestMapping = annotInstance[0];
            virtualServiceKeyValue = new VirtualServiceKeyValue();
            virtualServiceKeyValue.setServiceType(VirtualServiceType.SPRING);
            if ( requestMapping.method().length > 0) {
                virtualServiceKeyValue.setKey(requestMapping.method()[0].name());
            }
            if (requestMapping.value().length > 0) {
                virtualServiceKeyValue.setValue(requestMapping.value()[0]);
            }
        }
        return virtualServiceKeyValue;
    }

    private static VirtualServiceKeyValue getPutMethod(Method method) {
        String key = "PUT";
        VirtualServiceKeyValue virtualServiceKeyValue = null;
        PUT[] annotInstance = method.getAnnotationsByType(PUT.class);
        if (annotInstance != null && annotInstance.length > 0) {
            virtualServiceKeyValue = new VirtualServiceKeyValue();
            virtualServiceKeyValue.setKey(key);
            virtualServiceKeyValue.setValue(getURL(method));
            virtualServiceKeyValue.setServiceType(VirtualServiceType.CXF_JAX_RS);
        } else {
            PutMapping[] annotInstanceMapping = method.getAnnotationsByType(PutMapping.class);
            if (annotInstanceMapping != null && annotInstanceMapping.length > 0) {
                virtualServiceKeyValue = new VirtualServiceKeyValue();
                virtualServiceKeyValue.setKey(key);
                virtualServiceKeyValue.setServiceType(VirtualServiceType.SPRING);
                if (annotInstanceMapping[0].value().length > 0)
                    virtualServiceKeyValue.setValue(annotInstanceMapping[0].value()[0]);
            }
        }
        return virtualServiceKeyValue;
    }

    private static VirtualServiceKeyValue getPostMethod(Method method) {
        String key = "POST";
        VirtualServiceKeyValue virtualServiceKeyValue = null;
        POST[] annotInstance = method.getAnnotationsByType(POST.class);
        if (annotInstance != null && annotInstance.length > 0) {
            virtualServiceKeyValue = new VirtualServiceKeyValue();
            virtualServiceKeyValue.setKey(key);
            virtualServiceKeyValue.setValue(getURL(method));
            virtualServiceKeyValue.setServiceType(VirtualServiceType.CXF_JAX_RS);
        } else {
            PostMapping[] annotInstanceMapping = method.getAnnotationsByType(PostMapping.class);
            if (annotInstanceMapping != null && annotInstanceMapping.length > 0) {
                virtualServiceKeyValue = new VirtualServiceKeyValue();
                virtualServiceKeyValue.setKey(key);
                virtualServiceKeyValue.setServiceType(VirtualServiceType.SPRING);
                if ( annotInstanceMapping[0].value().length > 0) {
                    virtualServiceKeyValue.setValue(annotInstanceMapping[0].value()[0]);
                }
            }
        }
        return virtualServiceKeyValue;
    }

    private static VirtualServiceKeyValue getDeleteMethod(Method method) {
        String key = "DELETE";
        VirtualServiceKeyValue virtualServiceKeyValue = null;
        DELETE[] annotInstance = method.getAnnotationsByType(DELETE.class);
        if (annotInstance != null && annotInstance.length > 0) {
            virtualServiceKeyValue = new VirtualServiceKeyValue();
            virtualServiceKeyValue.setKey(key);
            virtualServiceKeyValue.setValue(getURL(method));
            virtualServiceKeyValue.setServiceType(VirtualServiceType.CXF_JAX_RS);
        } else {
            DeleteMapping[] annotInstanceMapping = method.getAnnotationsByType(DeleteMapping.class);
            if (annotInstanceMapping != null && annotInstanceMapping.length > 0) {
                virtualServiceKeyValue = new VirtualServiceKeyValue();
                virtualServiceKeyValue.setKey(key);
                virtualServiceKeyValue.setServiceType(VirtualServiceType.SPRING);
                if (annotInstanceMapping[0].value().length > 0) {
                    virtualServiceKeyValue.setValue(annotInstanceMapping[0].value()[0]);
                }
            }
        }
        return virtualServiceKeyValue;
    }

    private static VirtualServiceKeyValue getGetMethod(Method method) {
        String key = "GET";
        VirtualServiceKeyValue virtualServiceKeyValue = null;
        GET[] annotInstance = method.getAnnotationsByType(GET.class);
        if (annotInstance != null && annotInstance.length > 0) {
            virtualServiceKeyValue = new VirtualServiceKeyValue();
            virtualServiceKeyValue.setKey(key);
            virtualServiceKeyValue.setValue(getURL(method));
            virtualServiceKeyValue.setServiceType(VirtualServiceType.CXF_JAX_RS);
        } else {
            GetMapping[] annotInstanceMapping = method.getAnnotationsByType(GetMapping.class);
            if (annotInstanceMapping != null && annotInstanceMapping.length > 0) {
                virtualServiceKeyValue = new VirtualServiceKeyValue();
                virtualServiceKeyValue.setKey(key);
                virtualServiceKeyValue.setServiceType(VirtualServiceType.SPRING);
                if (annotInstanceMapping[0].value().length > 0) {
                    virtualServiceKeyValue.setValue(annotInstanceMapping[0].value()[0]);
                }
            }
        }
        return virtualServiceKeyValue;
    }

    public static String getURL(Method method) {
        Path[] annotPathInstance = method.getAnnotationsByType(Path.class);
        if (annotPathInstance != null && annotPathInstance.length > 0) {
            Path path = annotPathInstance[0];
            return path.value();
        }
        return null;
    }

}
