package io.virtualan.core.soap;

import io.virtualan.core.model.SoapService;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.jws.WebParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConditionalOnProperty(name = {"virtualan.soap.wsdl", "virtualan.soap.package"}, matchIfMissing = false)
public class WSEndpointConfiguration implements BeanFactoryAware {

  final static Map<String, SoapService> wsServiceMockList = new HashMap<>();
  private static final Logger log = LoggerFactory.getLogger(WSEndpointConfiguration.class);

  @Autowired
  private SoapEndpointCodeGenerator soapEndpointCodeGenerator;

  private BeanFactory beanFactory;
  @Value("${virtualan.soap.wsdl:classpath*:/wsdl/*}")
  private String wsdlLocation;
  @Value("${virtualan.soap.package:io.virtualan.ws.domain}")
  private String soapPackage;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }


  @PostConstruct
  public void loadSoapWSservice() throws Exception {
    DefaultListableBeanFactory beanRegistry = (DefaultListableBeanFactory) beanFactory;

    List<Class> portTypeList = soapEndpointCodeGenerator.findMyTypes("io.virtualan");
    for(Class clazz : portTypeList) {
      Method[] methods = clazz.getDeclaredMethods();
      Arrays.stream(methods).forEach( method  -> {
        loadParameters(method);
      });
    }

    GenericBeanDefinition virtualanSOAPWS = new GenericBeanDefinition();
    virtualanSOAPWS.setBeanClass(soapEndpointCodeGenerator.buildEndpointClass(wsServiceMockList));
    beanRegistry.registerBeanDefinition("virtualanSOAPWS", virtualanSOAPWS);

  }

  public void loadParameters(Method method) {
    Annotation[][] annotations = method.getParameterAnnotations();
    for (Annotation[] annotationRow : annotations) {
      for (Annotation annotation : annotationRow) {
        if (annotation instanceof WebParam) {
          WebParam clzzz = (WebParam) annotation;
          SoapService soapService = new SoapService();
          soapService.setNs(clzzz.targetNamespace());
          soapService.setMethod(method.getName());
          soapService.setLocalPart(clzzz.name());
          soapService.setRequestClassName(method.getParameters()[0].getType().getCanonicalName());
          soapService.setResponseClassName(method.getReturnType().getTypeName());
          wsServiceMockList.put(soapService.getNs() + "_" + soapService.getMethod(), soapService);
          return;
        }
      }
    }
  }

}
