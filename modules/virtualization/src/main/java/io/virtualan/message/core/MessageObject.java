package io.virtualan.message.core;

import lombok.Data;
import org.json.JSONObject;

@Data
public class MessageObject {

	private JSONObject jsonObject;
	private String outboundTopic;
	private String inboundTopic;

	private String messageKey;
	private String outputMessage;
}
