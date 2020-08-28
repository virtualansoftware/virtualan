Feature: Test Kafka API

  Scenario: Setup a mock service for Kafka for POST
    Given set Kafka Mock data for the following given input
      | brokerUrl                       | localhost:9092        	|
      | input                           | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }      |
      | output                          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }              |
      | requestTopicOrQueueName	        |   virtualan.input         |
      | responseTopicOrQueueName        |   virtualan.output        |
      | type                            |   Response                |
      | resource                        |  virtualan.input      	|
      | requestType                     |  KAFKA                    |
    When tester create the mock data for Kafka
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |
  Scenario: Setup a mock service for Kafka for POST
    Given set Kafka Mock data for the following given input
      | brokerUrl                       | localhost:9092        	|
      | input                           | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }      |
      | output                          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }              |
      | requestTopicOrQueueName	        |   virtualan.input         |
      | responseTopicOrQueueName        |   virtualan.output        |
      | type                            |   Response                |
      | resource                        |  virtualan.input      	|
      | requestType                     |  KAFKA                    |
    When tester create the mock data for Kafka
    Then verify the status code is 400
    And verify response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |
