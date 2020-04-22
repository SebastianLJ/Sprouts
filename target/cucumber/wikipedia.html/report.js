$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("file:use_cases/c_driven/draw_a_line.feature");
formatter.feature({
  "name": "Draw a line between two nodes",
  "description": "  Description: When the user inputs 2 node names, a line is drawn between those nodes\n  Actor: User",
  "keyword": "Feature"
});
formatter.scenario({
  "name": "Choose two existing nodes",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that a game has nodes 1 and 2",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.thatAGameHasNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user chooses nodes 1 and 2",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(playGameSteps.java:58)\n\tat ✽.the user chooses nodes 1 and 2(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/draw_a_line.feature:7)\n",
  "status": "failed"
});
formatter.step({
  "name": "nodes 1 and 2 exist",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndExist(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "nodes 1 and 2 are valid",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndAreValid(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "a new node is created on the line between the two nodes",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes()"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "the game has been extended by 1 line(s) and 1 node(s)",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineSAndNodeS(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.scenario({
  "name": "Connect node to itself",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that a game has nodes 1 and 2",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.thatAGameHasNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user chooses nodes 1 and 1",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(playGameSteps.java:58)\n\tat ✽.the user chooses nodes 1 and 1(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/draw_a_line.feature:15)\n",
  "status": "failed"
});
formatter.step({
  "name": "a new node is created on the circle between the two nodes",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.aNewNodeIsCreatedOnTheCircleBetweenTheTwoNodes()"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "the game has been extended by 1 line(s) and 1 node(s)",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineSAndNodeS(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.scenario({
  "name": "Choose one existing and one non-existing node",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that a game has nodes 1 and 2",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.thatAGameHasNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user chooses nodes 1 and 3",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(playGameSteps.java:58)\n\tat ✽.the user chooses nodes 1 and 3(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/draw_a_line.feature:21)\n",
  "status": "failed"
});
formatter.step({
  "name": "the game has been extended by 0 line(s) and 0 node(s)",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineSAndNodeS(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "a message with the text \"One or both nodes does not exist\" is given to the user",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aMessageWithTheTextIsGivenToTheUser(java.lang.String)"
});
formatter.result({
  "status": "skipped"
});
formatter.scenario({
  "name": "Choose one valid node and one invalid node",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that a game has nodes 1 and 2",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.thatAGameHasNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "node 1 is connected to node 1 and to node 2",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodeIsConnectedToNodeAndToNode(java.lang.Integer,java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user chooses nodes 1 and 2",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(playGameSteps.java:58)\n\tat ✽.the user chooses nodes 1 and 2(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/draw_a_line.feature:28)\n",
  "status": "failed"
});
formatter.step({
  "name": "the game has been extended by 0 line(s) and 0 node(s)",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineSAndNodeS(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "a message with the text \"Nodes cannot have more than 3 connecting edges\" is given to the user",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aMessageWithTheTextIsGivenToTheUser(java.lang.String)"
});
formatter.result({
  "status": "skipped"
});
formatter.scenario({
  "name": "Choose two existing nodes",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that a game has nodes 1 and 3",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.thatAGameHasNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user chooses nodes 1 and 3",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(playGameSteps.java:58)\n\tat ✽.the user chooses nodes 1 and 3(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/draw_a_line.feature:40)\n",
  "status": "failed"
});
formatter.step({
  "name": "nodes 1 and 3 exist",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndExist(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "nodes 1 and 3 are valid",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndAreValid(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "a new node is created on the line between the two nodes",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes()"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "the game has been extended by 1 line(s) and 1 node(s)",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineSAndNodeS(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "skipped"
});
formatter.uri("file:use_cases/c_driven/initialize_game.feature");
formatter.feature({
  "name": "Initialize game",
  "description": "  Description: When a new game is started, a number of nodes is initialized\n  Actor: User",
  "keyword": "Feature"
});
formatter.scenario({
  "name": "Start game with 3 nodes",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that no game is being played",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.thatNoGameIsBeingPlayed()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user inputs 3 initial nodes",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.theUserInputsInitialNodes(java.lang.Integer)"
});
formatter.result({
  "error_message": "java.lang.IllegalArgumentException: bound must be positive\n\tat java.base/java.util.Random.nextInt(Random.java:388)\n\tat Model.SproutModel.addRandomNodes(SproutModel.java:46)\n\tat Controller.SproutController.attemptInitializeGame(SproutController.java:33)\n\tat Controller.SproutLauncher.acceptUserInput(SproutLauncher.java:58)\n\tat acceptance_tests.c_driven.startGameSteps.theUserInputsInitialNodes(startGameSteps.java:35)\n\tat ✽.the user inputs 3 initial nodes(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/c_driven/initialize_game.feature:7)\n",
  "status": "failed"
});
formatter.step({
  "name": "a new game is created",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aNewGameIsCreated()"
});
formatter.result({
  "status": "skipped"
});
formatter.step({
  "name": "the game has 3 nodes and 0 lines drawn",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.theGameHasNodesAndLinesDrawn(int,int)"
});
formatter.result({
  "status": "skipped"
});
formatter.scenario({
  "name": "Start game with 0 nodes",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that no game is being played",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.thatNoGameIsBeingPlayed()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user inputs 0 initial nodes",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.theUserInputsInitialNodes(java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "no game is being played",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.noGameIsBeingPlayed()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "a message with the text \"You must start the game with at least 2 nodes and at most 99 nodes\" is given to the user",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aMessageWithTheTextIsGivenToTheUser(java.lang.String)"
});
formatter.result({
  "status": "passed"
});
formatter.scenario({
  "name": "Start game with 100 nodes",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "that no game is being played",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.thatNoGameIsBeingPlayed()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the user inputs 100 initial nodes",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.theUserInputsInitialNodes(java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "no game is being played",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.noGameIsBeingPlayed()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "a message with the text \"You must start the game with at least 2 nodes and at most 99 nodes\" is given to the user",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aMessageWithTheTextIsGivenToTheUser(java.lang.String)"
});
formatter.result({
  "status": "passed"
});
});