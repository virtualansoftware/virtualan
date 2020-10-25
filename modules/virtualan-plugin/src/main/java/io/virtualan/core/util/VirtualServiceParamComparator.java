/*
 *
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
package io.virtualan.core.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.params.Param;
import io.virtualan.params.ParamTypes;

/**
 *
 * @author Elan Thangamani
 *
 **/

@Service("virtualServiceParamComparator")
public class VirtualServiceParamComparator {


    public boolean isAllParamPresent(final MockServiceRequest mockServiceRequest,
        final ReturnMockResponse rMockResponse) {
        for (final VirtualServiceKeyValue virtualServiceKeyValue : rMockResponse.getMockRequest()
            .getAvailableParams()) {
            if (mockServiceRequest.getParams().containsKey(virtualServiceKeyValue.getKey())
                && mockServiceRequest.getParams().get(virtualServiceKeyValue.getKey())
                .equals(virtualServiceKeyValue.getValue())) {
                mockServiceRequest.getParams().remove(virtualServiceKeyValue.getKey());
            } else {
                if (mockServiceRequest.getHeaderParams() != null
                    && mockServiceRequest.getHeaderParams()
                    .get(virtualServiceKeyValue.getKey().toLowerCase()) != null && !mockServiceRequest.getHeaderParams()
                    .get(virtualServiceKeyValue.getKey().toLowerCase())
                    .contains(virtualServiceKeyValue.getValue())) {
                    return false;
                }
            }
        }
        return mockServiceRequest.getParams().isEmpty();
    }


    private int isParameterMatch(MockRequest mockRequest, MockServiceRequest mockServiceRequest) {
        int numberOfMatch = 0;

        for (final VirtualServiceKeyValue mockKeyValueParams : mockRequest.getAvailableParams()) {
            Class type = null;
            if (mockServiceRequest.getParamsType() != null) {
                type = mockServiceRequest.getParamsType().get(mockKeyValueParams.getKey());
            }
            final Param param = new Param();
            if (mockServiceRequest.getParams().containsKey(mockKeyValueParams.getKey())) {
                param.setActualValue(
                    mockServiceRequest.getParams().get(mockKeyValueParams.getKey()));
            } else {
                param.setActualValue(mockServiceRequest.getHeaderParams()
                    .get(mockKeyValueParams.getKey().toLowerCase()));
            }
            param.setExpectedValue(mockKeyValueParams.getValue());
            param.setName(mockKeyValueParams.getKey());
            numberOfMatch = getNumberOfMatch(mockRequest, numberOfMatch, mockKeyValueParams, type,
                param);
        }
        return numberOfMatch;
    }

    private int getNumberOfMatch(MockRequest mockRequest, int numberOfMatch,
        VirtualServiceKeyValue mockKeyValueParams, Class type, Param param) {
        if (mockRequest.getExcludeSet() == null
            || !mockRequest.getExcludeSet().contains(mockKeyValueParams.getKey())) {
            if (type == null) {
                if (ParamTypes.DEFAULT.compareParam(param)) {
                    numberOfMatch++;
                }
            } else if (ParamTypes.fromString(type.getCanonicalName()).compareParam(param)) {
                numberOfMatch++;
            }
        }
        return numberOfMatch;
    }


    public int isParameterMatch(MockRequest mockRequest, Map<String, String> actualQueryMap) {
        final Map<String, String> filteredActualQueryMap = new HashMap<>();
        int noumberOfMatch = 0;
        getResp(actualQueryMap, filteredActualQueryMap);
        if (mockRequest.getAvailableParams().size() == filteredActualQueryMap.size()) {
            for (final VirtualServiceKeyValue vsKeyValueParams : mockRequest.getAvailableParams()) {
                if (mockRequest.getExcludeSet() == null
                    || !mockRequest.getExcludeSet().contains(vsKeyValueParams.getKey())) {
                    if (vsKeyValueParams.getValue()
                        .equals(filteredActualQueryMap.get(vsKeyValueParams.getKey()))) {
                        noumberOfMatch++;
                    } else {
                        return noumberOfMatch;
                    }
                }
            }
        }
        return noumberOfMatch;
    }

    public void getResp(Map<String, String> actualQueryMap,
        Map<String, String> filteredActualQueryMap) {
        for (final Map.Entry<String, String> mapEntry : actualQueryMap.entrySet()) {
            if (mapEntry.getValue() != null && !"null".equals(mapEntry.getValue())) {
                filteredActualQueryMap.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }
    }

    public int compareQueryParams(MockRequest mockRequest, MockServiceRequest mockServiceRequest) {
        if (mockRequest.getAvailableParams() == null
            || mockRequest.getAvailableParams().isEmpty()) {
            return isEmptyRequest(mockServiceRequest.getParams());
        } else {
            return isParameterMatch(mockRequest, mockServiceRequest);
        }
    }


    public int compareQueryParams(MockRequest mockRequest, Map<String, String> actualQueryMap) {
        if (mockRequest.getAvailableParams() == null
            || mockRequest.getAvailableParams().isEmpty()) {
            return isEmptyRequest(actualQueryMap);
        } else {
            return isParameterMatch(mockRequest, actualQueryMap);
        }
    }

    public int isEmptyRequest(Map<String, String> actualQueryMap) {
        if(actualQueryMap != null) {
            for (final Map.Entry<String, String> checkEmpty : actualQueryMap.entrySet()) {
                if (!"null".equals(checkEmpty.getValue())) {
                    return 0;
                }
            }
        }
        return 1;
    }
}
