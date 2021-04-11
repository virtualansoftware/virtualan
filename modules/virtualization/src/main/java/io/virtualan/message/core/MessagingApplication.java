package io.virtualan.message.core;

import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@ConditionalOnClass(IntegrationFlows.class)
@EnableIntegration
@EnableKafka
@ConditionalOnResource(resources = {"classpath:conf/kafka.json"})
@Service
public class MessagingApplication {

  private static final Logger log = LoggerFactory.getLogger(MessagingApplication.class);
  private static List<NewTopic> topicList = new ArrayList<>();

  private String bootstrapServers;
  private List<String> topics = new ArrayList<>();
  private Map<String, Object> producerConfig = new HashMap<>();
  private Map<String, Object> consumerConfigs = new HashMap<>();

  @Autowired
  private MessageUtil messageUtil;

  private static JSONArray getJsonObject() throws IOException {
    InputStream stream = MessagingApplication.class.getClassLoader()
        .getResourceAsStream("conf/kafka.json");
    if (stream != null) {
      String jmsConfigJson = readString(stream);
      JSONObject jsonObject = new JSONObject(jmsConfigJson);
      return jsonObject.optJSONArray("Kafka");
    }
    return null;
  }

  public static String readString(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  private static NewTopic addNewTopic(String topic) {
    Map<String, String> configs = new HashMap<>();
    int partitions = 5;
    Short replication = 1;
    return new NewTopic(topic, partitions, replication).configs(configs);
  }

  @PostConstruct
  public void init() {
    try {
      JSONArray jsonArray = getJsonObject();
      if (jsonArray != null) {
        JSONObject obj = jsonArray.optJSONObject(0);
        if (obj != null) {
          bootstrapServers = obj.getString("broker");
          JSONArray array = obj.getJSONArray("topics");
          for (int i = 0; i < array.length(); i++) {
            topics.add(array.get(i).toString());
          }
          addTopics();
          bootstrapServers = obj.getString("broker");
          getConfigMap(obj, "consumer", consumerConfigs);
          getConfigMap(obj, "producer", producerConfig);
          if (topicList != null) {
            addTopic(topicList);
          }
        }
      }
    } catch (Exception e) {
      log.error("Unable to load the kafka configuration");
    }
  }

  public Map getConfigMap(JSONObject obj, String consumer,
      Map<String, Object> consumerConfigs) {
    Map configProps = loadProperties(obj.optString(consumer));
    if (configProps != null) {
      consumerConfigs.putAll(configProps);
    }
    return configProps;
  }

  public void addTopics() throws ExecutionException, InterruptedException {
    Set<String> names = getTopics();
    for (String topic : topics) {
      boolean contains = names.contains(topic);
      if (!contains) {
        topicList.add(addNewTopic(topic));
      }
    }
  }

  private Map<String, String> loadProperties(String propFileName) {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
    Map<String, String> mapList = new HashMap<>();
    Properties properties = new Properties();
    if (inputStream != null) {
      try {
        properties.load(inputStream);
        properties.forEach((k, v) -> {
          String sk = k.toString();
          String sv = v.toString();
          mapList.put(sk, sv);
        });
        return mapList;
      } catch (IOException e) {
        log.warn("property file '{}' not found in the classpath.. loading default setting {}",
            propFileName, e.getMessage());
      }
    } else {
      log.warn("property file '{}' not found in the classpath.. loading default setting",
          propFileName);
    }
    return null;
  }

  private AdminClient getAdminClient() {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return AdminClient.create(config);
  }

  public void addTopic(List<NewTopic> topicList) {
    AdminClient admin = getAdminClient();
    admin.createTopics(topicList);
  }

  public boolean isTopicExists(String topic) throws ExecutionException, InterruptedException {
    return getTopics().contains(topic);
  }

  private Set<String> getTopics() throws ExecutionException, InterruptedException {
    AdminClient admin = getAdminClient();
    ListTopicsResult listTopics = admin.listTopics();
    return listTopics.names().get();
  }

  @Bean
  private ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }


  @Bean
  private Map<String, Object> producerConfigs() {
    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    if (producerConfig.size() == 1) {
      producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 1);
      producerConfig.put("acks", "all");
      producerConfig.put("retries", 0);
    }
    return producerConfig;
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    if (consumerConfigs.size() == 1) {
      consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "virtualan-consumer-1");
      consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      consumerConfigs.put("max.poll.records", 1);
      consumerConfigs.put("max.poll.interval.ms", 1000);
      consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
      consumerConfigs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
    }
    return consumerConfigs;
  }

  @Bean
  private ConsumerFactory<?, ?> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean("sentToTransformer")
  private DirectChannel sentToTransformer() {
    return new DirectChannel();
  }


  @Bean("listenerFromKafkaFlow")
  public IntegrationFlow listenerFromKafkaFlow() {
    return IntegrationFlows
        .from(Kafka.messageDrivenChannelAdapter(consumerFactory(),
            KafkaMessageDrivenChannelAdapter.ListenerMode.record,
            topics.toArray(new String[topics.size()]))
            .configureListenerContainer(c ->
                c.ackMode(ContainerProperties.AckMode.RECORD)
                    .ackOnError(true)
                    .idleEventInterval(100L)
                    .id("messageListenerContainer"))
        ).<Message<?>>channel(sentToTransformer())
        .<Message<?>, MessageObject>transform(transformer())
        .<MessageObject>handle(getResponseMessage())
        .handle(postMessage()).get();
  }

  @Bean
  public GenericTransformer<Message<?>, MessageObject> transformer() {
    return new GenericTransformer<Message<?>, MessageObject>() {
      @Override
      public MessageObject transform(Message<?> message) {
        return parse(message);
      }

    };
  }

  @Transformer
  public MessageObject parse(Message<?> message) {
    MessageObject messageObject = new MessageObject();
    try {
      messageObject.setJsonObject(
          (JSONObject) new JSONTokener((message.getPayload().toString())).nextValue());
      messageObject
          .setInboundTopic(message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC).toString());
      return messageObject;
    } catch (JSONException e) {
      log.warn("parse {}", e.getCause());
    }
    return messageObject;
  }

  @Bean
  private KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
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
        Message<String> message = MessageBuilder
            .withPayload(messageObject.getJsonObject().toString())
            .setHeader(KafkaHeaders.TOPIC, messageObject.getOutboundTopic())
            .setHeader(KafkaHeaders.MESSAGE_KEY, messageObject.getMessageKey())
            .setHeader(KafkaHeaders.PARTITION_ID, 0)
            .setHeader("X-Virtualan-Header", "Mock-Service-Response")
            .build();
        kafkaTemplate().send(message);
      }
      return null;
    };
  }

  interface ResponseMessage {

    MessageObject readResponseMessage(MessageObject messageObject);
  }

  interface SendMessage {

    String send(MessageObject messageObject);
  }

}


