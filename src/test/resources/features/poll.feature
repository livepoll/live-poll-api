Feature: polls endpoint

  Scenario: User fetches all polls
    Given I am logged in as test user
    Given I have no polls created yet
    When I create 2 new dummy polls
    And I retrieve my polls
    Then I get back exactly 2 polls

  Scenario: User fetches one specific poll
      Given I have created a poll named DummyPoll
      When I fetch my poll named DummyPoll
      Then I get back my poll named DummyPoll
