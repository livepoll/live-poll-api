Feature: User endpoint

  Background:
    Given A test user exists

  Scenario: User logs in and can only access data concerning his own user
    When I log in as test user
    Then My log in is confirmed
    And I am authorized to retrieve information about my own user
    And I am not authorized to retrieve information about a different user

    Scenario: User logs out and can't access any data anymore
      Given I am logged in as test user
      When I log myself out
      Then I can't access any data anymore
