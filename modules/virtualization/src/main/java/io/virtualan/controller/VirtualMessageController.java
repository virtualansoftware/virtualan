package io.virtualan.controller;

import io.virtualan.core.model.RequestType;
import io.virtualan.core.model.VirtualServiceMessageRequest;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.model.VirtualServiceStatus;
import io.virtualan.core.util.Converter;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.service.VirtualService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("virtualMessageController")
public class VirtualMessageController {

  private static final Logger log = LoggerFactory.getLogger(VirtualMessageController.class);
  Locale locale = LocaleContextHolder.getLocale();

  @Autowired
  private MessageUtil messageUtil;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private VirtualService virtualService;

  @Autowired
  private Converter converter;

  @RequestMapping(value = "/virtualservices/load-topics", method = RequestMethod.GET)
  public ResponseEntity<String> listAllTopics()
      throws Exception {
    JSONArray array = getAvailableQueues();
    return ResponseEntity.status(HttpStatus.OK).body(array.toString());
  }

  public ResponseEntity checkIfServiceDataAlreadyExists(
      VirtualServiceRequest virtualServiceRequest) throws Exception {
    final Long id = messageUtil.isMockAlreadyExists(virtualServiceRequest);
    if (id != null && id != 0) {
      final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
          messageSource.getMessage("VS_DATA_ALREADY_EXISTS", null, locale));
      virtualServiceRequest.setId(id);
      virtualServiceRequest = converter.convertAsJson(virtualServiceRequest);
      VirtualServiceMessageRequest virtualServiceMessageRequest = new VirtualServiceMessageRequest();
      BeanUtils.copyProperties(virtualServiceRequest, virtualServiceMessageRequest);
      virtualServiceMessageRequest.setBrokerUrl(virtualServiceRequest.getUrl());
      virtualServiceMessageRequest.setResponseTopicOrQueueName(virtualServiceRequest.getMethod());
      virtualServiceMessageRequest.setRequestTopicOrQueueName(virtualServiceRequest.getOperationId());
      virtualServiceStatus.setVirtualServiceMessageRequest(virtualServiceMessageRequest);
      return new ResponseEntity<VirtualServiceStatus>(virtualServiceStatus, HttpStatus.BAD_REQUEST);
    }
    return null;
  }

  @RequestMapping(value = "/virtualservices/message/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
    final VirtualServiceRequest MockLoadRequest = virtualService.findById(id);
    if (MockLoadRequest == null) {
      return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NOT_FOUND);
    }
    virtualService.deleteMockRequestById(id);
    return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/virtualservices/message", method = RequestMethod.GET)
  public ResponseEntity<List<VirtualServiceMessageRequest>> listAllMockMessageLoadRequests() {
    final List<VirtualServiceRequest> mockLoadRequests = virtualService.findAllMockRequests();

    List<VirtualServiceMessageRequest> msgList = new ArrayList<>();
    for ( VirtualServiceRequest request :mockLoadRequests) {
      VirtualServiceMessageRequest virtualServiceMessageRequest = new VirtualServiceMessageRequest();
      request = converter.convertAsJson(request);
      if(RequestType.KAFKA.name().equalsIgnoreCase(request.getRequestType())
          || RequestType.AMQ.name().equalsIgnoreCase((request.getRequestType()))) {
        BeanUtils.copyProperties(request, virtualServiceMessageRequest);
        virtualServiceMessageRequest.setBrokerUrl(request.getUrl());
        virtualServiceMessageRequest.setResponseTopicOrQueueName(request.getMethod());
        virtualServiceMessageRequest.setRequestTopicOrQueueName(request.getOperationId());
        virtualServiceMessageRequest.setInput(request.getInput() != null ? request.getInput().toString() : null);
        virtualServiceMessageRequest.setOutput(request.getOutput() != null ? request.getOutput() .toString() : null);
        msgList.add(virtualServiceMessageRequest);
      }
    }

    if (msgList.isEmpty()) {
      return new ResponseEntity<List<VirtualServiceMessageRequest>>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<List<VirtualServiceMessageRequest>>(msgList, HttpStatus.OK);
  }

  @RequestMapping(value = "/virtualservices/message", method = RequestMethod.POST)
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceMessageRequest virtualServiceMessageRequest) {
    try {
      VirtualServiceRequest request = new VirtualServiceRequest();
      BeanUtils.copyProperties(virtualServiceMessageRequest, request);
      converter.convertJsonAsString(request);
      request.setUrl(virtualServiceMessageRequest.getBrokerUrl());
      request.setMethod(virtualServiceMessageRequest.getResponseTopicOrQueueName());
      request.setOperationId(virtualServiceMessageRequest.getRequestTopicOrQueueName());
      if(virtualServiceMessageRequest.getRequestType() == null ) {
        request.setRequestType(RequestType.KAFKA.toString());
      } else {
        request.setRequestType(virtualServiceMessageRequest.getRequestType());
      }
      ResponseEntity responseEntity = checkIfServiceDataAlreadyExists(request);

      if (responseEntity != null) {
        return responseEntity;
      }

      VirtualServiceRequest mockTransferObject = virtualService.saveMockRequest(request);
      mockTransferObject = converter.convertAsJson(mockTransferObject);
      VirtualServiceMessageRequest virtualServiceMessageRequestResponse = new VirtualServiceMessageRequest();
      BeanUtils.copyProperties(mockTransferObject, virtualServiceMessageRequestResponse);
      virtualServiceMessageRequestResponse.setBrokerUrl(mockTransferObject.getUrl());
      virtualServiceMessageRequestResponse.setResponseTopicOrQueueName(mockTransferObject.getMethod());
      virtualServiceMessageRequestResponse.setRequestTopicOrQueueName(mockTransferObject.getOperationId());
      virtualServiceMessageRequestResponse.setMockStatus(
          new VirtualServiceStatus(messageSource.getMessage("VS_SUCCESS", null, locale)));
      return new ResponseEntity<VirtualServiceMessageRequest>(virtualServiceMessageRequestResponse, HttpStatus.CREATED);
    } catch (final Exception e) {
      return new ResponseEntity<VirtualServiceStatus>(new VirtualServiceStatus(
          messageSource.getMessage("VS_UNEXPECTED_ERROR", null, locale) + e.getMessage()),
          HttpStatus.BAD_REQUEST);
    }
  }


  private JSONArray getAvailableQueues() throws Exception {
    //KAFKA CONFIGS
    InputStream stream = VirtualMessageController.class.getClassLoader()
        .getResourceAsStream("conf/kafka.json");
    JSONArray messageServiceInfos  = new JSONArray();

    if (stream != null) {
      String jmsConfigJson = readString(stream);
      JSONObject jsonObject = new JSONObject(jmsConfigJson);
      messageServiceInfos = jsonObject.optJSONArray("Kafka");
    }

    //JMS CONFIGS
    stream = VirtualMessageController.class.getClassLoader()
        .getResourceAsStream("conf/jms-config.json");
    if (stream != null) {
      String jmsConfigJson = readString(stream);
      JSONObject jsonObject = new JSONObject(jmsConfigJson);
      Iterator<String> keys = jsonObject.keys();
      while(keys.hasNext()) {
        String key = keys.next();
        JSONArray jmsArray = jsonObject.getJSONArray(key);
        if(jmsArray != null && jmsArray.length() > 0) {
          JSONObject expected = jmsArray.optJSONObject(0);
          JSONObject jmsObject = new JSONObject();
          jmsObject.put("broker", expected.getString("broker-url"));
          jmsObject.put("topics", expected.getJSONArray("receiver-queue"));
          messageServiceInfos.put(jmsObject);
        }
      }
    }
    return messageServiceInfos;
  }

  public String readString(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }


  public JSONArray getArray(String[] stringArray) {
    JSONArray array = new JSONArray();
		for (String name : stringArray) {
			array.put(name);
		}
    return array;

  }
}
