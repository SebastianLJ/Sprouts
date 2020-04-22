Feature: Draw lines on a canvas
  Description: The user can draw legal lines on the canvas
  Actor: User

#  Scenario: Draw a line between 2 valid nodes => < 3 lines connected
#    Given that a game has nodes 1 and 2
#    When the user draws a line from node 1 to 2
#    Then the line can be seen on the canvas
#    And a new node is created on the line between the two nodes
#    And the game has been extended by one line and one node
#
#  Scenario: Draw a line that collides with another line => illegal line
#    Given that a game has nodes 1 and 2
#    When the user draws a line from node 1 to 2
#    And the line collides with another line
#    Then the line disappears from the canvas
#    And the game is unchanged
#
#  Scenario: Draw a line that collides with itself => illegal line
#    Given that a game has nodes 1 and 2
#    And that the user initializes a line in node 1
#    When the line collides with itself
#    Then the line disappears from the canvas
#    And the game is unchanged
#
#  Scenario: Draw a line to an invalid node => illegal line
#    Given that a game has nodes 1 and 2
#    When the user draws a line from node 1 to 2
#    And node 2 has 3 connecting lines
#    Then the line disappears from the canvas
#    And the game is unchanged
