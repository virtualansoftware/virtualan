Feature: Test Soap API

  Scenario: Setup a mock service for Soap for POST
    Given set Soap Mock data for the following given input
      | url					| http://virtualan.io/types/helloworld	|
      | input               | <hel:person xmlns:hel="http://virtualan.io/types/helloworld"> <hel:firstName>John</hel:firstName> <hel:lastName>Mathew</hel:lastName> </hel:person>       |
      | output              | <ns2:greeting xmlns:ns2="http://virtualan.io/types/helloworld">             <ns2:greeting>Welcome John for the SOAP World!!!</ns2:greeting></ns2:greeting> |
      | operationId	        |   person                              |
      | method              |   person                              |
      | type                |   Response                            |
      | resource            |  http://virtualan.io/types/helloworld	|
      | contentType         |  XML                                  |
      | requestType         |  SOAP                                 |
    When tester create the mock data for Soap
    Then verify the status code is 201
    And verify mock response with "mockStatus" includes following in the response
      | code | Mock created successfully |
  Scenario: Setup a mock service for Soap for POST
    Given set Soap Mock data for the following given input
      | url					| http://virtualan.io/types/helloworld	|
      | input               | <hel:person xmlns:hel="http://virtualan.io/types/helloworld"> <hel:firstName>Suki</hel:firstName> <hel:lastName>Elan</hel:lastName> </hel:person>       |
      | output              | <ns2:greeting xmlns:ns2="http://virtualan.io/types/helloworld">             <ns2:greeting>Welcome SOAP World!!!</ns2:greeting></ns2:greeting> |
      | operationId	        |   person                              |
      | method              |   person                              |
      | type                |   Response                            |
      | resource            |  http://virtualan.io/types/helloworld	|
      | contentType         |  XML                                  |
      | requestType         |  SOAP                                 |
    When tester create the mock data for Soap
    Then verify the status code is 400
    And verify response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |
