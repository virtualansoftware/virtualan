package io.virtualan.controller;

import io.virtualan.core.model.*;
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
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("virtualMessageController")
@Slf4j
public class VirtualMessageController {

  Locale locale = LocaleContextHolder.getLocale();

  @Autowired
  private MessageUtil messageUtil;

  @Value("${virtualan.script.enabled:false}")
  private boolean scriptEnabled;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private VirtualService virtualService;

  @Autowired
  private Converter converter;

  @GetMapping(value = "/virtualservices/load-topics")
  public ResponseEntity<String> listAllTopics() throws IOException {
    JSONArray array = getAvailableQueues();
    return ResponseEntity.status(HttpStatus.OK).body(array.toString());
  }

  public ResponseEntity checkIfServiceDataAlreadyExists(
      VirtualServiceRequest virtualServiceRequest) throws JAXBException, IOException {
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

  @DeleteMapping(value = "/virtualservices/message/{id}")
  public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
    final VirtualServiceRequest mockLoadRequest = virtualService.findById(id);
    if (mockLoadRequest == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    virtualService.deleteMockRequestById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(value = "/virtualservices/message")
  public ResponseEntity<List<VirtualServiceMessageRequest>> listAllMockMessageLoadRequests() {
    final List<VirtualServiceRequest> mockLoadRequests = virtualService.findAllMockRequests();

    List<VirtualServiceMessageRequest> msgList = new ArrayList<>();
    for ( VirtualServiceRequest request :mockLoadRequests) {
      VirtualServiceMessageRequest virtualServiceMessageRequest = new VirtualServiceMessageRequest();
      request = converter.convertAsJson(request);
      if(RequestType.KAFKA.name().equalsIgnoreCase(request.getRequestType())
          || RequestType.AMQ.name().equalsIgnoreCase((request.getRequestType()))
          || RequestType.MQTT.name().equalsIgnoreCase((request.getRequestType()))) {
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
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(msgList, HttpStatus.OK);
  }

  @PostMapping(value = "/virtualservices/message")
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceMessageRequest virtualServiceMessageRequest) {
    try {
      if(!scriptEnabled && virtualServiceMessageRequest.getType() != null && (ResponseProcessType.SCRIPT.toString().equalsIgnoreCase(virtualServiceMessageRequest.getType().toString())
              || ResponseProcessType.RULE.toString().equalsIgnoreCase(virtualServiceMessageRequest.getType().toString()))) {
        return new ResponseEntity<>(
                "{\"message\":\""+messageSource.getMessage("VS_VALIDATION_FAILURE_REJECT", null, locale)+"\"}",
                null, HttpStatus.BAD_REQUEST);
      }
      VirtualServiceRequest request = new VirtualServiceRequest();
      BeanUtils.copyProperties(virtualServiceMessageRequest, request);
      converter.convertJsonAsString(request);
      request.setUrl(virtualServiceMessageRequest.getBrokerUrl());
      request.setMethod(virtualServiceMessageRequest.getResponseTopicOrQueueName());
      request.setOperationId(virtualServiceMessageRequest.getRequestTopicOrQueueName());
      if(request.getMethod().equalsIgnoreCase(request.getOperationId())){
        return new ResponseEntity<VirtualServiceStatus>(new VirtualServiceStatus(
            messageSource.getMessage("VS_INVALID_TOPIC_ERROR", null, locale)),
            HttpStatus.BAD_REQUEST);
      }
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


  private JSONArray getAvailableQueues() throws IOException {
    //KAFKA CONFIGS
    InputStream stream = VirtualMessageController.class.getClassLoader()
        .getResourceAsStream("conf/kafka.json");
    JSONArray messageServiceInfos  = new JSONArray();

    if (stream != null) {
      String jmsConfigJson = readString(stream);
      JSONObject jsonObject = new JSONObject(jmsConfigJson);
      messageServiceInfos = jsonObject.optJSONArray("Kafka");
    }

    //MQTT CONFIGS
    stream = VirtualMessageController.class.getClassLoader()
        .getResourceAsStream("conf/mqtt-config.json");
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
          jmsObject.put("broker", key +" : " +expected.getJSONArray("broker-url").getString(0));
          jmsObject.put("topics", expected.getJSONArray("receiver-queue"));
          messageServiceInfos.put(jmsObject);
        }
      }
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
          if("IBMMQ".equalsIgnoreCase(key)) {

            StringBuilder messageIndentifier = new StringBuilder();
            messageIndentifier.append(expected.getString("host"));
            messageIndentifier.append("(");
            messageIndentifier.append(expected.getInt("port"));
            messageIndentifier.append(")- ");
            messageIndentifier.append(expected.getString("channel"));
            messageIndentifier.append(" - ");
            messageIndentifier.append(expected.getString("queue-mgr"));
            jmsObject.put("broker", key +" : " + messageIndentifier.toString());
          } else {
            jmsObject.put("broker", key +" : " +expected.getString("broker-url"));
          }
          jmsObject.put("topics", expected.optJSONArray("receiver-queue"));
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
