Feature: Polls endpoint for a user

  Background:
    Given I am logged in as test user
    Given I have no polls created yet

  Scenario: User fetches all polls
    When I create 2 new dummy polls
    And I retrieve my polls
    Then I get back exactly 2 polls

  Scenario: User fetches one specific poll
    When I create a poll named "MySuperCoolPoll"
    And I retrieve my polls
    Then I get back exactly 1 polls
    And I get back a poll named "MySuperCoolPoll"
