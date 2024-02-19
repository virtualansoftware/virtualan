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

import java.util.List;

import javax.ws.rs.Path;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import io.virtualan.annotation.VirtualService;

/**
 * This ApiType class identify what type of REST service would be implemented.
 *
 * @author Elan Thangamani
 **/
@Component
@Slf4j
public class ApiType {

  private ApiType() {

  }

  private static VirtualServiceType getType(Class clazz) {
    if (clazz.isAnnotationPresent(RequestMapping.class)) {
      return VirtualServiceType.SPRING;
    } else if (clazz.isAnnotationPresent(Path.class)) {
      return VirtualServiceType.CXF_JAX_RS;
    }
    return null;
  }

  public static void addApis(List<String> apis, List<Class> classes) {
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
  }
}
