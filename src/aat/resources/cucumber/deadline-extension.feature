Feature: Deadline Extension

  Scenario: Automatically accepting for the first extension request
    Given a standard online hearing is created
    And a standard question
    And the post request is sent to create the question
    And a standard question
    And the post request is sent to create the question
    When the put request is sent to issue the question round ' "1" '
    And deadline extension is requested
    Then the response code is 200
    And question states are question_deadline_extension_granted
    And question deadline extension count is 1
    And question history has at least 2 events
    And an event has been queued for this online hearing of event type question_deadline_extension_granted

  Scenario: Cannot extend deadline without questions
    Given a standard online hearing is created
    And deadline extension is requested
    Then the response code is 424

  Scenario: Cannot extend deadline for questions in question_issue_pending state
    Given a standard online hearing is created
    And a standard question
    And the post request is sent to create the question
    When deadline extension is requested
    Then the response code is 424
    And the response message is 'No questions to extend deadline for'

  Scenario: Extending already extended deadline is denied
    Given a standard online hearing is created
    And a standard question
    And the post request is sent to create the question
    And a standard question
    And the post request is sent to create the question
    When the put request is sent to issue the question round ' "1" '
    And deadline extension is requested
    And deadline extension is requested again
    Then the response code is 200
    And question states are question_deadline_extension_denied
    And question history has at least 3 events
