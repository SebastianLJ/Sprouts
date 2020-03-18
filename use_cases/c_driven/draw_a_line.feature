Feature: Draw a line between two nodes
  Description: When the user inputs 2 node names, a line is drawn between those nodes
  Actor: User

  Scenario: Choose two existing nodes
    Given that a game has nodes 1 and 2
    And the user chooses nodes 1 and 2
    And nodes 1 and 2 exist
    And nodes 1 and 2 are valid
    Then a new node is created on the line between the two nodes
    And the game has been extended by 1 line(s) and 1 node(s)

  Scenario: Connect node to itself
    Given that a game has nodes 1 and 2
    And the user chooses nodes 1 and 1
    Then a new node is created on the circle between the two nodes
    And the game has been extended by 1 line(s) and 1 node(s)

  Scenario: Choose one existing and one non-existing node
    Given that a game has nodes 1 and 2
    And the user chooses nodes 1 and 3
    Then the game has been extended by 0 line(s) and 0 node(s)
    And a message with the text "One or both nodes chosen does not exist" is given to the user

  Scenario: Choose one valid node and one invalid node
    Given that a game has nodes 1 and 2
    And node 1 is connected to node 1 and to node 2
    And the user chooses nodes 1 and 2
    Then the game has been extended by 0 line(s) and 0 node(s)
    And a message with the text "Nodes cannot have more than 3 connecting edges" is given to the user

#  Scenario: Draw a line that collides with an existing line
#    Given that a user draws a line
#    And that line collides with an existing line
#    Then the game has been extended by 0 line(s) and 0 node(s)
#    And a message with the text "Collision with existing line" is given to the user

  Scenario: Choose two existing nodes
    Given that a game has nodes 1 and 3
    And the user chooses nodes 1 and 3
    And nodes 1 and 3 exist
    And nodes 1 and 3 are valid
    Then a new node is created on the line between the two nodes
    And the game has been extended by 1 line(s) and 1 node(s)
