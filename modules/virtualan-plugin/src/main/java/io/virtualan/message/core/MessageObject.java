package io.virtualan.message.core;

import lombok.Data;
import org.json.JSONObject;

@Data
public class MessageObject {
	String outboundTopic;
	String inboundTopic;
	JSONObject jsonObject;
	String messageKey;
	String outputMessage;
}
