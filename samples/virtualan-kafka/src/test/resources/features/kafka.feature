Feature: Test Kafka API
  Scenario: Setup a mock service for Kafka for POST 1
    Given Create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 101,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    When a user post application/json in virtualservices/message resource on virtualan
    Then Verify the status code is 201
    And Verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Kafka for POST 2
    Given Create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    When a user post application/json in virtualservices/message resource on virtualan
    Then Verify the status code is 201
    And Verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Kafka for POST 3
    Given Create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    When a user post application/json in virtualservices/message resource on virtualan
    Then Verify the status code is 400
    And Verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |

  Scenario: check produce and consume event validation 1
    Given Send inline message pets for event MOCK_REQUEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
    And Pause message PROCESSING for process for 2000 milliseconds
    When Verify-by-elements for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id            | i~101           |
      | category.name | german shepherd |
    Then Verify for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~101,Rocky,i~100:german shepherd,available |
    And Verify for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~101,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |

  Scenario: check produce and consume event validation 2
    Given Send inline message pets for event MOCK_REQUEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
    When Verify-by-elements for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id            | i~102           |
      | category.name | german shepherd |
    Then Verify for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~102,Rocky,i~100:german shepherd,available |
    And Verify for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~102,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |