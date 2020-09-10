package io.virtualan.api;

import java.lang.reflect.Method;
import java.security.KeyPair;
import java.util.AbstractMap;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;


@Component
public class WSResource {

  public static boolean isExists(Method method) {
    PayloadRoot a = method.getAnnotation(PayloadRoot.class);
    return  a != null;
  }

  public static AbstractMap.SimpleEntry<String, String> getResourceParent(Method method) {
    PayloadRoot a = method.getAnnotation(PayloadRoot.class);
    String part = a.localPart();
    String ns = a.namespace().contains("}")  ?  a.namespace().replace("}","").replace("{","") : a.namespace() ;
    AbstractMap.SimpleEntry<String, String> entry
        = new AbstractMap.SimpleEntry<>(part, ns);
    return entry;
  }



}
