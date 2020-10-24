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
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * This is Soap Virtual Service .
 *
 * @author  Elan Thangamani
 *
 **/

@Configuration
@Component
@ConditionalOnProperty(name = {"virtualan.soap.package"}, matchIfMissing = true)
public class SoapEndpointCodeGenerator {

  static Logger log = LoggerFactory.getLogger(SoapEndpointCodeGenerator.class);


  public static Class buildEndpointClass(Map<String, SoapService> soapWsServices)
      throws CannotCompileException, IOException {
    ClassPool pool = ClassPool.getDefault();
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    pool.insertClassPath(new LoaderClassPath(cl));
    CtClass cc = pool.makeClass("io.virtualan.VirtualanEndpoint");
    addClassAnnotation(cc);
    for (Entry<String, SoapService> soapWsServiceEntry : soapWsServices.entrySet()) {
      SoapService soapService = soapWsServiceEntry.getValue();
      String methodSignature = generateMethod(soapService);
      CtMethod method = CtMethod.make(methodSignature, cc);

      addParameterAnnotation(cc, method);
      addMethodAnnotation(cc, soapService, method);
    }
    if(log.isDebugEnabled()) {
      cc.getClassFile()
          .write(new DataOutputStream(new FileOutputStream("VirtualanEndpoint.java")));
    }
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
    keyValue.entrySet().stream().forEach(x -> annot2.addMemberValue(
        x.getKey(),
        new StringMemberValue(x.getValue(), cpool))
    );
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
    return new Annotation(annotationName, cp);
  }

  private static String generateMethod(SoapService soapService) {
    StringBuilder sb = new StringBuilder();
    sb.append("public  ")
        .append(soapService.getResponseClassName())
        .append(" ")
        .append(soapService.getMethod())
        .append("( " + soapService.getRequestClassName() + " request " + " )")
        .append("{ return null; }");
    return sb.toString();
  }

  public static void addClassAnnotation(CtClass clazz)  {
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


  protected List<Class> findMyTypes(String basePackage) {
    List<Class> candidates = new ArrayList<>();
    try {
      ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
      MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
          resourcePatternResolver);
      String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
          resolveBasePackage(basePackage) + "/**/*.class";
      Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        if (resource.isReadable()) {
          MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
          if (isCandidate(metadataReader)) {
            candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
          }
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      log.warn("Unable to load the package : {}"  , basePackage);
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
    } catch (Exception e) {
      log.warn(" isCandidate unexpected error {}", e.getMessage());
    }
    return false;
  }

}
