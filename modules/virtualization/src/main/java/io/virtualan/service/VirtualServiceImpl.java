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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.Converter;
import io.virtualan.dao.VirtualServiceRepository;
import io.virtualan.entity.VirtualServiceEntity;

/**
 * This is Virtual Service read from virtualan or cache implementations.
 *
 * @author Elan Thangamani
 *
 **/
@Service("virtualService")
public class VirtualServiceImpl implements VirtualService {

    private final Logger log = LoggerFactory.getLogger(VirtualServiceImpl.class);

    @Autowired
    @Qualifier("virtualServiceRepository")
    private VirtualServiceRepository virtualServiceRepository;

    @Autowired
    private Converter converter;


    @Override
    public List<VirtualServiceRequest> findAllMockRequests() {
        final Iterable<VirtualServiceEntity> mockEntityList = virtualServiceRepository.findAll();
        return getVirtualServiceRequests(mockEntityList);
    }
    
    @Override
    public void importAllMockRequests(List<VirtualServiceRequest> virtualServiceRequestList) {
        List<VirtualServiceEntity> virtualServiceEntityList = new ArrayList<>();
        for (Iterator<VirtualServiceRequest> it = virtualServiceRequestList.iterator(); it.hasNext(); ) {
            VirtualServiceRequest request = it.next();
            VirtualServiceEntity virtualServiceEntity =
                    Converter.converterRToE(request);
            virtualServiceEntity.setLastUsedDateTime(Calendar.getInstance());
            virtualServiceEntityList.add(virtualServiceEntity);
        }
        virtualServiceRepository.saveAll(virtualServiceEntityList);
    }
    
    @Override
    public VirtualServiceRequest findById(long id) {
        Optional<VirtualServiceEntity> virtualServiceRequest  = virtualServiceRepository.findById(id);
        return virtualServiceRequest.map(Converter::converterEToR).orElse(null);
    }

    @Override
    @Transactional("virtualTransactionManager")
    public VirtualServiceRequest saveMockRequest(VirtualServiceRequest mockTransferObject) {
        final VirtualServiceEntity virtualServiceEntity =
                Converter.converterRToE(mockTransferObject);
        virtualServiceEntity.setLastUsedDateTime(Calendar.getInstance());
        return Converter.converterEToR(virtualServiceRepository.save(virtualServiceEntity));
    }

    @Override
    public void updateMockRequest(VirtualServiceRequest mockRequest) {
        final VirtualServiceEntity virtualServiceEntity = Converter.converterRToE(mockRequest);
        virtualServiceEntity.setLastUsedDateTime(Calendar.getInstance());
        Converter.converterEToR(virtualServiceRepository.save(virtualServiceEntity));
    }

    @Override
    public void deleteMockRequestById(long id) {
        findById(id);
        virtualServiceRepository.deleteById(id);
    }

    @Override
    public boolean isMockRequestExist(VirtualServiceRequest mockRequest) {
        return virtualServiceRepository.findById(mockRequest.getId()).isPresent();
    }

    public static Predicate<VirtualServiceRequest> filterOperationIdAndResource(String resource,
            String operationId) {
        return p -> (p.getResource().equalsIgnoreCase(resource)
                && p.getOperationId().equalsIgnoreCase(operationId));
    }

    @Override
    public List<VirtualServiceRequest> readByOperationId(String resource, String operationId) {
        return getVirtualServiceRequests(
                virtualServiceRepository.findByResourceAndOperationId(resource, operationId));
    }

    @Override
    @Async("asyncWorkExecutor")
    @Transactional("virtualTransactionManager")
    public void updateUsageTime(MockRequest request) {
        Optional<VirtualServiceEntity> virtualServiceRequest  = virtualServiceRepository.findById(request.getVirtualServiceId());
        if(virtualServiceRequest.isPresent()) {
            virtualServiceRequest.get().setLastUsedDateTime(Calendar.getInstance());
            final long usageCount = request.getUsageCount() + 1;
            virtualServiceRequest.get().setUsageCount(usageCount);
        }
    }

    @Override
    public void periodicalRemovalOfUnusedMocks(int removeMockDataUnusedAfter, boolean doCleanup) {
        if (doCleanup) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -removeMockDataUnusedAfter);
            final Iterable<VirtualServiceEntity> mockEntityList =
                    virtualServiceRepository.findByLastUsedDateTimeBefore(calendar);
            final int count = getVirtualServiceRequests(mockEntityList).size();
            if (count > 0) {
                virtualServiceRepository.deleteAll(mockEntityList);
            } else {
                log.warn("No record to delete...");
            }
        }
    }

    private List<VirtualServiceRequest> getVirtualServiceRequests(
            Iterable<VirtualServiceEntity> mockEntityList) {
        final List<VirtualServiceRequest> list = new ArrayList<>();
        for (final VirtualServiceEntity mockEntity : mockEntityList) {
            list.add(Converter.converterEToR(mockEntity));
        }
        return list;
    }

}
