package io.virtualan;


import io.virtualan.idaithalam.core.api.MassApiExecutor;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BDD_API_E2E_With_Lowcode_IntegrationTest {

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
            boolean isSuccess = MassApiExecutor.invoke("apiexecution.yaml");
            if (!isSuccess) {
                Assert.assertTrue("Integration testcases are Failed", false);
            }
            Assert.assertTrue("Successfully executed all the integration testcases",true);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue("Integration testcases are Failed > " + e.getMessage(), false);
        }
    }

}