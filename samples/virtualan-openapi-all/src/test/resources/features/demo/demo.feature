Feature: Test Pet API
  Scenario: User calls service to get a pet by its id
    Given a pet exists with an id of 1000
    When a user GET the petId by id
    Then verify the status code is 500
    And verify response includes following in the response
      | code		  | MISSING_MOCK_DATA     |
      | message		| Mock response was not added for the given parameter	 |

  Scenario: Setup a mock service for Pet with POST call with "Mock Request Body" validation failure
    Given set Pet Mock data for the following given input
      | url					| /pets	             |
      | input                 | INVALID_INPUT      |
      | output                | ERROR              |
      | httpStatusCode	    |   201              |
      | method                |   POST             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
    When tester create the mock data for Pet
    Then verify the status code is 400
    And verify response includes following in the response
      | code			|Check input Json for the "Mock Request Body", Correct the input/Json!!!     |

  Scenario: Setup a mock service for Pet for POST
    Given set Pet Mock data for the following given input
      | url					| /pets	             |
      | input                 | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }      |
      | output                | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }              |
      | httpStatusCode	    |   201              |
      | method                |   POST             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: User calls service to POST and create Pet
    Given create a pet with given input
      | input                 | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }      |
    When a user POST the pet with id
    Then verify the status code is 201
    And verify response includes following in the response
      | id		| 100     	   |
      | name		| GoldFish-POST |

  Scenario: Setup a mock service for duplicate Pet with POST method
    Given set Pet Mock data for the following given input
      | url					| /pets	             |
      | input                 | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }      |
      | output                | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }              |
      | httpStatusCode	    |   201              |
      | method                |   POST             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
    When tester create the mock data for Pet
    Then verify the status code is 400
    And verify response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |

  Scenario: Setup a mock service for  Pet with GET API
    Given set Pet Mock data for the following given input
      | url					| /pets/110	             |
      | output                | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   GET             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |110    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: Setup a mock service for  Pet with GET API
    Given set Pet Mock data for the following given input
      | url					| /pets/120	             |
      | output                | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   GET             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |120    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: Setup a mock service for  Pet with GET API
    Given set Pet Mock data for the following given input
      | url					| /pets/130	             |
      | output                | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   GET             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |130    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: User calls service to GET a pet by its id
    Given a pet exists with an id of 110
    When a user GET the petId by id
    Then verify the status code is 200
    And verify response includes following in the response
      | id		| 110     	   |
      | name		| GoldFish-GET |

  Scenario: Setup a mock service for  Pet with DELETE API
    Given set Pet Mock data for the following given input
      | url					| /pets/120	             |
      | output                | {   "category": {     "id": 120,     "name": "Fish-DELETE"   },   "id": 120,   "name": "GoldFish-DELETE",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 120,       "name": "Fish-DELETE"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   DELETE             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |120    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: User calls service to DELETE a pet by its id
    Given a pet exists with an id of 120
    When a user DELETE the petId by id
    Then verify the status code is 200
    And verify response includes following in the response
      | id		| 120     	   |
      | name		| GoldFish-DELETE |

  Scenario: Setup a mock service for  Pet with PUT API
    Given set Pet Mock data for the following given input
      | url					| /pets/130	             |
      | input                 | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] }  |
      | output                | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   PUT             |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |130    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |
  Scenario: User calls service to PUT and create Pet
    Given update a pet with given a pet id 130 with input
      | input | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] }  |
    When a user PUT the petId with id
    Then verify the status code is 200
    And verify response includes following in the response
      | id		| 130     	   |
      | name		| GoldFish-PUT |

  Scenario: Setup a mock service for  Pet with PUT API
    Given set Pet Mock data for the following given input
      | url					| /pets/230	             |
      | input                 | {   "category": {     "id": 230,     "name": "Fish-PATCH"   },   "id": 230,   "name": "GoldFish-PATCH",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 230,       "name": "Fish-PATCH"     }   ] }  |
      | output                | {   "category": {     "id": 230,     "name": "Fish-PATCH"   },   "id": 230,   "name": "GoldFish-PATCH",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 230,       "name": "Fish-PATCH"     }   ] }  |
      | httpStatusCode	    |   200              |
      | method                |   PATCH           |
      | type                  |   Response        |
    And set available parameters for the following given input
      | key   | value | type|
      | petId |230    | java.lang.Long |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: User calls service to PATCH and create Pet
    Given update a pet with given a pet id 230 with input
      | input | {   "category": {     "id": 230,     "name": "Fish-PATCH"   },   "id": 230,   "name": "GoldFish-PATCH",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 230,       "name": "Fish-PATCH"     }   ] }  |
    When a user PATCH the petId with id
    Then verify the status code is 200
    And verify response includes following in the response
      | id		| 230     	   |
      | name		| GoldFish-PATCH |

  Scenario: Setup a mock service for sample with GET API
    Given set Pet Mock data for the following given input
      | url					| /sample/validate/{sampleId} |
      | output              |  { "id": 1, "sample1": "one","sample2": "two","sample3": "three"}                      |
      | httpStatusCode	    |   200              |
      | method              |   GET             |
    And set available parameters for the following given input
      | key       | value  | type|
      | sampleId | 1       | java.lang.String |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |


  Scenario: Setup a mock service for sample with GET API
    Given set Pet Mock data for the following given input
      | url					| /sample/validate/{sampleId} |
      | output              |  { "id": 2, "sample1": "one","sample2": "two","sample3": "three"}                      |
      | httpStatusCode	    |   200              |
      | method              |   GET             |
    And set available parameters for the following given input
      | key       | value  | type|
      | sampleId | 2       | java.lang.String |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: Setup a mock service for sample with GET API
    Given set Pet Mock data for the following given input
      | url					| /sample/validate/{sampleId} |
      | output              |  { "id": 3, "sample1": "one","sample2": "two","sample3": "three"}                      |
      | httpStatusCode	    |   200              |
      | method              |   GET             |
    And set available parameters for the following given input
      | key       | value  | type|
      | sampleId | 3       | java.lang.String |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: Setup a mock service for sample with GET API
    Given set Pet Mock data for the following given input
      | url					| /sample/validate/{sampleId} |
      | output              |  { "id": 4, "sample1": "one","sample2": "two","sample3": "three"}                      |
      | httpStatusCode	    |   200              |
      | method              |   GET             |
    And set available parameters for the following given input
      | key       | value  | type|
      | sampleId | 4       | java.lang.String |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: Setup a mock service for sample with GET API
    Given set Pet Mock data for the following given input
      | url					| /sample/validate/{sampleId} |
      | output              |  { "id": 5, "sample1": "one","sample2": "two","sample3": "three"}                      |
      | httpStatusCode	    |   200              |
      | method              |   GET             |
    And set available parameters for the following given input
      | key       | value  | type|
      | sampleId | 5       | java.lang.String |
    When tester create the mock data for Pet
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |

  Scenario: User check if high risk level for the give dateOfbirth and zipcode
    Given create a risk with given input
      | input                 | {   "birthday" : "1964-02-02",   "postalCode" : "1201" }|
    When a user POST the risk with id
    Then verify the status code is 200
    And verify risk response with 70 includes in the response

  Scenario: User check if low risk level for the give dateOfbirth and zipcode
    Given create a risk with given input
      | input                 | {   "birthday" : "2013-02-02",   "postalCode" : "60563" }|
    When a user POST the risk with id
    Then verify the status code is 200
    And verify risk response with 35 includes in the response
