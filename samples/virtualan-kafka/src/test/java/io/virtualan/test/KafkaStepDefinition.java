package io.virtualan.test;


import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.model.VirtualServiceKeyValue;
import io.virtualan.model.VirtualServiceRequest;
import org.springframework.boot.web.server.LocalServerPort;


public class KafkaStepDefinition extends KafkaTest {

		private Response response;
		private ValidatableResponse json;
		private RequestSpecification request;
		VirtualServiceRequest virtualServiceRequest = null;

		private String VIRTUAL_SERVICE = "http://localhost:8800/virtualservices/message";

		@LocalServerPort
		int randomServerPort;

		Map<String, String>  urlMap = loadUrl();

		public Map loadUrl() {
			Map<String, String>  urlMapBuild = new HashMap();
			return urlMapBuild;
		}

		@Given("a (.*) exists with an id of (.*)")
		public void petExistsById(String resource, int id) {
			request = given().port(80).pathParam("id", id);
		}

		@When("a user GET the (.*) by id")
		public void retrievesById(String serviceUrl) {
			response = request.when().accept("application/json").get(urlMap.get(serviceUrl));
		}

		@Given("update a pet with given a pet id (\\d+) with input$")
		public void updateKafkaData(int petId, Map<String, String> petMap)  {
			String json = petMap.get("input");
			request = given().contentType("application/json").port(80).pathParam("id", petId).body(json);
		}

		@Given("create a (.*) with given input$")
		public void createKafkaData(String resource, Map<String, String> petMap)  {
			String json = petMap.get("input");
			request = given().contentType("application/json").port(80).body(json);
		}

		@When("a user POST the (.*) with id")
		public void createServiceById(String serviceUrl) {
			response = request.when().accept("application/json").post(urlMap.get(serviceUrl));
		}

		@When("a user PUT the (.*) with id")
		public void updateKafkaById(String serviceUrl) {
			response = request.when().accept("application/json").put(urlMap.get(serviceUrl));
		}

		@When("a user DELETE the (.*) by id")
		public void deleteById(String serviceUrl) {
			response = request.when().accept("application/json").delete(urlMap.get(serviceUrl));
		}

		@Then("verify the status code is (\\d+)")
		public void verifyStatusCode(int statusCode) {
			json = response.then().statusCode(statusCode);
			System.out.println("RESPONE" + json.toString());
		}

		@And("^verify mock response with (.*) includes following in the response$")
		public void mockResponse(String context, DataTable data) throws Throwable {
			final Map<String, String> mockStatus = response.jsonPath().getMap(context);
			data.asMap(String.class, String.class).forEach((k, v) -> {
				assertEquals(v, mockStatus.get(k));
			});
		}

		@And("^verify (.*) response with (.*) includes in the response$")
		public void mockSingleResponse(String resource, String context) throws Throwable {
			assertEquals(context, json.extract().body().asString());
		}

		@And("^verify response includes following in the response$")
		public void verfiyResponse(DataTable data) throws Throwable {
			data.asMap(String.class, String.class).forEach((k, v) -> {
				System.out.println(v  + " : "+ json.extract().body().jsonPath().getString(k));
				assertEquals(v, json.extract().body().jsonPath().getString(k));
			});
		}

		@Given("set Kafka Mock data for the following given input")
		public void setUpMockData(Map<String, String> virtualServiceRequestInfo) {
			virtualServiceRequest = new VirtualServiceRequest();
			virtualServiceRequest.setResource(virtualServiceRequestInfo.get("resource"));
			virtualServiceRequest.setHttpStatusCode(virtualServiceRequestInfo.get("httpStatusCode"));
			virtualServiceRequest.setMethod(virtualServiceRequestInfo.get("method"));
			virtualServiceRequest.setInput(virtualServiceRequestInfo.get("input"));
			virtualServiceRequest.setOutput(virtualServiceRequestInfo.get("output"));
			virtualServiceRequest.setOperationId(virtualServiceRequestInfo.get("operationId"));
			virtualServiceRequest.setUrl(virtualServiceRequestInfo.get("url"));
			virtualServiceRequest.setType(virtualServiceRequestInfo.get("type"));
			virtualServiceRequest.setContentType(virtualServiceRequestInfo.get("contentType"));
			virtualServiceRequest.setType(virtualServiceRequestInfo.get("type"));
			virtualServiceRequest.setRequestType(virtualServiceRequestInfo.get("requestType"));
		}

		@And ("set available parameters for the following given input$")
		public void setUpMockDataWithParam(DataTable data) {
			if(data != null && data.asList(VirtualServiceKeyValue.class) != null) {
				virtualServiceRequest.setAvailableParams(data.asList(VirtualServiceKeyValue.class));
			}
			request = given().port(80).contentType("application/json").body(virtualServiceRequest);
		}

		@When("tester create the mock data for Kafka")
		public void creatMockRequest() {
			request = given().port(80).contentType("application/json").body(virtualServiceRequest);
			response = request.accept("application/json")
										.when().post(VIRTUAL_SERVICE);
			System.out.println(response.getBody().prettyPrint());
		}

	}