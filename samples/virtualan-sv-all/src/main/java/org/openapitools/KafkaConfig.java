package org.openapitools;

import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import org.springframework.core.annotation.Order;

@EnableKafka
@Configuration
public class KafkaConfig {

    private int kafkaPort;

    @Autowired
    private EmbeddedKafkaBroker broker;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public EmbeddedKafkaBroker broker() throws IOException {
        ServerSocket ss = ServerSocketFactory.getDefault().createServerSocket(9092);
        this.kafkaPort = ss.getLocalPort();
        ss.close();

        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, false) ;
        embeddedKafkaBroker.kafkaPorts(this.kafkaPort);
        return embeddedKafkaBroker;

    }

}