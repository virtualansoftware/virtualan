package io.virtualan.message.core.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnResource(resources = {"classpath:conf/jms-config.json"})
@Slf4j
public class JMSMessageSender {

  List<VirtualanJMSConnectionFactory> virtualanJMSConnectionFactory;

  @Autowired
  private BeanFactory beanFactory;

  static void sendMessage(String inboundTopic, String outboudTopic, String message) {
    log.info(JMSTemplateLookup.getJmsTemplateMap().toString());
    log.info("sending:{} " , message);

    JMSTemplateLookup.getJmsTemplate(inboundTopic).send(outboudTopic, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(message);
      }
    });
  }

  @Autowired
  private void  allVirtualanJMSConnectionFactory(List<VirtualanJMSConnectionFactory> virtualanJMSConnectionFactory) {
    this.virtualanJMSConnectionFactory = virtualanJMSConnectionFactory;
  }


  private ConnectionFactory connectionFactory(JMSConfigurationDomain conf) throws JMSException {
      for(VirtualanJMSConnectionFactory virtualanJMSConnection : this.virtualanJMSConnectionFactory){
        if(conf.getJmsType().equalsIgnoreCase(virtualanJMSConnection.getJMSType())) {
          return virtualanJMSConnection.connectionFactory(conf);
        }
      }
      log.warn("JMS Conf JMSType {} :: {} ",  conf.getJmsType() , conf );
      throw new JMSException("JMS Conf JMSType is not found" + conf.getJmsType());
  }


  private String readString(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  @PostConstruct
  public void init() throws IOException {
    try {
      JSONObject jmsConfigurations = getJMSConfiguration();
      Iterator<String> keys = jmsConfigurations.keys();
      while(keys.hasNext()) {
        String key = keys.next();
        JSONArray array = jmsConfigurations.getJSONArray(key);
        if(array != null && array.length() > 0) {
          buildJMSListener(array, key);
        }
      }
    } catch (Exception e) {
      log.error("JMS conf is not loaded {}", e.getMessage());
    }
  }

  private void buildJMSListener(JSONArray array, String jmsType){
    for (int i = 0; i < array.length(); i++) {
      JSONObject object = array.optJSONObject(i);
      JMSConfigurationDomain conf = getJmsConfigurationDomain(object, jmsType);
      if (conf.getReceiverQueueName().contains(conf.getSenderQueueName())) {
        log.info("JMS conf is not valid to be loaded : {} " , conf);
        continue;
      }
      if (conf.getReceiverQueueName() != null) {
        registerListenerBeans(conf);
      }
      if (conf.getSenderQueueName() != null) {
        try {
          JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory(conf));
          for (String queue : conf.getReceiverQueueName()) {
            JMSTemplateLookup.loadTemplate(queue, jmsTemplate);
          }
          log.info(JMSTemplateLookup.getJmsTemplateMap().toString());
        }catch (JMSException e){
          log.warn("JMS Exception error : {} : conf : {}", e.getMessage(), conf);
        }
      }
      log.info("JMS conf loaded : {} " , conf);
    }
  }

  private JSONObject getJMSConfiguration() throws IOException {
    InputStream stream = JMSMessageSender.class.getClassLoader()
        .getResourceAsStream("conf/jms-config.json");
    String jmsConfigJson = readString(stream);
    return new JSONObject(jmsConfigJson);
  }

  private List<String> getReceiverQueues(JSONObject object)  {
    List<String> receiverQueue = new ArrayList<>();
    for(int i=0; i < object.getJSONArray("receiver-queue").length(); i++) {
      receiverQueue.add((String) object.getJSONArray("receiver-queue").get(i));
    }
    return receiverQueue;
  }

  private JMSConfigurationDomain getJmsConfigurationDomain(JSONObject object, String jmsType) {
    JMSConfigurationDomain conf = new JMSConfigurationDomain();
    conf.setJmsType(jmsType);
    if("IBMMQ".equalsIgnoreCase(jmsType)) {
     conf.setSystem(object.getString("systemName"));
     conf.setHost(object.getString("host"));
     conf.setPort(object.getInt("port"));
     conf.setQueueMgr(object.getString("queue-mgr"));
     conf.setChannel(object.getString("channel"));
     conf.setUserName(object.optString("username"));
     conf.setPassword(object.optString("password"));
     conf.setReceiverQueueName(getReceiverQueues(object));
     conf.setSenderQueueName(object.optString("response-queue"));
   } else {
     conf.setSystem(object.getString("systemName"));
     conf.setBrokerUrl(object.getString("broker-url"));
     conf.setUserName(object.optString("user"));
     conf.setPassword(object.optString("password"));
     conf.setReceiverQueueName(getReceiverQueues(object));
     conf.setSenderQueueName(object.optString("response-queue"));
    }
    return conf;
  }

  private void registerListenerBeans(JMSConfigurationDomain conf) {
    for(String queue : conf.getReceiverQueueName()) {
      try {
        GenericBeanDefinition jmsBean = new GenericBeanDefinition();
        jmsBean.setBeanClass(JMSListener.class);
        BeanDefinitionBuilder bean = BeanDefinitionBuilder
            .rootBeanDefinition(DefaultMessageListenerContainer.class)
            .addPropertyValue("connectionFactory", connectionFactory(conf))
            .addPropertyValue("destinationName", queue)
            .addPropertyValue("messageListener", jmsBean);
        DefaultListableBeanFactory beanRegistry = (DefaultListableBeanFactory) beanFactory;
        beanRegistry.registerBeanDefinition(conf.getSystem().concat(queue), bean.getBeanDefinition());

        DefaultMessageListenerContainer messageListenerContainer = beanFactory
            .getBean(conf.getSystem().concat(queue), DefaultMessageListenerContainer.class);
        if (!messageListenerContainer.isRunning()) {
          log.info( "{} bean registered successfully.. and Started JmsListenerContainer", conf.getSystem());
          messageListenerContainer.start();
        }
      }catch (JMSException e){
        log.warn("JMS  Listener register Exception error : {} : conf : {}" , e.getMessage(), conf);
      }
    }
  }

}