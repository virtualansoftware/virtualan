package io.virtualan.test;


import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

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


public class PetStepDefinition extends PetApiTest {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;
	VirtualServiceRequest virtualServiceRequest = null;
	
	private String PET_BY_ID = "http://localhost:8080/pets/{id}";
	private String PET_URL = "http://localhost:8080/pets/";
	private String VIRTUAL_SERVICE = "http://localhost:8080/virtualservices";

	@Given("a pet exists with an id of (.*)")
	public void petExistsById(int id) {
		request = given().port(80).pathParam("id", id);
	}

	@When("a user GET the pet by id")
	public void retrievesById() {
		response = request.when().accept("application/json").get(PET_BY_ID);
	}
	
	@Given("update a pet with given a pet id (\\d+) with input$")
	public void updatePetData(int petId, Map<String, String> petMap)  {
		String json = petMap.get("input");
		request = given().contentType("application/json").port(80).pathParam("id", petId).body(json);
	}
	
	@Given("create a pet with given input$")
	public void createPetData(Map<String, String> petMap)  {
		String json = petMap.get("input");
		request = given().contentType("application/json").port(80).body(json);
	}
	
	@When("a user POST the pet with id")
	public void createPetById() {
		response = request.when().accept("application/json").post(PET_URL);
	}
	
	@When("a user PUT the pet with id")
	public void updatePetById() {
		response = request.when().accept("application/json").put(PET_BY_ID);
	}
	
	@When("a user DELETE the pet by id")
	public void deleteById() {
		response = request.when().accept("application/json").delete(PET_BY_ID);
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
