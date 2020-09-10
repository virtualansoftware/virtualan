package io.virtualan.message.core.jms;

public class JMSConfigurationDomain {
  String system;
  String brokerUrl;
  String userName;
  String password;
  String receiverQueueName;
  String senderQueueName;

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getBrokerUrl() {
    return brokerUrl;
  }

  public void setBrokerUrl(String brokerUrl) {
    this.brokerUrl = brokerUrl;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getReceiverQueueName() {
    return receiverQueueName;
  }

  public void setReceiverQueueName(String receiverQueueName) {
    this.receiverQueueName = receiverQueueName;
  }

  public String getSenderQueueName() {
    return senderQueueName;
  }

  public void setSenderQueueName(String senderQueueName) {
    this.senderQueueName = senderQueueName;
  }
  
  @Override
  public String toString() {
    return "JMSConfigurationDomain{" +
        "system='" + system + '\'' +
        ", brokerUrl='" + brokerUrl + '\'' +
        ", userName='" + userName + '\'' +
        ", password='" + password + '\'' +
        ", receiverQueueName='" + receiverQueueName + '\'' +
        ", senderQueueName='" + senderQueueName + '\'' +
        '}';
  }
}
