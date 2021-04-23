/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package io.virtualan.sv.mock.api;

import io.virtualan.sv.mock.to.Pet;
import io.swagger.annotations.*;
import io.virtualan.annotation.ApiVirtual;
import io.virtualan.annotation.VirtualService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-11T00:52:23.638-05:00[America/Chicago]")
@Validated
@Api(value = "pets", description = "the pets API")
@VirtualService
public interface PetsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /pets : Add a new pet to the store
     *
     * @param body Pet object that needs to be added to the store (required)
     * @return Invalid input (status code 405)
     */
    @ApiVirtual
    @ApiOperation(value = "Add a new pet to the store", nickname = "addPet", notes = "", authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 405, message = "Invalid input") })
    @PostMapping(
        value = "/pets",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> addPet(@ApiParam(value = "Pet object that needs to be added to the store" ,required=true )  @Valid @RequestBody Pet body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /pets/{petId} : Deletes a pet
     *
     * @param petId Pet id to delete (required)
     * @param apiKey  (optional)
     * @return Invalid pet value (status code 400)
     */
    @ApiVirtual
    @ApiOperation(value = "Deletes a pet", nickname = "deletePet", notes = "", authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 400, message = "Invalid pet value") })
    @DeleteMapping(
        value = "/pets/{petId}"
    )
    default ResponseEntity<Void> deletePet(@ApiParam(value = "Pet id to delete",required=true) @PathVariable("petId") Long petId,@ApiParam(value = "" ) @RequestHeader(value="api_key", required=false) String apiKey) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /pets/findByStatus : Finds Pets by status
     * Multiple status values can be provided with comma separated strings
     *
     * @param status Status values that need to be considered for filter (required)
     * @return successful operation (status code 200)
     *         or Invalid status value (status code 400)
     */
    @ApiVirtual
    @ApiOperation(value = "Finds Pets by status", nickname = "findPetsByStatus", notes = "Multiple status values can be provided with comma separated strings", response = Pet.class, responseContainer = "List", authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Pet.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid status value") })
    @GetMapping(
        value = "/pets/findByStatus",
        produces = { "application/json" }
    )
    default ResponseEntity<List<Pet>> findPetsByStatus(@NotNull @ApiParam(value = "Status values that need to be considered for filter", required = true, allowableValues = "available, pending, sold") @Valid @RequestParam(value = "status", required = true) List<String> status) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"name\", \"id\" : 6 }, \"type\" : \"DOGS\", \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /pets/findByTags : Finds Pets by tags
     * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
     *
     * @param tags Tags to filter by (required)
     * @return successful operation (status code 200)
     *         or Invalid tag value (status code 400)
     * @deprecated
     */
    @ApiVirtual
    @ApiOperation(value = "Finds Pets by tags", nickname = "findPetsByTags", notes = "Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.", response = Pet.class, responseContainer = "List", authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Pet.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid tag value") })
    @GetMapping(
        value = "/pets/findByTags",
        produces = { "application/json" }
    )
    default ResponseEntity<List<Pet>> findPetsByTags(@NotNull @ApiParam(value = "Tags to filter by", required = true) @Valid @RequestParam(value = "tags", required = true) List<String> tags) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"name\", \"id\" : 6 }, \"type\" : \"DOGS\", \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /pets/{petId} : Find pet by ID
     * Returns a single pet
     *
     * @param petId ID of pet to return (required)
     * @return successful operation (status code 200)
     *         or Invalid ID supplied (status code 400)
     *         or Pet not found (status code 404)
     */
    @ApiVirtual
    @ApiOperation(value = "Find pet by ID", nickname = "getPetById", notes = "Returns a single pet", response = Pet.class, authorizations = {
        
        @Authorization(value = "api_key")
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Pet.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Pet not found") })
    @GetMapping(
        value = "/pets/{petId}",
        produces = { "application/json" }
    )
    default ResponseEntity<Pet> getPetById(@ApiParam(value = "ID of pet to return",required=true) @PathVariable("petId") Long petId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"name\", \"id\" : 6 }, \"type\" : \"DOGS\", \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PATCH /pets/{petId} : patch an existing pet
     *
     * @param petId ID of pet to return (required)
     * @param body Pet object that needs to be added to the store (required)
     * @return successful operation (status code 200)
     *         or Invalid ID supplied (status code 400)
     *         or Pet not found (status code 404)
     *         or Validation exception (status code 405)
     */
    @ApiVirtual
    @ApiOperation(value = "patch an existing pet", nickname = "patchPet", notes = "", response = Pet.class, authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Pet.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Pet not found"),
        @ApiResponse(code = 405, message = "Validation exception") })
    @PatchMapping(
        value = "/pets/{petId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Pet> patchPet(@ApiParam(value = "ID of pet to return",required=true) @PathVariable("petId") Long petId,@ApiParam(value = "Pet object that needs to be added to the store" ,required=true )  @Valid @RequestBody Pet body) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"name\", \"id\" : 6 }, \"type\" : \"DOGS\", \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /pets/{petId} : Update an existing pet
     *
     * @param petId ID of pet to return (required)
     * @param body Pet object that needs to be added to the store (required)
     * @return successful operation (status code 200)
     *         or Invalid ID supplied (status code 400)
     *         or Pet not found (status code 404)
     *         or Validation exception (status code 405)
     */
    @ApiVirtual
    @ApiOperation(value = "Update an existing pet", nickname = "updatePet", notes = "", response = Pet.class, authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets") })
         }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Pet.class),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Pet not found"),
        @ApiResponse(code = 405, message = "Validation exception") })
    @PutMapping(
        value = "/pets/{petId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Pet> updatePet(@ApiParam(value = "ID of pet to return",required=true) @PathVariable("petId") Long petId,@ApiParam(value = "Pet object that needs to be added to the store" ,required=true )  @Valid @RequestBody Pet body) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"name\", \"id\" : 6 }, \"type\" : \"DOGS\", \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}