Feature: Conversations

  Scenario: Submit a whole conversation
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    Given a standard answer
    And a POST request is sent for an answer
    And the notification scheduler runs
    Given a standard decision
    And a POST request is sent for a decision
    Given a standard decision for update
    And the update decision state is decision_issue_pending
    And a PUT request is sent for a decision
    And the notification scheduler runs
    Given a standard decision reply
    And the decision reply is ' "decision_rejected" '
    And a POST request is sent for a decision reply
    Given a standard decision reply
    And a POST request is sent for a decision reply
    When a GET request is sent for a conversation
    Then the response code is 200
    And the conversation response contains an online hearing
    And the conversation response contains an online hearing with at least 2 history entries
    And the conversation response contains an online hearing with the correct uri
    And the conversation response contains an online hearing with state desc of 'Continuous Online Hearing Decision Issued'
    And the conversation response contains an online hearing with 1 history entry  with state desc of 'Continuous Online Hearing Started'
    And the conversation response contains a decision
    And the conversation response contains a decision with the correct uri
    And the conversation response contains a decision with state desc of 'Decision Issued'
    And the conversation response contains a decision with 3 history entries
    And the conversation response contains a decision with 1 history entry with state desc of 'Decision Drafted'
    And the conversation response contains 2 decision replies
    And the conversation response contains a decision reply with the correct uri
    And the conversation response contains 1 question
    And the conversation response contains a question with the correct uri
    And the conversation response contains a question with state desc of 'Question Issued'
    And the conversation response contains a question with at least 3 history entries
    And the conversation response contains a question with 1 history entry with state desc of 'Question Drafted'
    And the conversation response contains 1 answer
    And the conversation response contains an answer with state desc of 'Answer Drafted'
    And the conversation response contains an answer with the correct uri
    And the conversation response contains an answer with 1 history entries
    And the conversation response contains an answer with 1 history entry with state desc of 'Answer Drafted'

  Scenario: Submit a conversation without decision
    Given a standard online hearing is created
    And a valid question
    And the put request is sent to issue the question round ' "1" '
    And the notification scheduler runs
    Given a standard answer
    And a POST request is sent for an answer
    And the notification scheduler runs
    When a GET request is sent for a conversation
    Then the response code is 200
    And the conversation response contains an online hearing
    And the conversation response contains an online hearing with 1 history entries
    And the conversation response contains an online hearing with the correct uri
    And the conversation response contains no decision
    And the conversation response contains 1 question
    And the conversation response contains a question with the correct uri
    And the conversation response contains a question with 3 history entries
    And the conversation response contains 1 answer
    And the conversation response contains an answer with the correct uri
    And the conversation response contains an answer with 1 history entries

  Scenario: Submit a conversation without question
    Given a standard online hearing is created
    When a GET request is sent for a conversation
    Then the response code is 200
    And the conversation response contains an online hearing
    And the conversation response contains an online hearing with 1 history entries
    And the conversation response contains an online hearing with the correct uri
    And the conversation response contains 0 question
