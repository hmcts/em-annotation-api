Feature: Online hearing filter feature

  Background:
    Given a standard online hearing is created
    And the response code is 201

  Scenario: Search for non-existent online hearing
  When a get request is sent to ' "/continuous-online-hearings?case_id=foo"' for the online hearing
    Then the response code is 200
    And the response contains 0 online hearings

  Scenario: Search for an online hearing by case id
    When a get request is sent to ' "/continuous-online-hearings?case_id=case_123"' for the online hearing
    Then the response code is 200
    And the response contains 1 online hearings
    And the response contains online hearing with case 'case_123'

  Scenario: Search for multiple online hearings by case id
    And a standard online hearing
    And the case id is 'case_456'
    And a POST request is sent for online hearing
    When a get request is sent to ' "/continuous-online-hearings?case_id=case_123&case_id=case_456"' for the online hearing
    And the response contains 2 online hearings
    And the response contains online hearing with case 'case_123'
    And the response contains online hearing with case 'case_456'

  Scenario: Search for non-existent state
    And a get request is sent to ' "/continuous-online-hearings?case_id=case_123"' for the online hearing
    And the response contains 1 online hearings
    When a get request is sent to ' "/continuous-online-hearings?case_id=case_123&state=foo"' for the online hearing
    Then the response contains 0 online hearings

  Scenario: Search for online hearing by case id and state
    When a get request is sent to ' "/continuous-online-hearings?case_id=case_123&state=continuous_online_hearing_started"' for the online hearing
    Then the response contains 1 online hearings
    And the response contains online hearing with case 'case_123'
