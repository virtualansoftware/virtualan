package io.virtualan.message.core.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JMSMessageSender {

  private static final Logger log = LoggerFactory.getLogger(JMSMessageSender.class);

  @Autowired
  private BeanFactory beanFactory;

  public static void sendMessage(String queueName, String message) {
    log.info(JMSTemplateLookup.getJmsTemplateMap().toString());
    log.info("sending: " + message);

    JMSTemplateLookup.getJmsTemplate(queueName).send(queueName, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(message);
      }
    });
  }

  public ConnectionFactory connectionFactory(JMSConfigurationDomain conf) {
    ConnectionFactory connectionFactory = null;
    if (conf.getUserName() != null && conf.getPassword() != null) {
      connectionFactory = new ActiveMQConnectionFactory(conf.getUserName(), conf.getPassword(),
          conf.getBrokerUrl());
    } else {
      connectionFactory = new ActiveMQConnectionFactory(conf.getBrokerUrl());
    }
    return connectionFactory;
  }


  public String readString(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  @PostConstruct
  public void init() throws IOException {
    try {
      JSONArray array = getJMSConfiguration();
      for (int i = 0; i < array.length(); i++) {
        JSONObject object = array.optJSONObject(i);
        JMSConfigurationDomain conf = getJmsConfigurationDomain(object);
        if (conf.getReceiverQueueName() == conf.getSenderQueueName()) {
          log.info("JMS conf is not valid to be loaded :  " + conf);
          continue;
        }
        if (conf.getReceiverQueueName() != null) {
          registerListenerBeans(conf);
        }

        if (conf.getSenderQueueName() != null) {
          JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory(conf));
          JMSTemplateLookup.loadTemplate(conf.getSenderQueueName(), jmsTemplate);
          log.info(JMSTemplateLookup.getJmsTemplateMap().toString());
        }
        log.info("JMS conf loaded : " + conf);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private JSONArray getJMSConfiguration() throws IOException {
    InputStream stream = JMSMessageSender.class.getClassLoader()
        .getResourceAsStream("conf/jms-config.json");
    String jmsConfigJson = readString(stream);
    JSONObject jsonObject = new JSONObject(jmsConfigJson);
    return jsonObject.optJSONArray("AMQ");
  }

  private JMSConfigurationDomain getJmsConfigurationDomain(JSONObject object) {
    JMSConfigurationDomain conf = new JMSConfigurationDomain();
    conf.setSystem(object.getString("systemName"));
    conf.setBrokerUrl(object.getString("broker-url"));
    conf.setUserName(object.optString("user"));
    conf.setPassword(object.optString("password"));
    conf.setReceiverQueueName(object.getString("request-queue"));
    conf.setSenderQueueName(object.optString("response-queue"));
    return conf;
  }

  public void registerListenerBeans(JMSConfigurationDomain conf) {
    GenericBeanDefinition jmsBean = new GenericBeanDefinition();
    jmsBean.setBeanClass(JMSListener.class);
    BeanDefinitionBuilder bean = BeanDefinitionBuilder
        .rootBeanDefinition(DefaultMessageListenerContainer.class)
        .addPropertyValue("connectionFactory", connectionFactory(conf))
          .addPropertyValue("destinationName", conf.getReceiverQueueName())
        .addPropertyValue("messageListener", jmsBean);
    DefaultListableBeanFactory beanRegistry = (DefaultListableBeanFactory) beanFactory;
    beanRegistry.registerBeanDefinition(conf.getSystem(), bean.getBeanDefinition());

    DefaultMessageListenerContainer messageListenerContainer = beanFactory.getBean(conf.getSystem(), DefaultMessageListenerContainer.class);
    if (!messageListenerContainer.isRunning()) {
      log.info(conf.getSystem() + " bean registered successfully.. and Started JmsListenerContainer");
      messageListenerContainer.start();
    }
  }

}