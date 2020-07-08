package io.virtualan.message.core;

import io.virtualan.controller.VirtualMessageController;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.*;
import springfox.documentation.service.ResponseMessage;

@Configuration
@EnableIntegration
@EnableKafka
@ConditionalOnClass({Kafka.class, IntegrationFlows.class, MessageChannelSpec.class})
@ConditionalOnResource(resources = {"classpath:kafka.json"})
public class MessagingApplication {
	
	private static final Logger log = LoggerFactory.getLogger(MessagingApplication.class);
	
	public static List<NewTopic> topicList = new ArrayList<NewTopic>();
	
	@Autowired
	private MessageUtil messageUtil;
	
	
	private static String bootstrapServers;
	private static String topicString;
	static {
		try {
			JSONObject obj =  getJsonObject().optJSONObject(0);
			if(obj != null ) {
				bootstrapServers = obj.getString("broker");
				topicString = obj.getString("topics");
				Set<String> names = getTopics();
				for (String topic : topicString.split(",")) {
					boolean contains = names.contains(topic);
					if (!contains) {
						topicList.add(addNewTopic(topic));
					}
				}
				if (topicList != null)
					addTopic(topicList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JSONArray getJsonObject() throws Exception {
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


	private static AdminClient getAdminClient() {
		Properties config = new Properties();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		return AdminClient.create(config);
	}
	
	public static void addTopic(List<NewTopic> topicList) {
		AdminClient admin = getAdminClient();
		admin.createTopics(topicList);
	}
	
	
	public static boolean isTopicExists(String topic) throws Exception {
		return getTopics().contains(topic);
	}
	
	private static NewTopic addNewTopic(String topic) {
		Map<String, String> configs = new HashMap<String, String>();
		int partitions = 5;
		Short replication = 1;
		return new NewTopic(topic, partitions, replication).configs(configs);
	}
	
	private static Set<String> getTopics() throws Exception {
		AdminClient admin = getAdminClient();
		ListTopicsResult listTopics = admin.listTopics();
		return listTopics.names().get();
	}
	
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<String, Object>(producerConfigs());
	}
	
	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		properties.put("acks", "all");
		properties.put("retries", 0);
		return properties;
	}
	
	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "virtualan-consumer-1");
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put("max.poll.records", 1);
		properties.put("max.poll.interval.ms", 1000);
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
		return properties;
	}
	
	@Bean
	public ConsumerFactory<?, ?> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}
	
	@Bean
	public DirectChannel sentToTransformer() {
		return new DirectChannel();
	}
	
	
	@Bean
	public DirectChannel listeningFromTransformer() {
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow listenerFromKafkaFlow() {
		
		return IntegrationFlows
				.from(Kafka.messageDrivenChannelAdapter(consumerFactory(),
						KafkaMessageDrivenChannelAdapter.ListenerMode.record,  topicString.split(",")
								)
								.configureListenerContainer(c ->
								c.ackMode(ContainerProperties.AckMode.RECORD)
										.ackOnError(true)
										.idleEventInterval(100L)
										.id("messageListenerContainer"))
						).<Message<?>>channel(sentToTransformer())
				. <Message<?>, MessageObject>transform(transformer())
				.channel(listeningFromTransformer())
				.get();
	}
	
	@Bean
	public GenericTransformer<Message<?>, MessageObject> transformer() {
		return new GenericTransformer<Message<?>, MessageObject>() {
			@Override
			public MessageObject transform(Message<?> message) {
				return parse( message);
			}
			
		};
	}
	
	@Transformer
	public MessageObject parse(Message<?> message) {
		MessageObject messageObject = new MessageObject();
		try {
			messageObject.jsonObject = (JSONObject) new JSONTokener((message.getPayload().toString())).nextValue();
			//messageObject.outboundTopic = "virtualan-dummy-outbound";
			messageObject.inboundTopic = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC).toString();
			return messageObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return messageObject;
	}
	
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<String, Object>(producerFactory());
	}
	
	@Bean
	public IntegrationFlow outboundGateFlow() {
		return IntegrationFlows.from(listeningFromTransformer()).handle(getResponseMessage())
				.handle(postMessage())
				.get();
	}
	
	@Bean
	public ResponseMessage getResponseMessage() {
		return new ResponseMessage() {
			@Override
			public MessageObject readResponseMessage(MessageObject messageObject) {
					VirtualServiceRequest virtualServiceRequest = new VirtualServiceRequest();
					virtualServiceRequest.setInput(messageObject.jsonObject.toString());
					virtualServiceRequest.setOperationId(messageObject.inboundTopic);
					virtualServiceRequest.setResource(messageObject.inboundTopic);
					ReturnMockResponse response  = messageUtil.getMatchingRecord(virtualServiceRequest);
					messageObject.outputMessage = response.getMockResponse().getOutput() ;
					messageObject.outboundTopic =  response.getMockRequest().getMethod();
					if(messageObject.outputMessage == null || messageObject.outboundTopic == null){
						log.info("No response configured.." );
						return null;
					} else {
						log.info("Response configured.. with ("+messageObject.outboundTopic+") :" + messageObject.outputMessage );
						return messageObject;
					}
			}
		};
	}
	
	@Bean
	public SendMessage postMessage() {
		return new SendMessage() {
			@Override
			public String send(MessageObject messageObject) {
				if (messageObject.outboundTopic != null) {
					Message<String> message = MessageBuilder
							.withPayload(messageObject.jsonObject.toString())
							.setHeader(KafkaHeaders.TOPIC, messageObject.outboundTopic)
							.setHeader(KafkaHeaders.MESSAGE_KEY, messageObject.messageKey)
							.setHeader(KafkaHeaders.PARTITION_ID, 0)
							.setHeader("X-Virtualan-Header", "Mock-Service-Response")
							.build();
					kafkaTemplate().send(message);
				}
				return null;
			}
		};
	}
	
	interface ResponseMessage {
		MessageObject readResponseMessage(MessageObject messageObject);
	};
	
	interface SendMessage {
		String send(MessageObject messageObject);
	};
	
}


