package uk.co.cheem.vcshistory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.ParseException;
import uk.co.cheem.vcshistory.config.GitHubConfig;
import uk.co.cheem.vcshistory.config.GitHubConfigParser;
import uk.co.cheem.vcshistory.controller.ControllerFactory;

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
    log.debug("Configuration: " + configuration.toString());

    val controller = ControllerFactory.fromConfig(configuration);
    controller.start();
    if (controller.isFailed()) {
      System.exit(1);
    }
  }

}
