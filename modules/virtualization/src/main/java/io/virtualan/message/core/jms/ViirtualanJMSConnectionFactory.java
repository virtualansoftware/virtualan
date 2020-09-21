package io.virtualan.message.core.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

interface VirtualanJMSConnectionFactory  {
  ConnectionFactory connectionFactory(JMSConfigurationDomain conf) throws JMSException;
  String getJMSType();
}
