package io.virtualan;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT, classes = AMQ2SpringBoot.class)
public class TestExecution {

  @Test
  public void test() {
    ConnectionFactory connectionFactory =
        new ActiveMQConnectionFactory("vm://localhost");
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    jmsTemplate.setDefaultDestination(new ActiveMQQueue("virtualan.input_1"));
    String message = "{   \"category\": {     \"id\": 0,     \"name\": \"string\"   },   \"id\": 101,   \"name\": \"doggie\",   \"photoUrls\": [     \"string\"   ],   \"status\": \"available\",   \"tags\": [     {       \"id\": 0,       \"name\": \"string\"     }   ] }";
    jmsTemplate.convertAndSend(message);
    JmsTemplate jmsTemplateReceive = new JmsTemplate(connectionFactory);
    jmsTemplateReceive.setDefaultDestination(new ActiveMQQueue("virtualan.output"));
    jmsTemplateReceive.receiveAndConvert();
  }

}
