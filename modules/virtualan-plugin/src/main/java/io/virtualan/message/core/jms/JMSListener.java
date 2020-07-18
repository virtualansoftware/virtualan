package io.virtualan.message.core.jms;

import io.virtualan.message.core.MessageObject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.kafka.support.KafkaHeaders;

@ConditionalOnResource(resources = {"classpath:conf/jms-config.json"})
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

        MessageObject messageObject = new MessageObject();
        try {
          messageObject.jsonObject = (JSONObject) new JSONTokener((text)).nextValue();
          //messageObject.outboundTopic = "virtualan-dummy-outbound";
          //messageObject.inboundTopic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC).toString();
          JMSMessageSender.sendMessage(queueName, text);
        } catch (JSONException e) {
          e.printStackTrace();
        }
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