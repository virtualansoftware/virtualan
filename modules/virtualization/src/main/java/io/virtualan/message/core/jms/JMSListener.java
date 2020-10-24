package io.virtualan.message.core.jms;

import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.message.core.MessageObject;
import io.virtualan.message.core.MessageUtil;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.stereotype.Service;

@ConditionalOnResource(resources = {"classpath:conf/jms-config.json"})
@Service
@Slf4j
public class JMSListener implements MessageListener {

  @Autowired
  private MessageUtil messageUtil;

  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        String text = ((TextMessage) message).getText();
        MessageObject messageObject = new MessageObject();
        messageObject.setJsonObject((JSONObject) new JSONTokener((text)).nextValue());
        VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
        virtualServiceRequest.setInput(messageObject.getJsonObject().toString());
        String inputTopic = message.getJMSDestination().toString();
        messageObject.setInboundTopic(inputTopic.substring(inputTopic.indexOf('/')+2));
        virtualServiceRequest.setOperationId(messageObject.getInboundTopic());
        virtualServiceRequest.setResource(messageObject.getInboundTopic());
        ReturnMockResponse response = messageUtil.getMatchingRecord(virtualServiceRequest);
        processMethod( messageObject, response);
      } catch (JSONException e) {
        log.error(" Invalid message : {}" , e.getMessage());
      } catch (JMSException e) {
        log.error( " has error posting message : {} : {}", e.getErrorCode() , e.getMessage());
      } catch (Exception e) {
        log.error( " has error posting message : : {}", e.getMessage());
      }
    }
  }

  private void processMethod( MessageObject messageObject,
      ReturnMockResponse response) {
    if (response != null && response.getMockResponse() != null) {
      messageObject.setOutputMessage(response.getMockResponse().getOutput());
      messageObject.setOutboundTopic(response.getMockRequest().getMethod());
      if (messageObject.getOutputMessage() == null || messageObject.getOutboundTopic() == null) {
        log.info("No outputMessage response configured..");
      } else {
        log.info("Response configured.. with ({}) : {}", messageObject.getOutboundTopic(),  messageObject.getOutputMessage());
        JMSMessageSender.sendMessage(messageObject.getInboundTopic(), messageObject.getOutboundTopic(), messageObject.getOutputMessage());
      }
    } else {
      log.info("No response configured for the given input");
    }
  }
}