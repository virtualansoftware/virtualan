package io.virtualan.aop;

import io.virtualan.core.model.MockServiceRequest;
import java.lang.annotation.Annotation;
import java.util.Map;
import lombok.Data;
import org.aspectj.lang.reflect.MethodSignature;

@Data
public class ServiceParamObject {

  private  Object[] args;
  private  MethodSignature methodSignature;
  private  MockServiceRequest mockServiceRequest;
  private  Map<String, String> paramMap;
  private  Map<String, Object> parameters;
  private  Map<String, Class> paramMapType;
  private  int argIndex;
  private  String requestParamName;
  private  Annotation annotation;

}
