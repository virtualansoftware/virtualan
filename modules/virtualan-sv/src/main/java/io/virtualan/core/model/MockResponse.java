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

package io.virtualan.core.model;

import java.util.List;
import lombok.Data;

/**
 * This is Mock service response data from virtualan.
 *
 *
 * @author Elan Thangamani
 *
 **/
@Data
public class MockResponse {

    String output;
    String httpStatusCode;
    List<VirtualServiceKeyValue> headerParams;

    public MockResponse(String output, String httpStatusCode) {
        this.output = output;
        this.httpStatusCode = httpStatusCode;
    }
    
    public MockResponse(){
    }
    public MockResponse builder() {
        return this;
    }
    

}
