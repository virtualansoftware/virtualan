package io.virtualan.core.soap;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.virtualan.core.model.SoapService;
import io.virtualan.core.model.VirtualServiceRequest;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

@Configuration
@Component
@ConditionalOnProperty(name = {"virtualan.soap.package"}, matchIfMissing = true)
public class SoapEndpointCodeGenerator {

  static Logger log = LoggerFactory.getLogger(SoapEndpointCodeGenerator.class);


  public static Class buildEndpointClass(Map<String, SoapService> soapWsServices)
      throws Exception {
    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.makeClass("io.virtualan.VirtualanEndpoint");
    addClassAnnotation(cc);
    for (Entry<String, SoapService> soapWsServiceEntry : soapWsServices.entrySet()) {
      SoapService soapService = soapWsServiceEntry.getValue();
      String methodSignature = generateMethod(soapService);
      CtMethod method = CtMethod.make(methodSignature, cc);

      addParameterAnnotation(cc, method);
      addMethodAnnotation(cc, soapService, method);
    }
//    log.info("##########################");
//    cc.getClassFile()
//        .write(new DataOutputStream(System.out));
//    log.info("##########################");
//
   return cc.toClass();
  }

  private static void addMethodAnnotation(CtClass cc, SoapService soapService, CtMethod method) {
    Map<String, String> keyValue = new HashMap();
    keyValue.put("namespace", soapService.getNs());
    keyValue.put("localPart", soapService.getLocalPart());
    ConstPool cpool = cc.getClassFile().getConstPool();
    AnnotationsAttribute attr =
        new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
    Annotation annot = new Annotation(
        "org.springframework.ws.server.endpoint.annotation.ResponsePayload", cpool);
    attr.addAnnotation(annot);
    Annotation annot1 = new Annotation("io.virtualan.annotation.ApiVirtual", cpool);
    attr.addAnnotation(annot1);
    Annotation annot2 = new Annotation(
        "org.springframework.ws.server.endpoint.annotation.PayloadRoot", cpool);
    if (keyValue != null) {
      keyValue.entrySet().stream().forEach(x -> annot2.addMemberValue(
          x.getKey(),
          new StringMemberValue(x.getValue(), cpool))
      );
    }
    attr.addAnnotation(annot2);
    method.getMethodInfo().addAttribute(attr);
  }

  private static void addParameterAnnotation(CtClass cc, CtMethod method)
      throws DuplicateMemberException {
    ParameterAnnotationsAttribute oldAns = new ParameterAnnotationsAttribute(
        cc.getClassFile().getConstPool(), ParameterAnnotationsAttribute.visibleTag);
    Annotation[][] anAr = new Annotation[1][1];
    anAr[0] = new Annotation[1];
    anAr[0][0] = constructAnnotation(cc.getClassFile().getConstPool(),
        "org.springframework.ws.server.endpoint.annotation.RequestPayload");
    oldAns.setAnnotations(anAr);

    MethodInfo methodInfo = method.getMethodInfo2();
    methodInfo.addAttribute(oldAns);
    cc.getClassFile().addMethod(methodInfo);
  }


  private static Annotation constructAnnotation(ConstPool cp, String annotationName) {
    Annotation ann = new Annotation(annotationName, cp);
    return ann;
  }

  private static String generateMethod(SoapService soapService)
      throws CannotCompileException {
    StringBuffer sb = new StringBuffer();
    sb.append("public  ")
        .append(soapService.getResponseClassName())
        .append(" ")
        .append(soapService.getMethod())
        .append("( " + soapService.getRequestClassName() + " request " + " )")
        .append("{ return null; }");
    return sb.toString();
  }

  public static void addClassAnnotation(CtClass clazz) throws Exception {
    ClassFile cfile = clazz.getClassFile();
    ConstPool cpool = cfile.getConstPool();
    AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
    Annotation annot = constructAnnotation(cpool,
        "org.springframework.ws.server.endpoint.annotation.Endpoint");
    Annotation annot2 = constructAnnotation(cpool, "io.virtualan.annotation.VirtualService");
    Annotation annot3 = constructAnnotation(cpool,
        "org.springframework.context.annotation.EnableAspectJAutoProxy");
    attr.addAnnotation(annot2);
    attr.addAnnotation(annot3);
    attr.addAnnotation(annot);
    cfile.addAttribute(attr);
  }


  public static void findAnnotatedClasses(String scanPackage) {
    ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
    for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage)) {
      System.out.println(beanDef);
    }
  }

  private static ClassPathScanningCandidateComponentProvider createComponentScanner() {
    ClassPathScanningCandidateComponentProvider provider
        = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AnnotationTypeFilter(WebService.class));
    return provider;
  }

  protected List<Class> findMyTypes(String basePackage) {
    List<Class> candidates = new ArrayList<Class>();
    try {
      ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
      MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
          resourcePatternResolver);
      String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
          resolveBasePackage(basePackage) + "/" + "**/*.class";
      Resource[] resources = new Resource[0];
      resources = resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        if (resource.isReadable()) {
          MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
          if (isCandidate(metadataReader)) {
            candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
          }
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      log.warn("Unable to load the package : " + basePackage);
    }

    return candidates;
  }

  private String resolveBasePackage(String basePackage) {
    return ClassUtils
        .convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
  }

  private boolean isCandidate(MetadataReader metadataReader) throws ClassNotFoundException {
    try {
      Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
      if (c.getAnnotation(WebService.class) != null) {
        return true;
      }
    } catch (Throwable e) {
    }
    return false;
  }

  private Map<String, VirtualServiceRequest> buildVirtualServiceInfo(
      Entry<String, Class> virtualServiceEntry) throws JsonProcessingException,
      InstantiationException, IllegalAccessException, ClassNotFoundException {
    Map<String, VirtualServiceRequest> mockAPILoadChoice =
        new LinkedHashMap<String, VirtualServiceRequest>();
    for (Method method : virtualServiceEntry.getValue().getDeclaredMethods()) {
      WebMethod[] annotInstance = method.getAnnotationsByType(WebMethod.class);
      if (annotInstance != null && annotInstance.length > 0) {
//        VirtualServiceRequest mockReturn = buildServiceDetails(virtualServiceEntry, method);
//        if (mockReturn != null) {
//          mockAPILoadChoice.put(method.getName(), mockReturn);
//        }
      }
    }
    return mockAPILoadChoice;
  }

}
