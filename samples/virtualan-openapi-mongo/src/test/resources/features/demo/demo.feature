Feature: Test Pet API
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
