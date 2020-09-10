package io.virtualan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.core.model.RequestType;
import io.virtualan.core.model.SoapService;
import io.virtualan.core.model.VirtualServiceMessageRequest;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.model.VirtualServiceStatus;
import io.virtualan.core.soap.SoapEndpointCodeGenerator;
import io.virtualan.core.soap.WSEndpointConfiguration;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.service.VirtualService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
  @Autowired(required = false)
  private WSEndpointConfiguration wsEndpointConfiguration;

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

  @RequestMapping(value = "/virtualservices/soap/services", method = RequestMethod.GET)
  public ResponseEntity<Collection<SoapService>> listAvailableSoapService() {
    final Collection<SoapService>  soapServices = wsEndpointConfiguration.getWsServiceMockList().values();
    if (soapServices == null || soapServices.isEmpty()) {
      return new ResponseEntity<Collection<SoapService>>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<Collection<SoapService>>(soapServices, HttpStatus.OK);
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
  public ResponseEntity<List<VirtualServiceRequest>> listAllMockMessageLoadRequests() {
    final List<VirtualServiceRequest> mockLoadRequests = virtualService.findAllMockRequests();
    final List<VirtualServiceRequest> mockRestLoadRequests = mockLoadRequests.stream().filter(x -> RequestType.SOAP.toString().equalsIgnoreCase(x.getRequestType()) || x.getRequestType() == null).collect(
        Collectors.toList());
    if (mockRestLoadRequests.isEmpty()) {
      return new ResponseEntity<List<VirtualServiceRequest>>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<List<VirtualServiceRequest>>(mockRestLoadRequests, HttpStatus.OK);
  }

  @RequestMapping(value = "/virtualservices/soap", method = RequestMethod.POST)
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceRequest virtualServiceRequest) {
    try {
        wsEndpointConfiguration.getWsServiceMockList().entrySet()
          .stream()
          .filter(
              x  -> (x.getValue().getMethod().equalsIgnoreCase(virtualServiceRequest.getMethod()) &&
                    x.getValue().getNs().equalsIgnoreCase(virtualServiceRequest.getUrl())))
          .forEach(y -> {
            try {
              Class reqClazzz = Class.forName(y.getValue().getRequestClassName());
              virtualServiceRequest.setInputObjectType(reqClazzz);
             } catch (ClassNotFoundException e) {
              log.warn("return Class not found : " + e.getMessage());
            }
          });
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
}
