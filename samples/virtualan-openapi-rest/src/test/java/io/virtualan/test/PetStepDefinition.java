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


public class PetStepDefinition extends PetApiTest {

		private Response response;
		private ValidatableResponse json;
		private RequestSpecification request;
		VirtualServiceRequest virtualServiceRequest = null;

		private String PET_BY_ID = "http://localhost:8800/api/pets/{id}";
		private String PET_URL = "http://localhost:8800/api/pets";
		private String VIRTUAL_SERVICE = "http://localhost:8800/virtualservices";

		@LocalServerPort
		int randomServerPort;

		Map<String, String>  urlMap = loadUrl();

		public Map loadUrl() {
			Map<String, String>  urlMapBuild = new HashMap();
			urlMapBuild.put("pet", "http://localhost:8800/api/pets");
			urlMapBuild.put("risk", "http://localhost:8800/api/riskfactor/compute");
			urlMapBuild.put("petId", "http://localhost:8800/api/pets/{id}");
			urlMapBuild.put("person", "http://localhost:8800/api/persons");
			urlMapBuild.put("personId", "http://localhost:8800/api/persons/{id}");
			return urlMapBuild;
		}

		@Given("a (.*) exists with an id of (.*)")
		public void petExistsById(String resource, String id) {
			request = given().port(80).pathParam("id", id);
		}

		@When("a user GET the (.*) by id")
		public void retrievesById(String serviceUrl) {
			response = request.when().accept("application/json").get(urlMap.get(serviceUrl));
		}

		@Given("update a pet with given a pet id (\\d+) with input$")
		public void updatePetData(int petId, Map<String, String> petMap)  {
			String json = petMap.get("input");
			request = given().contentType("application/json").port(80).pathParam("id", petId).body(json);
		}

		@Given("create a (.*) with given input$")
		public void createPetData(String resource, Map<String, String> petMap)  {
			String json = petMap.get("input");
			request = given().contentType("application/json").port(80).body(json);
		}

		@When("a user POST the (.*) with id")
		public void createServiceById(String serviceUrl) {
			response = request.when().accept("application/json").post(urlMap.get(serviceUrl));
		}

		@When("a user PUT the (.*) with id")
		public void updatePetById(String serviceUrl) {
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

		@Given("set Pet Mock data for the following given input")
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

		}

		@And ("set available parameters for the following given input$")
		public void setUpMockDataWithParam(DataTable data) {
			if(data != null && data.asList(VirtualServiceKeyValue.class) != null) {
				virtualServiceRequest.setAvailableParams(data.asList(VirtualServiceKeyValue.class));
			}
			request = given().port(80).contentType("application/json").body(virtualServiceRequest);
		}

		@When("tester create the mock data for Pet")
		public void creatMockRequest() {
			response = request.when().post(VIRTUAL_SERVICE);
			System.out.println(response.getBody().prettyPrint());
		}

	}