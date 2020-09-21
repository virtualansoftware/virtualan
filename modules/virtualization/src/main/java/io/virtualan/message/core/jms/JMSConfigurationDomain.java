package io.virtualan.message.core.jms;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JMSConfigurationDomain {
  String system;
  String brokerUrl;
  String userName;
  String password;
  List<String> receiverQueueName;
  String senderQueueName;
  String host;
  String channel;
  int port;
  String queueMgr;
  String jmsType;

  @Override
  public String toString() {
    return "JMSConfigurationDomain{" +
        "system='" + system + '\'' +
        ", brokerUrl='" + brokerUrl + '\'' +
        ", userName='" + userName + '\'' +
        ", password='" + password + '\'' +
        ", receiverQueueName=" + receiverQueueName +
        ", senderQueueName='" + senderQueueName + '\'' +
        ", host='" + host + '\'' +
        ", channel='" + channel + '\'' +
        ", port=" + port +
        ", queueMgr='" + queueMgr + '\'' +
        ", jmsType='" + jmsType + '\'' +
        '}';
  }
}
