package io.virtualan.message.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.*;
import io.virtualan.core.util.BestMatchComparator;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceValidRequest;
import io.virtualan.core.util.XMLConverter;
import io.virtualan.core.util.rule.ScriptExecutor;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("messageUtil")
public class MessageUtil {

	private static final Logger log = LoggerFactory.getLogger(MessageUtil.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ScriptExecutor scriptExecutor;

	@Autowired
	private VirtualServiceValidRequest virtualServiceValidRequest;

	@Autowired
	private VirtualServiceUtil virtualServiceUtil;
	
	public ReturnMockResponse isResponseExists(final Map<Integer, ReturnMockResponse> returnMockResponseMap) {
		final List<ReturnMockResponse> returnMockResponseList =
				new ArrayList<>(returnMockResponseMap.values());
		Collections.sort(returnMockResponseList, new BestMatchComparator());
		log.debug("Sorted list : {}" , returnMockResponseList);
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
			mockServiceRequest.setContentType(mockTransferObject.getContentType());
			mockServiceRequest.setInputObjectType(mockTransferObject.getInputObjectType());


			//Rule Execution
			Map<Integer, ReturnMockResponse> returnMockResponseMap =
					virtualServiceUtil.validateBusinessRules(mockDataSetupMap, mockServiceRequest);

			//No Rule conditions exists/met then run the script
			if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
				returnMockResponseMap = virtualServiceValidRequest.checkScriptResponse(mockDataSetupMap, mockServiceRequest);
			}

			//No script conditions exists/met then run the mock response
			if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
				returnMockResponseMap =
						isResponseExists(mockDataSetupMap, mockServiceRequest);
			}
			if(returnMockResponseMap == null || returnMockResponseMap.isEmpty()) {
				 returnMockResponseMap = isResponseExists(mockDataSetupMap, mockServiceRequest);
			}

			if (returnMockResponseMap.size() > 0) {
				return isResponseExists(returnMockResponseMap);
			}
			
			
		} catch (final Exception e) {
			log.error("getMatchingRecord :: {}" , e.getMessage());
		}
		return null;
	}
	
	private Map<Integer, ReturnMockResponse> isResponseExists(
			final Map<MockRequest, MockResponse> mockDataSetupMap,
			MockServiceRequest mockServiceRequest) throws IOException, JAXBException {
		return virtualServiceValidRequest.validObject(mockDataSetupMap,
				mockServiceRequest);
	}
	
	public Long isMockAlreadyExists(VirtualServiceRequest mockTransferObject)
			throws JAXBException, IOException {
			final Map<MockRequest, MockResponse> mockDataSetupMap = virtualServiceUtil.readDynamicResponse(
					mockTransferObject.getResource(), mockTransferObject.getOperationId());
			final MockServiceRequest mockServiceRequest = new MockServiceRequest();
			
			mockServiceRequest
					.setHeaderParams(Converter.converter(mockTransferObject.getHeaderParams()));
			mockServiceRequest.setOperationId(mockTransferObject.getOperationId());
			mockServiceRequest
					.setParams(Converter.converter(mockTransferObject.getAvailableParams()));
			mockServiceRequest.setResource(mockTransferObject.getResource());
			mockServiceRequest.setContentType(mockTransferObject.getContentType());
			mockServiceRequest.setRule(mockTransferObject.getRule());

			if (mockTransferObject.getInputObjectType() != null) {

				//validate if it is a valid script
				if(mockServiceRequest.getRule() != null) {
					scriptExecutor.executeScript(mockServiceRequest, new MockResponse(),
							mockServiceRequest.getRule().toString());
				}
				if(ContentType.XML.equals(mockTransferObject.getContentType())){
					mockServiceRequest.setInput(
							XMLConverter.xmlToObject(mockTransferObject.getInputObjectType(),mockTransferObject.getInput().toString()));
				} else {
					mockServiceRequest.setInput(objectMapper.readValue(mockTransferObject.getInput().toString(), mockTransferObject.getInputObjectType()));
				}
			} else {
				mockServiceRequest.setInput(mockTransferObject.getInput());
			}
			mockServiceRequest.setContentType(mockTransferObject.getContentType());
			mockServiceRequest.setInputObjectType(mockTransferObject.getInputObjectType());
			mockServiceRequest.setOutput(mockTransferObject.getOutput());
			final Map<Integer, ReturnMockResponse> returnMockResponseMap =
					isResponseExists(mockDataSetupMap, mockServiceRequest);
			
			if (returnMockResponseMap.size() > 0) {
				return isResposeExists(returnMockResponseMap);
			}
			return null;
	}
	
	public long isResposeExists( final Map<Integer, ReturnMockResponse> returnMockResponseMap)  {
		final List<ReturnMockResponse> returnMockResponseList =
				new ArrayList<>(returnMockResponseMap.values());
		Collections.sort(returnMockResponseList, new BestMatchComparator());
		log.debug("Sorted list : {}",  returnMockResponseList);
		final ReturnMockResponse rMockResponse = returnMockResponseList.iterator().next();
		if (rMockResponse != null && rMockResponse.getHeaderResponse() != null && rMockResponse.isExactMatch()) {
			return rMockResponse.getMockRequest().getVirtualServiceId();
		}
		return 0;
	}
	
}
