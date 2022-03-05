Feature: Test Pet API

#  Scenario: User calls service to READ a pet by its id
#    Given pet with an path param petId of 1000
#    And add content type with given header params
#      | contentType | application/json |
#    When a user get application/json in pets_petId resource on pet
#    Then the status code is 500
#    And verify across response includes following in the response
#      | code    | MISSING_MOCK_DATA                                   |
#      | message | Mock response was not added for the given parameter |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets         |
      | type           | Response      |
      | resource       | pets          |
      | httpStatusCode | 201           |
      | input          | INVALID_INPUT |
      | output         | ERROR         |
      | method         | POST          |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 400
    And verify across response includes following in the response
      | code | Check input Json for the "Mock Request Body", Correct the input/Json!!! |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets                                                                                                                                                                                                                                      |
      | type           | Response                                                                                                                                                                                                                                   |
      | resource       | pets                                                                                                                                                                                                                                       |
      | httpStatusCode | 201                                                                                                                                                                                                                                        |
      | input          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output         | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | method         | POST                                                                                                                                                                                                                                       |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets                                                                                                                                                                                                                                      |
      | type           | Response                                                                                                                                                                                                                                   |
      | resource       | pets                                                                                                                                                                                                                                       |
      | httpStatusCode | 201                                                                                                                                                                                                                                        |
      | input          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output         | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | method         | POST                                                                                                                                                                                                                                       |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 400
    And verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |

  Scenario: User calls service to CREATE and Create Pet
    Given create a pet with given input
      | category.id   | i~100         |
      | category.name | Fish-POST     |
      | id            | i~100         |
      | name          | GoldFish-POST |
      | photoUrls[0]  | /fish/        |
      | status        | available     |
      | tags[0].id    | i~100         |
      | tags[0].name  | Fish-POST     |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in pets resource on pet
    Then the status code is 201
    And verify across response includes following in the response
      | id   | 100           |
      | name | GoldFish-POST |

  Scenario: Setup a mock service for  Pet with READ API
    Given create Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                           |
      | type                     | Response                                                                                                                                                                                                                                |
      | resource                 | pets                                                                                                                                                                                                                                    |
      | httpStatusCode           | 200                                                                                                                                                                                                                                     |
      | output                   | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] } |
      | method                   | GET                                                                                                                                                                                                                                     |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                   |
      | availableParams[0].value | 110                                                                                                                                                                                                                                     |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for  Pet with READ API with QUERY Param
    Given create Pet Mock data for the with given input
      | url                              | /pets/findByTags                                                                                                                                                                                                                          |
      | type                             | Response                                                                                                                                                                                                                                  |
      | resource                         | pets                                                                                                                                                                                                                                      |
      | httpStatusCode                   | 200                                                                                                                                                                                                                                       |
      | output                           | {   "category": {     "id": 111,     "name": "Fish-GET-Q"   },   "id": 111,   "name": "Fish-GET-Q",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 111,       "name": "Fish-GET-Q"     }   ] } |
      | method                           | GET                                                                                                                                                                                                                                       |
      | availableParams[0].key           | tags                                                                                                                                                                                                                                      |
      | availableParams[0].value         | red=                                                                                                                                                                                                                                      |
      | availableParams[0].parameterType | QUERY_PARAM                                                                                                                                                                                                                               |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to READ a pet by its id
    Given A user perform a api action
    And add query by tags with given query params
      | tags | red= |
    And add content type with given header params
      | contentType | application/json |
    When a user get application/json in pets_findByTags resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id   | 111        |
      | name | Fish-GET-Q |

  Scenario: User calls service to READ a pet by its id
    Given pet with an path param petId of 110
    And add content type with given header params
      | contentType | application/json |
    When a user get application/json in pets_petId resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id   | 110          |
      | name | GoldFish-GET |

  Scenario: Setup a mock service for Pet with DELETE API
    Given create Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                                    |
      | type                     | Response                                                                                                                                                                                                                                         |
      | resource                 | pets                                                                                                                                                                                                                                             |
      | httpStatusCode           | 200                                                                                                                                                                                                                                              |
      | output                   | {   "category": {     "id": 120,     "name": "Fish-DELETE"   },   "id": 120,   "name": "GoldFish-DELETE",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 120,       "name": "Fish-DELETE"     }   ] } |
      | method                   | DELETE                                                                                                                                                                                                                                           |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                            |
      | availableParams[0].value | 120                                                                                                                                                                                                                                              |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to DELETE a pet by its id
    Given pet with an path param petId of 120
    And add content type with given header params
      | contentType | application/json |
    When a user delete application/json in pets_petId resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id   | 120             |
      | name | GoldFish-DELETE |

  Scenario: Setup a mock service for  Pet with PUT API
    Given create PUT Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                           |
      | type                     | Response                                                                                                                                                                                                                                |
      | resource                 | pets                                                                                                                                                                                                                                    |
      | httpStatusCode           | 200                                                                                                                                                                                                                                     |
      | input                    | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] } |
      | output                   | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] } |
      | method                   | PUT                                                                                                                                                                                                                                     |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                   |
      | availableParams[0].value | 130                                                                                                                                                                                                                                     |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to PUT and Create Pet
    Given pet with an path param petId of 130
    And update with mock data with given input
      | category.id   | i~130        |
      | category.name | Fish-PUT     |
      | id            | i~130        |
      | name          | GoldFish-PUT |
      | photoUrls[0]  | /fish/       |
      | status        | available    |
      | tags[0].id    | i~130        |
      | tags[0].name  | Fish-PUT     |
    And add content type with given header params
      | contentType | application/json |
    When a user put application/json in pets_petId resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id   | 130          |
      | name | GoldFish-PUT |

  Scenario: Parameterized GET - validate username
    Given pet with an path param username of John
    And add content type with given header params
      | contentType | application/json |
    When a user get application/json in user_username resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id        | 111985 |
      | firstName | Biden  |

  Scenario: Parameterized PUT - PET
    Given pet with an path param petId of 6003
    And add content type with given header params
      | contentType | application/json |
    And update with mock data with given input
      | category.id   | i~130     |
      | category.name | None-PUT  |
      | id            | i~6003    |
      | name          | None-PUT  |
      | photoUrls[0]  | /fish/    |
      | status        | available |
      | tags[0].id    | i~130     |
      | tags[0].name  | Fish-PUT  |
    When a user put application/json in pets_petId resource on pet
    Then the status code is 200
    And verify across response includes following in the response
      | id   | 6003     |
      | name | None-PUT |

  Scenario: Parameterized POST - PET test
    Given create a pet with given input
      | category.id   | i~100     |
      | category.name | Fish-POST |
      | id            | i~105     |
      | name          | doggie    |
      | photoUrls[0]  | /fish/    |
      | status        | available |
      | tags[0].id    | i~100     |
      | tags[0].name  | Fish-POST |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in pets resource on pet
    Then the status code is 201
    And verify across response includes following in the response
      | id   | 105    |
      | name | doggie |

  Scenario: Setup a mock service for Pet with READ API with arraylist QUERY Param
    Given create Pet Mock data for the with given input
      | url                              | /pets/findByStatus                                                                                                                                                                                                                                                                    |
      | type                             | Response                                                                                                                                                                                                                                                                              |
      | resource                         | pets                                                                                                                                                                                                                                                                                  |
      | httpStatusCode                   | 200                                                                                                                                                                                                                                                                                   |
      | output                           | [   {     "category": {       "id": 0,       "name": "string"     },     "id": 0,     "name": "string",     "photoUrls": [       "string"     ],     "status": "available",     "tags": [       {         "id": 0,         "name": "string"       }     ],     "type": "string"   } ] |
      | method                           | GET                                                                                                                                                                                                                                                                                   |
      | availableParams[0].key           | status                                                                                                                                                                                                                                                                                |
      | availableParams[0].value         | available,sold                                                                                                                                                                                                                                                                        |
      | availableParams[0].parameterType | QUERY_PARAM                                                                                                                                                                                                                                                                           |
      | availableParams[1].key           | petIds                                                                                                                                                                                                                                                                                |
      | availableParams[1].value         | 111,123                                                                                                                                                                                                                                                                               |
      | availableParams[1].parameterType | QUERY_PARAM                                                                                                                                                                                                                                                                           |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to READ a pet by queryParams array
    Given A user perform a api action
    And add query by tags with given query params
      | status | available,sold |
      | petIds | 111,123        |
    And add content type with given header params
      | contentType | application/json |
    When a user get application/json in pets_findByStatus resource on pet
    Then the status code is 200
