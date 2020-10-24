package io.virtualan.api;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;


@Component
public class WSResource {

  private WSResource(){
  }

  public static boolean isExists(Method method) {
    PayloadRoot a = method.getAnnotation(PayloadRoot.class);
    return  a != null;
  }

  public static AbstractMap.SimpleEntry<String, String> getResourceParent(Method method) {
    PayloadRoot a = method.getAnnotation(PayloadRoot.class);
    String part = a.localPart();
    String ns = a.namespace().contains("}")  ?  a.namespace().replace("}","").replace("{","") : a.namespace() ;
    return new AbstractMap.SimpleEntry<>(part, ns);
  }

}
