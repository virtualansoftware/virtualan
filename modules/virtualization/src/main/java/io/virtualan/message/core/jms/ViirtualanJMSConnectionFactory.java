package io.virtualan.message.core.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

interface VirtualanJMSConnectionFactory  {
  ConnectionFactory connectionFactory(JMSConfigurationDomain conf) throws JMSException, jakarta.jms.JMSException;
  String getJMSType();
}
