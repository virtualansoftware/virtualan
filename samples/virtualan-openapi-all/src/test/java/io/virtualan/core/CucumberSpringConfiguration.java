package io.virtualan.core;

import io.cucumber.spring.CucumberContextConfiguration;
import io.virtualan.cucumblan.core.BaseStepDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.openapitools.OpenAPI2SpringBoot;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = { OpenAPI2SpringBoot.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class CucumberSpringConfiguration {

  @Autowired
  private BaseStepDefinition baseStepDefinition;

}