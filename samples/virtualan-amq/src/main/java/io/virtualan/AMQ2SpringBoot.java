package io.virtualan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication (exclude = {KafkaAutoConfiguration.class})
@ComponentScan(basePackages = {"io.virtualan"})
public class AMQ2SpringBoot {


    public static void main(String[] args) {
        new SpringApplication(AMQ2SpringBoot.class).run(args);
    }


}
