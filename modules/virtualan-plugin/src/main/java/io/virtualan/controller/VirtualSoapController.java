package io.virtualan.controller;

import io.virtualan.core.model.RequestType;
import io.virtualan.core.model.VirtualServiceMessageRequest;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.model.VirtualServiceStatus;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.service.VirtualService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

@RestController("virtualSoapController")
public class VirtualSoapController {

  private static final Logger log = LoggerFactory.getLogger(VirtualSoapController.class);
  Locale locale = LocaleContextHolder.getLocale();
  @Autowired
  private MessageUtil messageUtil;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private VirtualService virtualService;

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

  @RequestMapping(value = "/virtualservices/soap/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
    final VirtualServiceRequest MockLoadRequest = virtualService.findById(id);
    if (MockLoadRequest == null) {
      return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NOT_FOUND);
    }
    virtualService.deleteMockRequestById(id);
    return new ResponseEntity<VirtualServiceRequest>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/virtualservices/soap", method = RequestMethod.GET)
  public ResponseEntity<List<VirtualServiceMessageRequest>> listAllMockMessageLoadRequests() {
    final List<VirtualServiceRequest> mockLoadRequests = virtualService.findAllMockRequests();

    List<VirtualServiceMessageRequest> msgList = new ArrayList<>();
    for ( VirtualServiceRequest request :mockLoadRequests) {
      VirtualServiceMessageRequest virtualServiceMessageRequest = new VirtualServiceMessageRequest();
      if(RequestType.KAFKA.toString().equalsIgnoreCase(request.getRequestType())) {
        BeanUtils.copyProperties(request, virtualServiceMessageRequest);
        virtualServiceMessageRequest.setBrokerUrl(request.getUrl());
        virtualServiceMessageRequest.setResponseTopicOrQueueName(request.getMethod());
        virtualServiceMessageRequest.setRequestTopicOrQueueName(request.getOperationId());
        msgList.add(virtualServiceMessageRequest);
      }
    }

    if (msgList.isEmpty()) {
      return new ResponseEntity<List<VirtualServiceMessageRequest>>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<List<VirtualServiceMessageRequest>>(msgList, HttpStatus.OK);
  }

  @RequestMapping(value = "/virtualservices/soap", method = RequestMethod.POST)
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceRequest virtualServiceRequest) {

    try {

      virtualServiceRequest.setRequestType(RequestType.SOAP.toString());
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


  private JSONArray getJsonObject() throws Exception {
    InputStream stream = VirtualSoapController.class.getClassLoader()
        .getResourceAsStream("conf/kafka.json");
    if (stream != null) {
      String jmsConfigJson = readString(stream);
      JSONObject jsonObject = new JSONObject(jmsConfigJson);
      return jsonObject.optJSONArray("Kafka");
    }
    return new JSONArray();
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
