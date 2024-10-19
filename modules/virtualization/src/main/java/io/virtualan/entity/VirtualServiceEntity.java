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

package io.virtualan.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

/**
 * This is Virtual Service entity.
 *
 * @author  Elan Thangamani
 *
 **/
@Entity
@Table(name = "virtual_service")
@Data
public class VirtualServiceEntity implements Serializable {

    private static final long serialVersionUID = -1801714432822866390L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "virtual_service_generator")
    @SequenceGenerator(name = "virtual_service_generator", sequenceName = "virtual_service_seq",allocationSize=1)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    
    //Purpose of the message
    @Column(name = "operationId", nullable = false)
    private String operationId;

    @Column(name = "input")
    private String input;

    @Column(name = "requestType")
    private String requestType;

    @Column(name = "output")
    private String output;
    
    @Column(name = "priority")
    private int priority;
    
    @Column(name = "type")
    private String type;
    
    //200
    @Column(name = "httpStatusCode")
    private String httpStatusCode;

    //broker name
    @Column(name = "resources")
    private String resource;
    
    //inboundTopic=""
    @Column(name = "url")
    private String url;


    @Column(name = "contentType")
    private String contentType;

    //CONSUMER-PRODUCER
    //CONSUMER
    @Column(name = "method")
    private String method;
    
    @Column(name = "excludeList")
    private String excludeList;
    
    @Column(name = "rule")
    private String rule;
    
    //inboundTopic=""
    //outboundTopic=""
    @Column(name = "availableParamsList")
    private String availableParamsList;

    @Column(name = "headerParamsList")
    private String headerParamsList;

    @Column(name = "lastUsedDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Calendar lastUsedDateTime;

    @Column(name = "usageCount")
    private long usageCount;

    public VirtualServiceEntity() {

    }
    public VirtualServiceEntity(String operationId, String input, String output) {
        this.operationId = operationId;
        this.input = input;
        this.output = output;
    }

    @Override
    public String toString() {
        return "VirtualServiceEntity{" +
            "id=" + id +
            ", operationId='" + operationId + '\'' +
            ", input='" + input + '\'' +
            ", requestType='" + requestType + '\'' +
            ", output='" + output + '\'' +
            ", priority=" + priority +
            ", type='" + type + '\'' +
            ", httpStatusCode='" + httpStatusCode + '\'' +
            ", resource='" + resource + '\'' +
            ", url='" + url + '\'' +
            ", contentType='" + contentType + '\'' +
            ", method='" + method + '\'' +
            ", excludeList='" + excludeList + '\'' +
            ", rule='" + rule + '\'' +
            ", availableParamsList='" + availableParamsList + '\'' +
            ", headerParamsList='" + headerParamsList + '\'' +
            ", lastUsedDateTime=" + lastUsedDateTime +
            ", usageCount=" + usageCount +
            '}';
    }
}
