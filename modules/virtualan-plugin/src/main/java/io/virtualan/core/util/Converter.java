package io.virtualan.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.entity.VirtualServiceEntity;

@Service("converter")
public class Converter {

    private static final String PARAM_DELIMITER = ":_:";


    public static Map<String, String> converter(List<VirtualServiceKeyValue> paranList) {
        final Map<String, String> mapkeyValue = new HashMap<>();
        if (paranList != null && paranList.size() > 0) {
            for (final VirtualServiceKeyValue availableParam : paranList) {
                if (availableParam.getValue() != null) {
                    mapkeyValue.put(availableParam.getKey(), availableParam.getValue());
                }
            }
        }
        return mapkeyValue;
    }

    public static VirtualServiceRequest converterEToR(VirtualServiceEntity mockEntity) {
        final VirtualServiceRequest request = new VirtualServiceRequest();
        BeanUtils.copyProperties(mockEntity, request);
        request.setAvailableParams(Converter.readParameter(mockEntity.getAvailableParamsList()));
        request.setHeaderParams(Converter.readParameter(mockEntity.getHeaderParamsList()));
        return request;
    }

    public static List<VirtualServiceKeyValue> readParameter(String paramStr) {
        final List<VirtualServiceKeyValue> availableParams = new LinkedList<>();
        if (paramStr != null) {
            final String[] availableParamsList = paramStr.split(Converter.PARAM_DELIMITER);
            if (availableParamsList != null && availableParamsList.length > 0) {
                for (final String availableParamsStr : availableParamsList) {
                    if (availableParamsStr.split("=").length == 2) {
                        availableParams
                                .add(new VirtualServiceKeyValue(availableParamsStr.split("=")[0],
                                        availableParamsStr.split("=")[1]));
                    }
                }
            }
        }
        return availableParams;
    }

    public static VirtualServiceEntity converterRToE(VirtualServiceRequest mockRequest) {
        final VirtualServiceEntity mockEntity = new VirtualServiceEntity();
        BeanUtils.copyProperties(mockRequest, mockEntity);
        mockEntity
                .setAvailableParamsList(Converter.readParameters(mockRequest.getAvailableParams()));
        mockEntity.setHeaderParamsList(Converter.readParameters(mockRequest.getHeaderParams()));
        return mockEntity;
    }

    public static String readParameters(List<VirtualServiceKeyValue> paranList) {
        final StringBuffer availableParamList = new StringBuffer();
        String availableParamStr = null;
        if (paranList != null && paranList.size() > 0) {
            for (final VirtualServiceKeyValue availableParam : paranList) {
                if (availableParam.getValue() != null) {
                    availableParamList.append(availableParam.getKey() + "="
                            + availableParam.getValue() + Converter.PARAM_DELIMITER);
                }
            }
            availableParamStr = availableParamList.toString();
            if (availableParamStr.lastIndexOf(Converter.PARAM_DELIMITER) > 0) {
                return availableParamStr.substring(0,
                        availableParamStr.lastIndexOf(Converter.PARAM_DELIMITER));
            } else if (availableParamStr != null && availableParamStr.trim().length() > 0) {
                return availableParamStr;
            }
        }
        return availableParamStr;
    }
}
