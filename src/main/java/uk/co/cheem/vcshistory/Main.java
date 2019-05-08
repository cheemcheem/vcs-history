package uk.co.cheem.vcshistory;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.ParseException;
import uk.co.cheem.vcshistory.config.CommandLineConfigParser;
import uk.co.cheem.vcshistory.config.Config;
import uk.co.cheem.vcshistory.controller.QueryControllerFactory;

/**
 * The type Main.
 */
@Slf4j
public class Main {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    // Parse arguments into a configuration object
    final var commandLineOptionParser = new CommandLineConfigParser(args);
    final Config configuration;
    try {
      configuration = commandLineOptionParser.createConfiguration();
    } catch (ParseException e) {
      log.error("Invalid arguments. " + e.getMessage());
      System.exit(1);
      return;
    }
    log.debug("Configuration: " + configuration.toString());

    val controller = QueryControllerFactory.fromConfig(configuration);
    controller.start();
    if (controller.isFailed()) {
      System.exit(1);
    }
  }

}
