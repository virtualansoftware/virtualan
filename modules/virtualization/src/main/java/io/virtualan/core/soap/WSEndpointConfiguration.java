/*
 * Copyright 2020 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.core.soap;

import io.virtualan.core.model.SoapService;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.jws.WebParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This is Soap Virtual Service .
 *
 * @author  Elan Thangamani
 *
 **/


@Component
@Configuration
@ConditionalOnProperty(name = {"virtualan.soap.package"}, matchIfMissing = false)
public class WSEndpointConfiguration implements BeanFactoryAware {

  final static Map<String, SoapService> wsServiceMockList = new HashMap<>();
  private static final Logger log = LoggerFactory.getLogger(WSEndpointConfiguration.class);

  @Autowired
  private SoapEndpointCodeGenerator soapEndpointCodeGenerator;
  private BeanFactory beanFactory;
  @Value("${virtualan.soap.package:io.virtualan.ws.domain}")
  private String soapPackage;

  public Map<String, SoapService> getWsServiceMockList() {
    return wsServiceMockList;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @PostConstruct
  public void loadSoapWSservice() throws Exception {
    DefaultListableBeanFactory beanRegistry = (DefaultListableBeanFactory) beanFactory;
    Map<String, String> documentations = getDocumentations();
    Arrays.stream(soapPackage.split(";")).forEach(packageName -> {
          List<Class> portTypeList = soapEndpointCodeGenerator.findMyTypes(packageName);

          for (Class clazz : portTypeList) {
            Method[] methods = clazz.getDeclaredMethods();
            Arrays.stream(methods).forEach(method -> {
              loadParameters(method, documentations.get(method.getName()));
            });
          }
        }
    );

    GenericBeanDefinition virtualanSOAPWS = new GenericBeanDefinition();
    virtualanSOAPWS.setBeanClass(soapEndpointCodeGenerator.buildEndpointClass(wsServiceMockList));
    beanRegistry.registerBeanDefinition("virtualanSOAPWS", virtualanSOAPWS);
  }


  public static void main(String[] args)
      throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
    new WSEndpointConfiguration().getDocumentations();
  }

  private Map<String, String> getDocumentations()
      throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
    Map<String, String> documentations = new HashMap<>();
    List<NodeList> nodeList = new ArrayList<>();
    List<String> lists = Arrays
        .asList("classpath:META-INF/resources/wsdl/*/*.wsdl");
    for (String pathName : lists) {
      final Resource[] resources = getCatalogList(pathName);
      for (final Resource file : resources) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(file.getURI().toString());
        NodeList nl = eval(xmlDocument);
        nodeList.add(nl);
      }
    }
    nodeList.stream().forEach(x -> {
      for(int i = 0; i < x.getLength(); i++) {
      if(!documentations.containsKey(x.item(i).getAttributes().getNamedItem("name").getNodeValue())){
        documentations
          .put(x.item(i).getAttributes().getNamedItem("name").getNodeValue(), x.item(i).getTextContent().trim());
        }
      }
    });
    return documentations;
  }

  private Resource[] getCatalogList(String path) throws IOException {
    final ClassLoader classLoader = MethodHandles.lookup().getClass().getClassLoader();

    final PathMatchingResourcePatternResolver resolver =
        new PathMatchingResourcePatternResolver(classLoader);

    return resolver.getResources(path);
  }

  public NodeList eval(final Document doc)
      throws XPathExpressionException {
    String pathStr = "//operation";
    final XPath xpath = XPathFactory.newInstance().newXPath();
    final XPathExpression expr = xpath.compile(pathStr);
    return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
  }


  public void loadParameters(Method method, String desc) {
    RequestWrapper requestWrapper = AnnotationUtils.findAnnotation(method, RequestWrapper.class);
    if (requestWrapper != null) {
      SoapService soapService = new SoapService();
      soapService.setNs(requestWrapper.targetNamespace());
      soapService.setMethod(method.getName());
      soapService.setLocalPart(requestWrapper.localName());
      soapService.setRequestClassName(requestWrapper.className());
      soapService.setDescription(desc);
      ResponseWrapper responseWrapper = AnnotationUtils
          .findAnnotation(method, ResponseWrapper.class);
      soapService.setResponseClassName(responseWrapper.className());
      wsServiceMockList.put(soapService.getNs() + "_" + soapService.getMethod(), soapService);
    } else {
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

}
