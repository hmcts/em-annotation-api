Feature: Triggers

  Background: A question round has been issued
    Given a standard online hearing is created
    And a valid question
    When the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs

  Scenario: Trigger Question Deadline Elapsed
    Given the question expiry date has expired
    And the trigger scheduler runs
    When the get request is sent to retrieve the submitted question
    Then the question state name is question_deadline_elapsed

  Scenario: Trigger Question Deadline Elapsed For Extended Deadlines
    And deadline extension is requested
    When the get request is sent to retrieve the submitted question
    Then the question state name is question_deadline_extension_granted
    Given the question expiry date has expired
    And the trigger scheduler runs
    When the get request is sent to retrieve the submitted question
    Then the question state name is question_deadline_elapsed

  Scenario: Trigger Question Reminder For Deadlines
    And deadline extension is tomorrow
    And the trigger scheduler runs
    And an event has been queued for this online hearing of event type question_deadline_reminder
