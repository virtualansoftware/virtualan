package io.virtualan.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * To run cucumber test
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
    glue = {"io.virtualan.cucumblan.core"},
    plugin = {"pretty",
        "io.virtualan.cucumblan.props.hook.FeatureScope",
        "json:target/cucumber-report.json",
        "html:target/cucumber-reports.html" })

public class KafkaMessageTest {

}
