package io.virtualan.message.core;

import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.*;
import io.virtualan.core.util.BestMatchComparator;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceValidRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("messageUtil")
public class MessageUtil {
	
	
	@Autowired
	private VirtualServiceValidRequest virtualServiceValidRequest;

	@Autowired
	private VirtualServiceUtil virtualServiceUtil;
	
	
	public ReturnMockResponse isResponseExists(final Map<Integer, ReturnMockResponse> returnMockResponseMap) throws IOException {
		final List<ReturnMockResponse> returnMockResponseList =
				new ArrayList<>(returnMockResponseMap.values());
		Collections.sort(returnMockResponseList, new BestMatchComparator());
		System.out.println("Sorted list : " + returnMockResponseList);
		final ReturnMockResponse rMockResponse = returnMockResponseList.iterator().next();
		if (rMockResponse != null && rMockResponse.getHeaderResponse() != null && rMockResponse.isExactMatch()) {
			return rMockResponse;
		}
		return null;
	}
	
	public  ReturnMockResponse getMatchingRecord(VirtualServiceRequest mockTransferObject) {
		
		try {
			
			final Map<MockRequest, MockResponse> mockDataSetupMap = virtualServiceUtil.readDynamicResponse(
					mockTransferObject.getResource(), mockTransferObject.getOperationId());
			final MockServiceRequest mockServiceRequest = new MockServiceRequest();
			
			mockServiceRequest
					.setHeaderParams(Converter.converter(mockTransferObject.getHeaderParams()));
			mockServiceRequest.setOperationId(mockTransferObject.getOperationId());
			mockServiceRequest
					.setParams(Converter.converter(mockTransferObject.getAvailableParams()));
			mockServiceRequest.setResource(mockTransferObject.getResource());
			mockServiceRequest.setInput(mockTransferObject.getInput());
			final Map<Integer, ReturnMockResponse> returnMockResponseMap =
					isResponseExists(mockDataSetupMap, mockServiceRequest);
			
			if (returnMockResponseMap.size() > 0) {
				return isResponseExists(returnMockResponseMap);
			}
			
			
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("getMatchingRecord :: " + e.getMessage());
		}
		return null;
	}
	
	private Map<Integer, ReturnMockResponse> isResponseExists(
			final Map<MockRequest, MockResponse> mockDataSetupMap,
			MockServiceRequest mockServiceRequest) throws IOException {
		return virtualServiceValidRequest.validObject(mockDataSetupMap,
				mockServiceRequest);
	}
	
	public Long isMockAlreadyExists(VirtualServiceRequest mockTransferObject) {
		
		try {
			
			final Map<MockRequest, MockResponse> mockDataSetupMap = virtualServiceUtil.readDynamicResponse(
					mockTransferObject.getResource(), mockTransferObject.getOperationId());
			final MockServiceRequest mockServiceRequest = new MockServiceRequest();
			
			mockServiceRequest
					.setHeaderParams(Converter.converter(mockTransferObject.getHeaderParams()));
			mockServiceRequest.setOperationId(mockTransferObject.getOperationId());
			mockServiceRequest
					.setParams(Converter.converter(mockTransferObject.getAvailableParams()));
			mockServiceRequest.setResource(mockTransferObject.getResource());
			mockServiceRequest.setInput(mockTransferObject.getInput());
			final Map<Integer, ReturnMockResponse> returnMockResponseMap =
					isResponseExists(mockDataSetupMap, mockServiceRequest);
			
			if (returnMockResponseMap.size() > 0) {
				return isResposeExists(mockTransferObject, mockServiceRequest,
						returnMockResponseMap);
			}
			
			
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("isMockAlreadyExists :: " + e.getMessage());
		}
		return null;
	}
	
	public long isResposeExists(VirtualServiceRequest mockTransferObject, final MockServiceRequest mockServiceRequest,
	                             final Map<Integer, ReturnMockResponse> returnMockResponseMap) throws IOException {
		final List<ReturnMockResponse> returnMockResponseList =
				new ArrayList<>(returnMockResponseMap.values());
		Collections.sort(returnMockResponseList, new BestMatchComparator());
		System.out.println("Sorted list : " + returnMockResponseList);
		final ReturnMockResponse rMockResponse = returnMockResponseList.iterator().next();
		if (rMockResponse != null && rMockResponse.getHeaderResponse() != null && rMockResponse.isExactMatch()) {
			return rMockResponse.getMockRequest().getVirtualServiceId();
		}
		return 0;
	}
	
}
