package io.virtualan;


import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import io.virtualan.idaithalam.core.api.VirtualanTestPlanExecutor;


@RunWith(SpringRunner.class)
@SpringBootTest
    (classes = {org.openapitools.OpenAPI2SpringBoot.class},webEnvironment = WebEnvironment.DEFINED_PORT)
public class BDDAPIE2EWithLowcodeIntegrationTest {

    @Before
    public void testBeforeClass(){
        System.out.println("----------------------------------------");
        System.out.println("--- Start Test -------");
        System.out.println("------------------------------------------");
    }

    @After
    public void testAfterClass(){
        System.out.println("------------------------------------------");
        System.out.println("---- END Test : ------");
        System.out.println("-------------------------------------------");
    }


    @SneakyThrows
    @Test
    public void executeEnd2EndTesting() throws InterruptedException {
        try {

            boolean isSuccess = VirtualanTestPlanExecutor.invoke("apiexecution.yaml");
            if (isSuccess) {
                Assert.assertTrue("Successfully executed all the integration testcases", true);
            } else {
                Assert.assertTrue("Integration testcases are Failed", false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue("Integration testcases are Failed > " + e.getMessage(), false);
        }
    }

}