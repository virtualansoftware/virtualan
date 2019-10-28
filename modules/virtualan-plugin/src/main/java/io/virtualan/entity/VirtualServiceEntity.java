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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * This is Virtual Service entity.
 *
 * @author  Elan Thangamani
 *
 **/
@Entity
@Table(name = "virtual_service")
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
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String getRule() {
        return rule;
    }
    
    public void setRule(String rule) {
        this.rule = rule;
    }
    
    public Calendar getLastUsedDateTime() {
        return lastUsedDateTime;
    }

    public void setLastUsedDateTime(Calendar lastUsedDateTime) {
        this.lastUsedDateTime = lastUsedDateTime;
    }

    public long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(long usageCount) {
        this.usageCount = usageCount;
    }

    public String getHeaderParamsList() {
        return headerParamsList;
    }

    public void setHeaderParamsList(String headerParamsList) {
        this.headerParamsList = headerParamsList;
    }

    public String getAvailableParamsList() {
        return availableParamsList;
    }

    public void setAvailableParamsList(String availableParamsList) {
        this.availableParamsList = availableParamsList;
    }

    public VirtualServiceEntity() {

    }

    public VirtualServiceEntity(String operationId, String input, String output) {
        this.operationId = operationId;
        this.input = input;
        this.output = output;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(String excludeList) {
        this.excludeList = excludeList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "VirtualServiceEntity{" +
                "id=" + id +
                ", operationId='" + operationId + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", httpStatusCode='" + httpStatusCode + '\'' +
                ", resource='" + resource + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", excludeList='" + excludeList + '\'' +
                ", availableParamsList='" + availableParamsList + '\'' +
                ", headerParamsList='" + headerParamsList + '\'' +
                ", lastUsedDateTime=" + lastUsedDateTime +
                ", usageCount=" + usageCount +
                '}';
    }
}
