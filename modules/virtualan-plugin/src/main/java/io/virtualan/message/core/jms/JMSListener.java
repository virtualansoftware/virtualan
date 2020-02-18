package io.virtualan.message.core.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSListener implements MessageListener {

  private static final Logger log = LoggerFactory.getLogger(JMSListener.class);

  String queueName;

  public JMSListener(String queueName) {
    this.queueName = queueName;
  }

  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        String text = ((TextMessage) message).getText();
        JMSMessageSender.sendMessage(queueName, text);
      } catch (JMSException e) {
        log.error(
            queueName + " has erro posting message : " + e.getErrorCode() + " : " + e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        log.error(queueName + " has erro posting message : " + e.getMessage());
      }
    }
  }
}