Feature: Event features

  Scenario: Update decision state to issued
    Given a standard online hearing is created
    Then the response code is 201
    And a standard decision
    And a POST request is sent for a decision
    And the response code is 201
    Given a standard decision for update
    And the update decision state is decision_issue_pending
    And a PUT request is sent for a decision
    When a GET request is sent for the saved online hearing
    Then the online hearing state is 'continuous_online_hearing_started'
    And  an event has been queued for this online hearing of event type decision_issued

  Scenario: Reset answer_submitted event state
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    And a POST request is sent for an answer
    And the response code is 201
    And an event has been queued for this online hearing of event type answers_submitted
    And the notification scheduler runs
    When the put request is sent to reset the events of type answers_submitted
    Then the response code is 200
    And the event has been set to event_forwarding_success of event type answers_submitted
    And the event type answers_submitted has been set to retries of 0

  Scenario: Reset question_issued event state when not failed
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And an event has been queued for this online hearing of event type question_round_issued
    When the put request is sent to reset the events of type question_round_issued
    Then the response code is 200
    And the event has been set to event_forwarding_success of event type question_round_issued

  Scenario: Reset question_issued event state when failed
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And an event has been queued for this online hearing of event type question_round_issued
    When the notification scheduler fails to send after configured retries for 'SSCS' and event type 'question_round_issued'
    When the put request is sent to reset the events of type question_round_issued
    Then the response code is 200
    And the event has been set to event_forwarding_pending of event type question_round_issued

  Scenario: Reset events for event type which does not exist
    Given a standard online hearing is created
    When the put request is sent to reset the events of type unknown_event_type
    Then the response code is 422

  Scenario: Reset events for where jurisdiction and event type registry does not exist
    Given a standard online hearing
    And a jurisdiction named ' "JUI_TEST", with id ' "5001" ' and max question rounds ' "2" ' is created
    And the online hearing jurisdiction is ' "JUI_TEST" '
    And the post request is sent to create the online hearing
    When the put request is sent to reset the events of type question_round_issued
    Then the response code is 424

  Scenario: Subscribe to a duplicate event
    Given a conflicting request to subscribe to question round issued
    When a POST request is sent to register
    Then the response code is 409

  Scenario: Subscribe to an event
    Given a standard event register request
    And jurisdiction ' "Long", with id ' "42" ' and max question rounds ' "5" ' is created
    When a POST request is sent to register
    Then the response code is 200

  Scenario: Update an event
    Given a standard event register request
    And jurisdiction ' "Long", with id ' "42" ' and max question rounds ' "5" ' is created
    When a POST request is sent to register
    Then the event register endpoint is 'http://localhost:8080/SSCS/notifications'
    Given a standard event register request
    And jurisdiction ' "Long", with id ' "42" ' and max question rounds ' "5" ' is created
    And the registration endpoint is 'http://foo.com'
    When a PUT request is sent to register
    Then the response code is 200
    And the event register endpoint is 'http://foo.com'

  Scenario: Subscribe to an event with invalid jurisdiction
    Given a standard event register request
    And an invalid '"jurisdiction"'
    When a POST request is sent to register
    Then the response code is 422

  Scenario: Subscribe to an event with invalid event type
    Given a standard event register request
    And an invalid '"eventType"'
    When a POST request is sent to register
    Then the response code is 422

  Scenario: Subscribe to an event with invalid url
    Given a standard event register request
    And an invalid '"url"'
    When a POST request is sent to register
    Then the response code is 422

  Scenario: Delete an event
    Given a standard event register request
    And jurisdiction ' "Long", with id ' "42" ' and max question rounds ' "5" ' is created
    When a POST request is sent to register
    And the event register is saved
    When a DELETE request is sent to register
    Then the response code is 200
    And the event register is deleted


