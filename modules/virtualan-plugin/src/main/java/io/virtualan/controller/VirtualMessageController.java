package io.virtualan.controller;

import io.virtualan.core.InvalidMockResponseException;
import io.virtualan.core.VirtualServiceInfo;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.*;
import io.virtualan.core.util.BestMatchComparator;
import io.virtualan.core.util.Converter;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceValidRequest;
import io.virtualan.message.core.ApplicationProps;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.message.core.MessagingApplication;
import io.virtualan.requestbody.RequestBodyTypes;
import io.virtualan.service.VirtualService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController("virtualMessageController")
public class VirtualMessageController {
	private static final Logger log = LoggerFactory.getLogger(VirtualMessageController.class);
	
	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private VirtualService virtualService;
	
	Locale locale = LocaleContextHolder.getLocale();
	
	@RequestMapping(value = "/virtualservices/load-topics", method = RequestMethod.GET)
	public ResponseEntity<String> listAllTopics()
			throws Exception {
		JSONArray array = getJsonObject();
		return ResponseEntity.status(HttpStatus.OK).body(array.toString());
	}
	
	public ResponseEntity checkIfServiceDataAlreadyExists(
			VirtualServiceRequest virtualServiceRequest) {
		final Long id = messageUtil.isMockAlreadyExists(virtualServiceRequest);
		if (id != null && id != 0) {
			final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
					messageSource.getMessage("VS_DATA_ALREADY_EXISTS", null, locale));
			virtualServiceRequest.setId(id);
			virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
			return new ResponseEntity<VirtualServiceStatus>(virtualServiceStatus,
					HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	
	@RequestMapping(value = "/virtualservices/message", method = RequestMethod.POST)
	public ResponseEntity createMockRequest(
			@RequestBody VirtualServiceRequest virtualServiceRequest) {
		
		try {
			
			ResponseEntity responseEntity = checkIfServiceDataAlreadyExists(virtualServiceRequest);
			
			if (responseEntity != null) {
				return responseEntity;
			}
			
			final VirtualServiceRequest mockTransferObject =
					virtualService.saveMockRequest(virtualServiceRequest);
			
			mockTransferObject.setMockStatus(
					new VirtualServiceStatus(messageSource.getMessage("VS_SUCCESS", null, locale)));
			return new ResponseEntity<>(mockTransferObject, HttpStatus.CREATED);
			
		} catch (final Exception e) {
			return new ResponseEntity<VirtualServiceStatus>(new VirtualServiceStatus(
					messageSource.getMessage("VS_UNEXPECTED_ERROR", null, locale) + e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}
	
	
	// REDO - Is this validation needed?
	/*public boolean isMockResponseBodyValid(VirtualServiceRequest mockTransferObject)
			throws InvalidMockResponseException {
		boolean isValid = true;
		try {
			final VirtualServiceRequest mockTransferObjectActual =
					virtualServiceInfo.getResponseType(mockTransferObject);
			if (!mockTransferObjectActual.getResponseType().isEmpty() && virtualServiceValidRequest
					.validResponse(mockTransferObjectActual, mockTransferObject)) {
				isValid = true;
			}
		} catch (final Exception e) {
			throw new InvalidMockResponseException(e);
		}
		return isValid;
	}*/
	
	
	

	
	
	
	
	private JSONArray getJsonObject() throws Exception {
		JSONArray array = new JSONArray();
		if(ApplicationProps.getProperty("virtualan.kafka.1.topics") != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("broker", ApplicationProps.getProperty("virtualan.kafka.1.bootstrapServers"));
			jsonObject.put("topics", getArray(ApplicationProps.getProperty("virtualan.kafka.1.topics").split(",")));
			array.put(jsonObject);
		} else {
			log.warn("ignore this message.. if kafka-mock service message features is not utilized..");
		}
		return array;
		
	}
	
	public JSONArray getArray(String[] stringArray) {
		JSONArray array = new JSONArray();
		for(String name : stringArray)
			array.put(name);
		return array;
		
	}
}
