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

/**
 * API supported responses for virtualan.
 * 
 * @author  Elan Thangamani
 * 
 **/
public class VirtualServiceApiResponse {

    private String code;
    private String objectType;
    private String objectValue;
    private String objectMessage;

    public VirtualServiceApiResponse() {

    }

    public VirtualServiceApiResponse(String code, String objectType, String objectValue,
            String objectMessage) {
        super();
        this.code = code;
        this.objectType = objectType;
        this.objectValue = objectValue;
        this.objectMessage = objectMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public String getObjectMessage() {
        return objectMessage;
    }

    public void setObjectMessage(String objectMessage) {
        this.objectMessage = objectMessage;
    }

    @Override
    public String toString() {
        return "VirtualServiceApiResponse [code=" + code + ", objectType=" + objectType
                + ", objectValue=" + objectValue + ", objectMessage=" + objectMessage + "]";
    }

}
