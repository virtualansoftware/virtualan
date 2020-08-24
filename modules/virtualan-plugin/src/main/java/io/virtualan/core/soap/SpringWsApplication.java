package io.virtualan.core.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class SpringWsApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringWsApplication.class, args);
  }
}
