package org.openapitools.configuration;

import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Autowired
    private EmbeddedKafkaBroker broker;


    private int kafkaPort;


    @Bean
    public EmbeddedKafkaBroker broker() throws IOException {
        ServerSocket ss = ServerSocketFactory.getDefault().createServerSocket(9092);
        this.kafkaPort = ss.getLocalPort();
        ss.close();

        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, false) ;
        embeddedKafkaBroker.kafkaPorts(this.kafkaPort);
        return embeddedKafkaBroker;

    }

}