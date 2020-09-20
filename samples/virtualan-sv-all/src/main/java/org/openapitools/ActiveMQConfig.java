package org.openapitools;

import javax.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class ActiveMQConfig {

    @Bean
    @Order(1)
    public Queue queue() {
        return new ActiveMQQueue("virtualan.input");
    }

}