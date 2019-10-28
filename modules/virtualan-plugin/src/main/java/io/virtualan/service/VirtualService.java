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

package io.virtualan.service;

import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.VirtualServiceRequest;

import java.util.List;

/**
 * This is Virtual Service read from virtualan.
 * 
 * @author  Elan Thangamani
 * 
 **/
public interface VirtualService {

    VirtualServiceRequest findById(long id);

    List<VirtualServiceRequest> readByOperationId(String resource, String name);

    VirtualServiceRequest saveMockRequest(VirtualServiceRequest mockRequest);

    void updateMockRequest(VirtualServiceRequest mockRequest);

    void deleteMockRequestById(long id);

    List<VirtualServiceRequest> findAllMockRequests();

    boolean isMockRequestExist(VirtualServiceRequest mockRequest);

    void updateUsageTime(MockRequest request);

    void periodicalRemovalOfUnusedMocks(int removeMockDataUnusedAfter, boolean doCleanup);
    
    void importAllMockRequests(List<VirtualServiceRequest> virtualServiceRequestList);
}
