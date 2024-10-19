package io.virtualan.message.core;

import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.message.core.MessagingApplication.ResponseMessage;
import io.virtualan.message.core.MessagingApplication.SendMessage;
import io.virtualan.message.core.jms.JMSMessageSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;


/**
 * The type Mqtt service.
 */
@ConditionalOnResource(resources = {"classpath:conf/mqtt-config.json"})
@Service("mqttService")
public class MqttService {

  private static final Logger log = LoggerFactory.getLogger(MqttService.class);
  private String[] brokerUrl;
  private String[] topics;
  private String MQTT_USERNAME;
  private String MQTT_PASSWORD;
  private int COMPLETION_TIMEOUT = 5000;
  private int QOS = 2;
  private boolean retained = true;
  private boolean CLEAN_SESSION = true;
  private int CONNECTION_TIMEOUT = 30;
  private int KEEP_ALIVE_INTERVAL = 60;
  private boolean AUTOMATIC_RECONNECT = true;

  @Autowired
  private MessageUtil messageUtil;

  private JSONObject getMQTTConfiguration() throws IOException {
    InputStream stream = JMSMessageSender.class.getClassLoader()
        .getResourceAsStream("conf/mqtt-config.json");
    String jmsConfigJson = null;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
      jmsConfigJson = br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
    return new JSONObject(jmsConfigJson).getJSONArray("MQTT").getJSONObject(0);
  }

  /**
   * Init.
   *
   * @throws IOException the io exception
   */
  @PostConstruct
  public void init() throws IOException {
    try {
      JSONObject jmsConfigurations = getMQTTConfiguration();
      JSONArray brokerUrls = jmsConfigurations.getJSONArray("broker-url");
      if(brokerUrls.length() ==0){
        log.error(" broker-url parameter is mandatory for MQTT");
        System.exit(0);
      }
      brokerUrl = new String[brokerUrls.length()];
      for (int i = 0; i < brokerUrls.length(); i++) {
        brokerUrl[i] = (brokerUrls.getString(i));
      }
      JSONArray arryTopics = jmsConfigurations.getJSONArray("receiver-queue");
      topics = new String[arryTopics.length()];

      for (int i = 0; i < arryTopics.length(); i++) {
        topics[i] = (arryTopics.getString(i));
      }

      if(jmsConfigurations.optString("username").length() > 0) {
        MQTT_USERNAME = jmsConfigurations.optString("username");
      }

      if(jmsConfigurations.optString("password").length() > 0) {
        MQTT_PASSWORD = jmsConfigurations.optString("password");
      }

      if(jmsConfigurations.optInt("qos") >0) {
        QOS = jmsConfigurations.optInt("qos");
      }

      if(jmsConfigurations.optInt("completionTimeout") >0) {
        COMPLETION_TIMEOUT = jmsConfigurations.optInt("completionTimeout");
      }

      if(!jmsConfigurations.optBoolean("cleanSession")) {
        CLEAN_SESSION = jmsConfigurations.optBoolean("cleanSession");
      }

      if(!jmsConfigurations.optBoolean("automaticReconnect")) {
        AUTOMATIC_RECONNECT = jmsConfigurations.optBoolean("automaticReconnect");
      }

      if(jmsConfigurations.optInt("connectionTimeout") >0) {
        CONNECTION_TIMEOUT = jmsConfigurations.optInt("connectionTimeout");
      }

      if(jmsConfigurations.optInt("keepAliveInterval") >0) {
        KEEP_ALIVE_INTERVAL = jmsConfigurations.optInt("keepAliveInterval");
      }

    } catch (Exception e) {
      log.warn("MQTT conf is not loaded {}" , e.getMessage());
    }
  }

  private MqttConnectOptions connectOptions() {
    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    if(MQTT_USERNAME != null) {
      mqttConnectOptions.setUserName(MQTT_USERNAME);
    }

    if(MQTT_PASSWORD != null) {
      mqttConnectOptions.setPassword(MQTT_PASSWORD.toCharArray());
    }
    mqttConnectOptions.setCleanSession(CLEAN_SESSION);
    mqttConnectOptions.setConnectionTimeout(CONNECTION_TIMEOUT);
    mqttConnectOptions.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
    mqttConnectOptions.setAutomaticReconnect(AUTOMATIC_RECONNECT);
    mqttConnectOptions.setServerURIs(brokerUrl);
    return mqttConnectOptions;
  }

