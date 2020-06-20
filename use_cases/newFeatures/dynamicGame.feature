#Feature:
#  Description:
#  Actor: User
#
#  # Success case
#  Scenario: Game is valid and complete
##    Given that the game is reset
##    And the game mode is smart
##    When the test file "completeGame.txt" is input in the file simulation
##    Then the file is a valid file
##    And the file runs uninterrupted
##    And the game response is "There are no more legal moves.\nPlayer 1 is the winner!"
#
#    # Fail case
#    Scenario: Initial number of nodes exceeded 99
#      Given all
##      Given that the game is reset
##      And the test file "exceedInit.txt" is input in the file simulation
##      And the file is a valid file
##      When the file moves are evaluated
##      And the file run is interrupted
##      And the tooltip response is "You must start the game with at least 2 nodes and at most 99 nodes"
###
##      # Fail case
##      Scenario: Initial number of nodes is less than 2
##
##        # Success case
##        Scenario: Game is valid but incomplete
##
##          # Fail case
##          Scenario: A node with 3 edges is chosen
##
##            # Fail case
##            Scenario: A non-existing node is chosen
##
##               # Fail case
##               Scenario: A text file with invalid format is input