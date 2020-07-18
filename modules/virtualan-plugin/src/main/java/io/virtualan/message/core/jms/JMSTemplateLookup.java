package io.virtualan.message.core.jms;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.jms.core.JmsTemplate;

@ConditionalOnResource(resources = {"classpath:conf/jms-config.json"})
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
