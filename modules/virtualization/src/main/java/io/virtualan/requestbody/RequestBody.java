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

package io.virtualan.requestbody;

import io.virtualan.core.model.ContentType;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * This is Virtual Service request body.
 * 
 * @author  Elan Thangamani
 * 
 **/
@Data
public class RequestBody {
    private Object actualInput;
    private String inputRequest;
    private String expectedInput;
    private Collection<String> excludeList;
    private Class inputObjectType;
    private String inputObjectTypeName;
    private ObjectMapper objectMapper;
    private ContentType contentType;

    @Override
    public String toString() {
        return "RequestBody{" +
            "actualInput=" + actualInput +
            ", inputRequest='" + inputRequest + '\'' +
            ", expectedInput='" + expectedInput + '\'' +
            ", excludeList=" + excludeList +
            ", inputObjectType=" + inputObjectType +
            ", inputObjectTypeName='" + inputObjectTypeName + '\'' +
            ", objectMapper=" + objectMapper +
            ", contentType=" + contentType +
            '}';
    }
}
