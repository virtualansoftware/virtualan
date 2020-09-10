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

import java.lang.reflect.Method;

import javax.ws.rs.Path;
import org.springframework.web.bind.annotation.RequestMapping;

import io.virtualan.core.model.VirtualServiceKeyValue;

/**
 *  This ApiResource class identify the resource name.
 * 
 * @author  Elan Thangamani
 * 
 **/

public class ApiResource {

	
	 public static String getResourceParent(Class clazz) {
		 String parentPath =  null; 
		 if(clazz.isAnnotationPresent(RequestMapping.class)) {
			 RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
			 if(requestMapping.value() != null & requestMapping.value().length  == 1) {
				 parentPath = requestMapping.value()[0];
			 }
		 } else if(clazz.isAnnotationPresent(Path.class)) {
			 Path path = (Path) clazz.getAnnotation(Path.class);
			 if(path != null) {
				 parentPath = path.value();
			 }
		 }
		 if(parentPath != null && parentPath.contains("/")) {
			String[] resources = parentPath.split("/");
			return resources.length >= 1 && resources[1].length() > 1 ? resources[1] : null;
		 } 
		 return null;
	    }

	 public static String getResource(Class clazz) {
		 String parentPath =  null; 
		 if(clazz.isAnnotationPresent(RequestMapping.class)) {
			 RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
			 if(requestMapping.value() != null & requestMapping.value().length  == 1) {
				 parentPath = requestMapping.value()[0];
			 }
		 } else if(clazz.isAnnotationPresent(Path.class)) {
			 Path path = (Path) clazz.getAnnotation(Path.class);
			 if(path != null) {
				 parentPath = path.value();
			 }
		 }
		 if(parentPath != null && parentPath.contains("/")) {
			return parentPath;
		 } 
			return null;
	}
 
	 
    public static String getResource(Method method) {
        VirtualServiceKeyValue virtualServiceKeyValue = ApiMethod.getApiMethodParamAndURL(method);
        if (virtualServiceKeyValue != null) {
            if (virtualServiceKeyValue.getValue() != null) {
                return getResourceByURL(virtualServiceKeyValue.getValue());
            }
        }
        return null;
    }

    public static String getResourceByURL(String url) {
        if (url != null && url.length() > 0) {
            int index = url.indexOf('/', 1) == -1 ? url.length() : url.indexOf('/', 1);
            url = '/' == url.charAt(0) ? url.substring(1, index) : url.substring(0, index);
            return url;
        }
        return null;
    }

}
