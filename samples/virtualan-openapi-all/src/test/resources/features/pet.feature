Feature: Test Pet API
  Scenario: User calls service to READ a pet by its id
    Given pet with an path param petId of 1000
    When a user get application/json in pets_petId resource on pet
    Then Verify the status code is 500
    And Verify across response includes following in the response
      | code			| MISSING_MOCK_DATA     |
      | message		| Mock response was not added for the given parameter |
  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given Create Pet Mock data for the with given input
      |url 				| 	/pets			|
      | type            | Response          |
      | resource        | pets              |
      |httpStatusCode	|	201				|
      |input 			| INVALID_INPUT 	|
      |output			| ERROR 			|
      |method			| POST 				|
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 400
    And Verify across response includes following in the response
      | code			|Check input Json for the "Mock Request Body", Correct the input/Json!!!     |
  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given Create Pet Mock data for the with given input
      | url             | /pets       |
      | type            | Response    |
      | resource        | pets        |
      | httpStatusCode | 201          |
      | input          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output         | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }|
      | method         | POST         |
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 201
    And Verify across response includes following in the response
      | mockStatus.code | Mock created successfully |
  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given Create Pet Mock data for the with given input
      | url             | /pets       |
      | type            | Response    |
      | resource        | pets        |
      | httpStatusCode  | 201         |
      | input           | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }|
      | method          | POST        |
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 400
    And Verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |
  Scenario: User calls service to CREATE and Create Pet
    Given Create a pet with given input
      | category.id		            | i~100 		|
      | category.name 	            | Fish-POST     |
      | id							| i~100			|
      | name 					 	| GoldFish-POST |
      |photoUrls[0]			        | /fish/ 		|
      |	status					    |available	    |
      |tags[0].id				    | i~100         |
      |tags[0].name			        | Fish-POST	    |
    When a user post accept application/json in pets resource on pet
    Then Verify the status code is 201
    And Verify across response includes following in the response
      | id		    | 100        	   |
      | name		| GoldFish-POST    |

  Scenario: Setup a mock service for  Pet with READ API
    Given Create Pet Mock data for the with given input
      | url                     | /pets/{petId} |
      | type                    | Response      |
      | resource                | pets          |
      | httpStatusCode          | 200           |
      | output                  | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] }|
      | method                  | GET           |
      | availableParams[0].key  | petId         |
      | availableParams[0].value| 110           |
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |
  Scenario: User calls service to READ a pet by its id
    Given pet with an path param petId of 110
    When a user get application/json in pets_petId resource on pet
    Then Verify the status code is 200
    And Verify across response includes following in the response
      | id		| 110     	   |
      | name	| GoldFish-GET |
  Scenario: Setup a mock service for Pet with DELETE API
    Given Create Pet Mock data for the with given input
      | url                     | /pets/{petId} |
      | type                    | Response      |
      | resource                | pets          |
      | httpStatusCode          | 200           |
      | output                  | {   "category": {     "id": 120,     "name": "Fish-DELETE"   },   "id": 120,   "name": "GoldFish-DELETE",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 120,       "name": "Fish-DELETE"     }   ] }|
      | method                  | DELETE           |
      | availableParams[0].key  | petId         |
      | availableParams[0].value| 120           |
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |
  Scenario: User calls service to DELETE a pet by its id
    Given pet with an path param petId of 120
    When a user delete application/json in pets_petId resource on pet
    Then Verify the status code is 200
    And Verify across response includes following in the response
      | id		| 120     	      |
      | name	| GoldFish-DELETE |

  Scenario: Setup a mock service for  Pet with PUT API
    Given Create PUT Pet Mock data for the with given input
      | url                     | /pets/{petId} |
      | type                    | Response      |
      | resource                | pets          |
      | httpStatusCode          | 200           |
      | input                  | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] }|
      | output                  | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] }|
      | method                  | PUT           |
      | availableParams[0].key  | petId         |
      | availableParams[0].value| 130           |
    When a user post accept application/json in virtualservices resource on virtualan
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |
  Scenario: User calls service to PUT and Create Pet
    Given pet with an path param petId of 130
    And Update with mock data with given input
      | category.id		            | i~130 		|
      | category.name 	            | Fish-PUT      |
      | id							| i~130			|
      | name 					 	| GoldFish-PUT  |
      | photoUrls[0]			    | /fish/ 		|
      |	status					    |available	    |
      |tags[0].id				    | i~130         |
      |tags[0].name			        | Fish-PUT	    |
    When a user update application/json in pets_petId resource on pet
    Then Verify the status code is 200
    And Verify across response includes following in the response
      | id		| 130     	   |
      | name	| GoldFish-PUT |
  Scenario: User calls service to validate username
    Given pet with an path param username of John
    When a user get application/json in user_username resource on pet
    Then Verify the status code is 200
    And Verify across response includes following in the response
      | id		    | 111985     	    |
      | firstName	| Biden         |
