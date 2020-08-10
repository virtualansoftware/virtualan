package io.virtualan.core.soap;

import io.virtualan.core.model.SoapService;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.stereotype.Component;

public class SoapEndpointCodeGenerator {

  public static Class buildEndpointClass(Map<String, SoapService> soapWsServices)
      throws Exception {
    ClassPool pool = ClassPool.getDefault();
    CtClass cc = pool.makeClass("io.virtualan.VirtualanEndpoint");
    addClassAnnotation(cc);
    for (Map.Entry<String, SoapService> soapWsServiceEntry : soapWsServices.entrySet()) {
      SoapService soapService = soapWsServiceEntry.getValue();
      String methodSignature = generateMethod(soapService);
      CtMethod method = CtMethod.make(methodSignature, cc);

      addParameterAnnotation(cc, method);
      addMethodAnnotation(cc, soapService, method);
    }
    cc.getClassFile()
        .write(new DataOutputStream(new FileOutputStream("VirtualanEndpoint.class")));
    return cc.toClass();
  }

  private static void addMethodAnnotation(CtClass cc, SoapService soapService, CtMethod method) {
    Map<String, String> keyValue = new HashMap();
    keyValue.put("namespace", soapService.getNs());
    keyValue.put("localPart", soapService.getMethod());
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

}
