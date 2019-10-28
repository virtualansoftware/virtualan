package io.virtualan.test;
 

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber; 

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty"},
		glue = {"io.virtualan.test"},
		features = {"classpath:features/pet"})
public class RunTest { 
	
}