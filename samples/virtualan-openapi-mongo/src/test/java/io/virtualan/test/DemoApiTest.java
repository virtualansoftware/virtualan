package io.virtualan.test;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.openapitools.OpenAPI2SpringBoot;

@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {OpenAPI2SpringBoot.class})
public class DemoApiTest {
    
}