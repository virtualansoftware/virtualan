package io.virtualan.controller;

import io.virtualan.core.VirtualParameterizedUtil;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.*;
import io.virtualan.core.soap.WSEndpointConfiguration;
import io.virtualan.core.util.Converter;
import io.virtualan.message.core.MessageUtil;
import io.virtualan.service.VirtualService;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController("virtualSoapController")
public class VirtualSoapController {

  private static final Logger log = LoggerFactory.getLogger(VirtualSoapController.class);
  Locale locale = LocaleContextHolder.getLocale();
  @Autowired
  private MessageUtil messageUtil;

  @Value("${virtualan.script.enabled:false}")
  private boolean scriptEnabled;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private VirtualParameterizedUtil virtualParameterizedUtil;

  @Autowired
  private Converter converter;

  @Autowired
  private VirtualService virtualService;

  @Autowired
  private VirtualServiceUtil virtualServiceUtil;

  @Autowired(required = false)
  private WSEndpointConfiguration wsEndpointConfiguration;

  public ResponseEntity checkIfServiceDataAlreadyExists(
      VirtualServiceRequest virtualServiceRequest) throws JAXBException, IOException {
    if ("PARAMS".equalsIgnoreCase(virtualServiceRequest.getType())) {
      Map response = virtualParameterizedUtil.handleParameterizedRequest(virtualServiceRequest);
      if(!response.isEmpty()) {
        final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
            messageSource.getMessage("VS_PARAMS_DATA_ALREADY_EXISTS", null, locale));
        virtualServiceRequest = converter.convertAsJson(virtualServiceRequest);
        virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
        virtualServiceStatus.setResponseParam( response);
        return new ResponseEntity<>(virtualServiceStatus,
            HttpStatus.BAD_REQUEST);
      }
    } else {
      final Long id = messageUtil.isMockAlreadyExists(virtualServiceRequest);
      if (id != null && id != 0) {
        final VirtualServiceStatus virtualServiceStatus = new VirtualServiceStatus(
            messageSource.getMessage("VS_DATA_ALREADY_EXISTS", null, locale));
        virtualServiceRequest.setId(id);
        virtualServiceStatus.setVirtualServiceRequest(virtualServiceRequest);
        return new ResponseEntity<VirtualServiceStatus>(virtualServiceStatus,
            HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }

  @GetMapping(value = "/virtualservices/soap/services")
  public ResponseEntity<Map<String, List<SoapService>>> listAvailableSoapService() {
    if (wsEndpointConfiguration == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    Map<String, SoapService> soapServiceMap = wsEndpointConfiguration.getWsServiceMockList();
    if (soapServiceMap != null && !soapServiceMap.isEmpty()) {

      Map<String, List<SoapService>> soapServicesByNs =
          soapServiceMap.values().stream()
              .sorted(Comparator.comparing(SoapService::getMethod))
              .map(x -> {x.setTypes(getTypes(scriptEnabled)); return x;})
              .collect(Collectors.groupingBy(SoapService::getNs));
      return new ResponseEntity<>(soapServicesByNs, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }


  private static Map<String, String> getTypes(boolean scriptEnabled) {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("RESPONSE", "Response");
    map.put("PARAMS", "Params");
    if(scriptEnabled) {
      map.put("RULE", "Rule");
      map.put("SCRIPT", "Script");
    }
    return map;
  }

  @DeleteMapping(value = "/virtualservices/soap/{id}")
  public ResponseEntity<VirtualServiceRequest> deleteMockRequest(@PathVariable("id") long id) {
    final VirtualServiceRequest mockLoadRequest = virtualService.findById(id);
    if (mockLoadRequest == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    virtualService.deleteMockRequestById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  @GetMapping(value = "/virtualservices/soap")
  public ResponseEntity<List<VirtualServiceRequest>> listAllMockMessageLoadRequests() {
    final List<VirtualServiceRequest> mockLoadRequests = virtualService.findAllMockRequests();

    final List<VirtualServiceRequest> mockRestLoadRequests = mockLoadRequests.stream().filter(x -> RequestType.SOAP.toString().equalsIgnoreCase(x.getRequestType()) || x.getRequestType() == null)
        .map(x -> {x.setTypes(getTypes(scriptEnabled)); return x;})
        .collect(
        Collectors.toList());
    if (mockRestLoadRequests.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(mockRestLoadRequests, HttpStatus.OK);
  }

  @PostMapping(value = "/virtualservices/soap")
  public ResponseEntity createMockRequest(
      @RequestBody VirtualServiceRequest virtualServiceRequest) {
    try {
      if(!scriptEnabled && virtualServiceRequest.getType() != null && (ResponseProcessType.SCRIPT.toString().equalsIgnoreCase(virtualServiceRequest.getType().toString())
              || ResponseProcessType.RULE.toString().equalsIgnoreCase(virtualServiceRequest.getType().toString()))) {
        return new ResponseEntity<>(
                "{\"message\":\""+messageSource.getMessage("VS_VALIDATION_FAILURE_REJECT", null, locale)+"\"}",
                null, HttpStatus.BAD_REQUEST);
      } else if (virtualServiceRequest.getType() == null) {
        virtualServiceRequest.setType(ResponseProcessType.RESPONSE.toString());
      }
      wsEndpointConfiguration.getWsServiceMockList().entrySet()
          .stream()
          .filter(
              x  -> (x.getValue().getMethod().equalsIgnoreCase(virtualServiceRequest.getMethod()) &&
                    x.getValue().getNs().equalsIgnoreCase(virtualServiceRequest.getUrl())))
          .forEach(y -> {
            try {
              Class reqClazzz = Class.forName(y.getValue().getRequestClassName());
              virtualServiceRequest.setInputObjectType(reqClazzz);
              Class resClazzz = Class.forName(y.getValue().getResponseClassName());
              virtualServiceRequest.setResponseObjectType(resClazzz);
            } catch (ClassNotFoundException e) {
              log.warn("return Class not found : {}" , e.getMessage());
            }
          });
      if(virtualServiceRequest.getResource() == null) {
        virtualServiceRequest.setResource(virtualServiceRequest.getUrl());
      }
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
