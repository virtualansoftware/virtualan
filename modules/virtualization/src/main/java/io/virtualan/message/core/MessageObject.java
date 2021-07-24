package io.virtualan.message.core;

import lombok.Data;
import org.json.JSONObject;
import org.springframework.messaging.MessageHeaders;

@Data
public class MessageObject {

	private JSONObject jsonObject;
	private String outboundTopic;
	private String inboundTopic;
	private MessageHeaders headers;

	private String messageKey;
	private String outputMessage;
}
