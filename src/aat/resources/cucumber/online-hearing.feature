Feature: Online hearing

  Background:
    Given a standard online hearing is created
    Then the response code is 201

  Scenario: Create online hearing
    And the response contains the following text '"online_hearing_id" '
    And the response contains the online hearing UUID
    And the response headers contains a location to the created entity
    And send get request to the location
    And the response code is 200

  Scenario: Create duplicate online hearing
    And a standard online hearing is created
    Then the response code is 409
    Then the response contains the following text '"Duplicate case found" '

  Scenario: Retrieve online hearing
    And the response contains the following text '"online_hearing_id" '
    And the response contains the online hearing UUID
    When a GET request is sent for the saved online hearing
    Then the response code is 200
    And the response contains the following text '"case_123" '

  Scenario: Update online hearing state to re-listed
    Given a standard update online hearing request
    And the update online hearing state is continuous_online_hearing_relisted
    And the relist reason is 'reason'
    And a PUT request is sent for online hearing
    Then the response code is 200
    And the response contains the following text '"Online hearing updated" '
    When a GET request is sent for the saved online hearing
    Then the response code is 200
    And the online hearing end date is not null
    And the online hearing reason is 'reason'
    And an event has been queued for this online hearing of event type continuous_online_hearing_relisted

  Scenario: Update online hearing that's already ended
    Given a standard update online hearing request
    And the update online hearing state is continuous_online_hearing_relisted
    And the relist reason is 'reason'
    And a PUT request is sent for online hearing
    Then the response code is 200
    And the response contains the following text '"Online hearing updated" '
    And a PUT request is sent for online hearing
    Then the response code is 409

  Scenario: Update with invalid online hearing state
    Given a standard update online hearing request
    And the update online hearing state is foo
    And a PUT request is sent for online hearing
    Then the response code is 422
    
  Scenario: Update with starting state
    Given a standard update online hearing request
    And the update online hearing state is continuous_online_hearing_started
    And a PUT request is sent for online hearing
    Then the response code is 409

  Scenario: Update non-existent online hearing
    And the update online hearing state is continuous_online_hearing_answers_sent
    And the request contains a random UUID
    And a PUT request is sent for online hearing
    Then the response code is 404
