Feature: Test Soap API
  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url					| http://virtualan.io/types/helloworld	|
      | input               | <hel:person xmlns:hel="http://virtualan.io/types/helloworld"> <hel:firstName>John</hel:firstName> <hel:lastName>Mathew</hel:lastName> </hel:person>       |
      | output              | <ns2:greeting xmlns:ns2="http://virtualan.io/types/helloworld">             <ns2:greeting>Welcome John for the SOAP World!!!</ns2:greeting></ns2:greeting> |
      | operationId	        |   person                              |
      | method              |   person                              |
      | type                |   Response                            |
      | resource            |  http://virtualan.io/types/helloworld	|
      | contentType         |  XML                                  |
      | requestType         |  SOAP                                 |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 201
    And verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url					| http://virtualan.io/types/helloworld	|
      | input               | <hel:person xmlns:hel="http://virtualan.io/types/helloworld"> <hel:firstName>Mani</hel:firstName> <hel:lastName>Elan</hel:lastName> </hel:person>       |
      | output              | <ns2:greeting xmlns:ns2="http://virtualan.io/types/helloworld">             <ns2:greeting>Welcome SOAP World!!!</ns2:greeting></ns2:greeting> |
      | operationId	        |   person                              |
      | method              |   person                              |
      | type                |   Response                            |
      | resource            |  http://virtualan.io/types/helloworld	|
      | contentType         |  XML                                  |
      | requestType         |  SOAP                                 |
    And add content type with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then the status code is 400
    And verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |
