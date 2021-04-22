package io.virtualan.cucumblan.core;

import io.cucumber.spring.CucumberContextConfiguration;
import io.virtualan.VirtualanOpenAPI2SpringBoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = {VirtualanOpenAPI2SpringBoot.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class CucumberSpringConfiguration {


}