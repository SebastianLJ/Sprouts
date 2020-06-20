$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("file:use_cases/newFeatures/dynamicGame.feature");
formatter.feature({
  "name": "",
  "description": "  Description:\n  Actor: User",
  "keyword": "Feature"
});
formatter.scenario({
  "name": "Game is valid and complete",
  "description": "",
  "keyword": "Scenario"
});
formatter.scenario({
  "name": "Initial number of nodes exceeded 99",
  "description": "",
  "keyword": "Scenario"
});
formatter.step({
  "name": "all",
  "keyword": "Given "
});
formatter.match({
  "location": "acceptance_tests.SmartGame.all()"
});
formatter.result({
  "error_message": "java.lang.NullPointerException\n\tat java.base/java.io.File.\u003cinit\u003e(File.java:276)\n\tat Controller.FileSimulationController.validateFile(FileSimulationController.java:98)\n\tat acceptance_tests.SmartGame.all(SmartGame.java:77)\n\tat ✽.all(file:///Users/birkberger/Documents/IntelliJProjects/Sprouts/use_cases/newFeatures/dynamicGame.feature:16)\n",
  "status": "failed"
});
});