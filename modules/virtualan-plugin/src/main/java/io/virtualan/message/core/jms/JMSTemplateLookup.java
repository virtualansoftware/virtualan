package io.virtualan.message.core.jms;

import java.util.HashMap;
import java.util.Map;
import org.springframework.jms.core.JmsTemplate;

public class JMSTemplateLookup {

  private static Map<String, JmsTemplate> jmsTemplateMap = new HashMap<>();

  private JMSTemplateLookup(){
  }

  public static void loadTemplate(String queueName, JmsTemplate jmsTemplate){
    jmsTemplateMap.put(queueName, jmsTemplate);
  }

  public static Map<String, JmsTemplate> getJmsTemplateMap(){
    return jmsTemplateMap;
  }

  public static JmsTemplate getJmsTemplate(String queueName){
    return jmsTemplateMap.get(queueName);
  }

}
