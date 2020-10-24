package io.virtualan.message.core.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;


@ConditionalOnClass({MQQueueConnectionFactory.class})
@Service
public class IBMMQConnectionFactory implements  VirtualanJMSConnectionFactory{

  @Override
  public ConnectionFactory connectionFactory(JMSConfigurationDomain conf) throws JMSException {
    MQQueueConnectionFactory connectionFactory = null;
    connectionFactory = new MQQueueConnectionFactory();
    if (conf.getUserName() != null && conf.getPassword() != null) {
      //userCredentialsConnectionFactoryAdapter
    }
    connectionFactory.setTransportType(1);
    connectionFactory.setCCSID(1208);
    connectionFactory.setHostName(conf.getHost());
    connectionFactory.setQueueManager(conf.getQueueMgr());
    connectionFactory.setPort(conf.getPort());
    connectionFactory.setChannel(conf.getChannel());

    return connectionFactory;
  }

  @Override
  public String getJMSType() {
    return "IBMMQ";
  }

}
