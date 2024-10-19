package io.virtualan.message.core.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

@ConditionalOnClass({ActiveMQConnectionFactory.class})
@Service
public class ActiveMQConnectionFactory implements VirtualanJMSConnectionFactory  {

  @Override
  public jakarta.jms.ConnectionFactory connectionFactory(JMSConfigurationDomain conf) throws JMSException {
    ConnectionFactory connectionFactory = null;
    if (conf.getUserName() != null && conf.getPassword() != null) {
      connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(conf.getUserName(), conf.getPassword(),
          conf.getBrokerUrl());
    } else {
      connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(conf.getBrokerUrl());
    }
    return connectionFactory;
  }

  @Override
  public String getJMSType() {
    return "AMQ";
  }

}
