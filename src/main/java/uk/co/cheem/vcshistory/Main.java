package uk.co.cheem.vcshistory;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import uk.co.cheem.vcshistory.config.GitHubConfig;
import uk.co.cheem.vcshistory.config.GitHubConfigParser;
import uk.co.cheem.vcshistory.suppliers.GitHubQuery;
import uk.co.cheem.vcshistory.view.GraphOutput;

@Slf4j
public class Main {

  public static void main(String[] args) {
    // Parse arguments into a configuration object
    final var commandLineOptionParser = new GitHubConfigParser(args);
    final GitHubConfig configuration;
    try {
      configuration = commandLineOptionParser.createConfiguration();
    } catch (ParseException e) {
      log.error("Invalid arguments. " + e.getMessage());
      System.exit(1);
      return;
    }
    log.debug(configuration.toString());

    // Make and send GraphQL Query
    final var gitHubSupplier = new GitHubQuery(configuration);
    gitHubSupplier.buildQuery();
    gitHubSupplier.sendQuery();
    final var response = gitHubSupplier.getResponse();
    log.debug("GraphQL response: " + response.toString());

    // Handle errors in the GraphQL communication
    if (response.getErrors() != null && response.getErrors().length != 0) {
      if (response.getErrors().length == 1) {
        log.error(
            "GraphQL error communicating with GitHub. " + response.getErrors()[0].getMessage()
        );
      } else {
        log.error(
            "GraphQL error communicating with GitHub. " + System.lineSeparator()
                + Arrays.toString(response.getErrors())
        );
      }
      log.error(
          "Try checking configuration was correctly parsed: '" + configuration.toString() + "'.");
      System.exit(1);
      return;
    }

    new GraphOutput(response.getResponse()).display();

  }

}
