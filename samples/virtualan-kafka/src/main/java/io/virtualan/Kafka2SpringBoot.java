package io.virtualan;

import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import io.virtualan.message.core.MessagingApplication;


@EnableKafka
@SpringBootApplication
@ComponentScan(basePackages = {"io.virtualan"})
@EmbeddedKafka(partitions = 1, controlledShutdown = false, brokerProperties = {
    "listeners=PLAINTEXT://localhost:9092", "port=9092",
    "log.dir=target/kafka-logs"})
public class Kafka2SpringBoot {

    @Autowired
    private Config config;

    public static void main(String[] args) {
        new SpringApplication(Kafka2SpringBoot.class).run(args);
    }


    @Autowired
    private EmbeddedKafkaBroker broker;


    @Configuration
    public static class Config {

        private int kafkaPort;

        private int zkPort;

        @Bean
        public EmbeddedKafkaBroker broker() throws IOException {
            ServerSocket ss = ServerSocketFactory.getDefault().createServerSocket(9092);
            this.kafkaPort = ss.getLocalPort();
            ss.close();

            EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, false) ;
            embeddedKafkaBroker.kafkaPorts(this.kafkaPort);
            embeddedKafkaBroker.brokerProperty("log.dir", "target/kafka-logs");
            return embeddedKafkaBroker;

        }

    }

}
