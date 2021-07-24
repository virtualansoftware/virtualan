package io.virtualan.cucumblan.core;

import io.cucumber.java.Before;
import io.virtualan.Kafka2SpringBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = Kafka2SpringBoot.class, loader = SpringBootContextLoader.class)
@io.cucumber.spring.CucumberContextConfiguration
public class CucumberContextConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(CucumberContextConfiguration.class);

  @Before
  public void setUp() {
    LOG.info("-------------- Spring Virtualan Kafka Context Initialized For Executing Cucumber Tests --------------");
  }

}