  /**
   * Default mqtt paho client factory default mqtt paho client factory.
   *
   * @return the default mqtt paho client factory
   */
  DefaultMqttPahoClientFactory defaultMqttPahoClientFactory() {
    DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
    clientFactory.setConnectionOptions(connectOptions());
    return clientFactory;
  }

  /**
   * Mqtt paho message driven channel adapter mqtt paho message driven channel adapter.
   *
   * @return the mqtt paho message driven channel adapter
   */
  public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter(){
    MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter
        = new MqttPahoMessageDrivenChannelAdapter(brokerUrl[0],
        UUID.randomUUID().toString(),
        defaultMqttPahoClientFactory(),
        topics);
    mqttPahoMessageDrivenChannelAdapter.setCompletionTimeout(COMPLETION_TIMEOUT);
    mqttPahoMessageDrivenChannelAdapter.setConverter(new DefaultPahoMessageConverter());
    mqttPahoMessageDrivenChannelAdapter.setQos(QOS);
    return mqttPahoMessageDrivenChannelAdapter;
  }

  /**
   * Parse message object.
   *
   * @param message the message
   * @return the message object
   */
  @Transformer
  public MessageObject parse(Message<?> message) {
    MessageObject messageObject = new MessageObject();
    try {
      messageObject.setJsonObject(
          (JSONObject) new JSONTokener((message.getPayload().toString())).nextValue());
      messageObject
          .setInboundTopic(message.getHeaders().get("mqtt_receivedTopic").toString());
      messageObject.setHeaders(message.getHeaders());
      return messageObject;
    } catch (JSONException e) {
      log.warn("parse {}", e.getCause());
    }
    return messageObject;
  }

  private DirectChannel sentToTransformer() {
    return new DirectChannel();
  }

  /**
   * Transformer generic transformer.
   *
   * @return the generic transformer
   */
  public GenericTransformer<Message<?>, MessageObject> transformer() {
    return new GenericTransformer<Message<?>, MessageObject>() {
      @Override
      public MessageObject transform(Message<?> message) {
        return parse(message);
      }

    };
  }

  @Bean
  private ResponseMessage getResponseMessage() {
    return messageObject -> {
      if (messageObject.getJsonObject() != null) {
        VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
        virtualServiceRequest.setInput(messageObject.getJsonObject().toString());
        virtualServiceRequest.setOperationId(messageObject.getInboundTopic());
        virtualServiceRequest.setResource(messageObject.getInboundTopic());
        ReturnMockResponse response = messageUtil.getMatchingRecord(virtualServiceRequest);
        if (response != null && response.getMockResponse() != null) {
          messageObject.setOutputMessage(response.getMockResponse().getOutput());
          messageObject.setOutboundTopic(response.getMockRequest().getMethod());
          if (messageObject.getOutputMessage() == null
              || messageObject.getOutboundTopic() == null) {
            log.info("No outputMessage response configured..");
            return null;
          } else {
            log.info("Response configured.. with ({}) : {}", messageObject.getOutboundTopic(),
                messageObject.getOutputMessage());
            return messageObject;
          }
        }
      }
      log.info("No response configured for the given input");
      return null;

    };
  }



  @Bean
  private SendMessage postMessage() {
    return messageObject -> {
      if (messageObject.getOutboundTopic() != null) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(messageObject.getOutputMessage().getBytes());
        mqttMessage.setQos(QOS);
        mqttMessage.setRetained(retained);
        Mqtt.getInstance(UUID.randomUUID().toString(), brokerUrl[0], connectOptions()).publish(messageObject.getOutboundTopic(), mqttMessage);
      }
      return null;
    };
  }

  /**
   * Mqtt inbound integration flow.
   *
   * @return the integration flow
   */
  @Bean
  public IntegrationFlow mqttInbound() {
    return IntegrationFlow.from(mqttPahoMessageDrivenChannelAdapter())
        .<Message<?>>channel(sentToTransformer())
        .<Message<?>, MessageObject>transform(transformer())
        .<MessageObject>handle(getResponseMessage())
        .handle(postMessage()).get();
  }

}