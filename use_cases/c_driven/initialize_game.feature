Feature: Initialize game
  Description: When a new game is started, a number of nodes is initialized
  Actor: User

  Scenario: Start game with 3 nodes
    Given that no game is being played
    And the user inputs 3 initial nodes
    Then a new game is created
    And the game has 3 nodes and 0 lines drawn

  Scenario: Start game with 0 nodes
    Given that no game is being played
    And the user inputs 0 initial nodes
    Then no game is being played
#    Then a message with the text "You must start the game with at least 2 nodes" is given to the user