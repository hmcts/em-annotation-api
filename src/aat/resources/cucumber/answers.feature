Feature: Answers

  Scenario: Submit an answer for unknown question
    Given a standard online hearing is created
    Given a standard answer
    When a POST request is sent for an answer
    Then the response code is 404

  Scenario: Submit a drafted answer for a question
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    When a POST request is sent for an answer
    Then the response code is 201
    And the response headers contains a location to the created entity
    And send get request to the location
    And the response code is 200
    And the notification scheduler runs
    And there is no event queued for this online hearing of event type answers_submitted
    When the get request is sent to retrieve the submitted question
    And the question state name is question_issued

  Scenario: Submit a "submitted" answer for a question
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    Then the response code is 201
    And the response headers contains a location to the created entity
    And send get request to the location
    And the response code is 200
    And the notification scheduler runs
    And an event has been queued for this online hearing of event type answers_submitted
    When the get request is sent to retrieve the submitted question
    And the question state name is question_answered

  Scenario: Retrieve an answer for a question
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And a POST request is sent for an answer
    And the response code is 201
    When a GET request is sent for an answer
    And the response code is 200
    And the answer response answer text is 'string'
    And the answer response answer state is 'answer_drafted'
    And the answer response answer state datetime is a valid ISO8601 date

  Scenario: Submit an answer with no answer text
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer text is empty
    When a POST request is sent for an answer
    Then the response code is 422

  Scenario: Update a drafted answer
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer text is 'foo'
    And a POST request is sent for an answer
    And the response code is 201
    And the answer text is 'bar'
    When a PUT request is sent for an answer
    Then the response code is 200
    And the answer text is 'bar'
    And there is no event queued for this online hearing of event type answers_submitted

  Scenario: Update an answer state
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer text is 'foo'
    And a POST request is sent for an answer
    And the response code is 201
    And the answer state is answer_submitted
    When a PUT request is sent for an answer
    Then the response code is 200
    And an event has been queued for this online hearing of event type answers_submitted

  Scenario: Try and update an answer state from submitted to draft
    Given a standard online hearing is created
    Given a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer text is 'foo'
    And a POST request is sent for an answer
    And the response code is 201
    And the answer state is answer_submitted
    When a PUT request is sent for an answer
    Then the response code is 200
    And the answer state is answer_drafted
    When a PUT request is sent for an answer
    Then the response code is 422

  Scenario: Update unknown answer
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And an unknown answer identifier
    And the answer text is 'foo'
    When a PUT request is sent for an answer
    Then the response code is 404

  Scenario: Attempt to create multiple answers
    Given a standard online hearing is created
    Given a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer text is 'foo'
    And a POST request is sent for an answer
    And the answer text is 'bar'
    When a POST request is sent for an answer
    And the response code is 409

  Scenario: Submit an answer for re-listed online hearing
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    Given a standard update online hearing request
    And the update online hearing state is continuous_online_hearing_relisted
    And the relist reason is 'reason'
    And a PUT request is sent for online hearing
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    Then the response code is 422

  Scenario: Submit an answer for an online hearing with a decision issued
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard decision
    When a POST request is sent for a decision
    Given a standard decision for update
    And the update decision state is decision_issue_pending
    And a PUT request is sent for a decision
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    Then the response code is 422
