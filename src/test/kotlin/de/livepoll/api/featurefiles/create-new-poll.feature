Feature: the version can be retrieved
  Scenario: client makes call to GET /version
    When a
    And the client receives server version 1.0
