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
  "name": "the user chooses nodes 1 and 2",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theUserChoosesNodesAnd(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "nodes 1 and 2 exist",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndExist(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "nodes 1 and 2 are valid",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.nodesAndAreValid(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "a new node is created on the line between the two nodes",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the game has been extended by 1 line and 1 node",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.playGameSteps.theGameHasBeenExtendedByLineAndNode(java.lang.Integer,java.lang.Integer)"
});
formatter.result({
  "status": "passed"
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
  "status": "passed"
});
formatter.step({
  "name": "a new game is created",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aNewGameIsCreated()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "the game has 3 nodes and 0 lines drawn",
  "keyword": "And "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.theGameHasNodesAndLinesDrawn(int,int)"
});
formatter.result({
  "status": "passed"
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
  "name": "a message with the text \"You must start the game with at least 2 nodes\" is given to the user",
  "keyword": "Then "
});
formatter.match({
  "location": "acceptance_tests.c_driven.startGameSteps.aMessageWithTheTextIsGivenToTheUser(java.lang.String)"
});
formatter.result({
  "status": "passed"
});
});