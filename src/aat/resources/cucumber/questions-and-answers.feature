Feature: Question Round Get

  Scenario: Retrieve question round when all questions drafted
    Given a standard online hearing is created
    And a valid question
    When the get request is sent to get question round ' "1" '
    Then the question round has a state of 'question_drafted'
    And the question round has 1 question
    And all questions in the question round have a state of 'question_drafted'

  Scenario: Retrieve question round when all questions answered
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    Then the response code is 201
    And the notification scheduler runs
    When the get request is sent to get question round ' "1" '
    Then the question round has a state of 'questions_answered'
    And the question round has 1 question
    And all questions in the question round have a state of 'question_answered'
    And all questions in the question round have an answer

  Scenario: Retrieve question round when not all questions answered
    Given a standard online hearing is created
    And a valid question
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    Then the response code is 201
    And the notification scheduler runs
    When the get request is sent to get question round ' "1" '
    Then the question round has a state of 'question_issued'
    And the question round has 2 question
    And 1 question in the question round has a state of 'question_answered'
    And 1 question in the question round has a state of 'question_issued'
    And 1 questions in the question round has an answer

  Scenario: Retrieve all question rounds for issued rounds
    Given a standard online hearing is created
    And a standard question
    And the question round is ' "1" '
    And the post request is sent to create the question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard question
    And the question round is ' "2" '
    And the post request is sent to create the question
    And the put request is sent to issue the question round ' "2" '
    And the notification scheduler runs
    And the get request is sent to get all question rounds
    Then the response code is 200
    And all questions in the question rounds have a state of 'question_issued'

  Scenario: Retrieve all question rounds when all questions answered
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    And a standard answer
    And the answer state is answer_submitted
    When a POST request is sent for an answer
    And the notification scheduler runs
    And a standard question
    And the question round is ' "2" '
    And the post request is sent to create the question
    And the put request is sent to issue the question round ' "2" '
    And a standard answer
    And the answer state is answer_submitted
    And the notification scheduler runs
    And a POST request is sent for an answer
    When the get request is sent to get all question rounds
    And the number of questions rounds is ' "2" '
    And all questions in the question rounds have a state of 'question_answered'

