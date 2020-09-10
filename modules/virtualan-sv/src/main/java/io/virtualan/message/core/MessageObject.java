package io.virtualan.message.core;

import lombok.Data;
import org.json.JSONObject;

@Data
public class MessageObject {

	public JSONObject jsonObject;
	public String outboundTopic;
	public String inboundTopic;

	public String messageKey;
	public String outputMessage;
}
