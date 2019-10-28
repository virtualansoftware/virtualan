package io.virtualan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"io.virtualan"})
public class Kafka2SpringBoot {

    
    public static void main(String[] args) throws Exception {
        new SpringApplication(Kafka2SpringBoot.class).run(args);
    }

    
}
