package io.virtualan.core.soap;

import io.virtualan.core.model.SoapService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Configuration
@EnableAspectJAutoProxy
public class WSEndpointConfiguration implements BeanFactoryAware {

  private static final Logger log = LoggerFactory.getLogger(WSEndpointConfiguration.class);

  final static Map<String, SoapService> wsServiceMockList = new HashMap<>();
  private BeanFactory beanFactory;
  @Value("${virtualan.soap.wsdl:classpath*:/wsdl/*.wsdl}")
  private String wsdlLocation;
  @Value("${virtualan.soap.package:io.virtualan.ws.domain}")
  private String soapPackage;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public String getName(Document xmlDocument, String expression)
      throws XPathExpressionException {
    XPath xPath = XPathFactory.newInstance().newXPath();
    NodeList nodeList = (NodeList) xPath.compile(expression)
        .evaluate(xmlDocument, XPathConstants.NODESET);
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      return node.getTextContent();
    }
    return null;
  }


  public Map<String, SoapService> getWSOperation(File file)
      throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
    FileInputStream fileIS = new FileInputStream(file);
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    Document xmlDocument = builder.parse(fileIS);
    XPath xPath = XPathFactory.newInstance().newXPath();
    String expression = "/definitions/binding/operation/@name";
    NodeList nodeList = (NodeList) xPath.compile(expression)
        .evaluate(xmlDocument, XPathConstants.NODESET);
    Map<String, SoapService> wsServiceMockList = new HashMap<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      String value = node.getTextContent();
      SoapService soapService = new SoapService();
      soapService.setMethod(value);
      expression = "/definitions/types/schema/@targetNamespace";
      String ns = getName(xmlDocument, expression);
      if (ns == null || ns.trim().equalsIgnoreCase("")) {
        expression = "/definitions/binding/operation[@name = '" + value + "']/operation/@soapAction";
        ns = getName(xmlDocument, expression);
      }
      soapService.setNs(ns);
      expression = "/definitions/portType/operation[@name='" + value + "']/input/@message";
      String input = getActualValue(getName(xmlDocument, expression));
      expression = "/definitions/portType/operation[@name='" + value + "']/output/@message";
      String output = getActualValue(getName(xmlDocument, expression));
      expression = "/definitions/message[@name='" + input + "']/part/@element";
      String inputElement = getActualValue(getName(xmlDocument, expression));
      soapService.setRequestClassName(buildClassType(inputElement));
      expression = "/definitions/message[@name='" + output + "']/part/@element";
      String outputElement = getActualValue(getName(xmlDocument, expression));
      soapService.setResponseClassName(buildClassType(outputElement));
      wsServiceMockList.put(ns.concat("-").concat(soapService.getMethod()), soapService);
    }
    return wsServiceMockList;
  }

  public String buildClassType(String str) {
    return soapPackage + "." + str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private String getActualValue(String input) {
    return input.substring(input.indexOf(":") + 1, input.length());
  }

  @PostConstruct
  public void loadSoapWSservice() throws Exception {
    ClassLoader cl = this.getClass().getClassLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
    Resource[] resources = resolver.getResources( wsdlLocation );
    DefaultListableBeanFactory beanRegistry = (DefaultListableBeanFactory) beanFactory;
    for (Resource fileEntry : resources) {
      log.info("WSDL File processing... >> " + fileEntry.getFile().getName());
      wsServiceMockList.putAll(new WSEndpointConfiguration().getWSOperation(fileEntry.getFile()));
    }
    GenericBeanDefinition virtualanSOAPWS = new GenericBeanDefinition();
    virtualanSOAPWS.setBeanClass(SoapEndpointCodeGenerator.buildEndpointClass(wsServiceMockList));
    beanRegistry.registerBeanDefinition("virtualanSOAPWS", virtualanSOAPWS);

  }
}
