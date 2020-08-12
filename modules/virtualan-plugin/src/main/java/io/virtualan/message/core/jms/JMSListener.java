package io.virtualan.message.core.jms;

import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.message.core.MessageObject;
import io.virtualan.message.core.MessageUtil;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;

@ConditionalOnResource(resources = {"classpath:conf/jms-config.json"})
public class JMSListener implements MessageListener {

  private static final Logger log = LoggerFactory.getLogger(JMSListener.class);
  String queueName;
  @Autowired
  private MessageUtil messageUtil;

  public JMSListener(String queueName) {
    this.queueName = queueName;
  }

  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        String text = ((TextMessage) message).getText();
        MessageObject messageObject = new MessageObject();
        messageObject.jsonObject = (JSONObject) new JSONTokener((text)).nextValue();
        VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
        virtualServiceRequest.setInput(messageObject.jsonObject.toString());
        virtualServiceRequest.setOperationId(messageObject.inboundTopic);
        virtualServiceRequest.setResource(messageObject.inboundTopic);
        ReturnMockResponse response = messageUtil.getMatchingRecord(virtualServiceRequest);
        if (response != null && response.getMockResponse() != null) {
          messageObject.outputMessage = response.getMockResponse().getOutput();
          messageObject.outboundTopic = response.getMockRequest().getMethod();
          if (messageObject.outputMessage == null || messageObject.outboundTopic == null) {
            log.info("No outputMessage response configured..");
          } else {
            log.info("Response configured.. with (" + messageObject.outboundTopic + ") :"
                + messageObject.outputMessage);
            JMSMessageSender.sendMessage(messageObject.outboundTopic, text);
          }
        } else {
          log.info("No response configured for the given input");
          JMSMessageSender.sendMessage(queueName, text);
        }

      } catch (JSONException e) {
        log.error(
            queueName + " Invalid message : " + e.getMessage());
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