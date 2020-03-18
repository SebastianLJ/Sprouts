package acceptance_tests;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(features = "use_cases",
        plugin = { "html:target/cucumber/wikipedia.html"},
        monochrome=true,
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        glue = { "acceptance_tests"},
        strict = true)
public class Acceptance_test {

}
