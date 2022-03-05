package io.virtualan.core;

import io.cucumber.spring.CucumberContextConfiguration;
import io.virtualan.cucumblan.core.BaseStepDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.virtualan.SpringWsApplication;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = { SpringWsApplication.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class CucumberSpringConfiguration {

  @Autowired
  private BaseStepDefinition baseStepDefinition;

}