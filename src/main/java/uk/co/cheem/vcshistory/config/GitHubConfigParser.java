package uk.co.cheem.vcshistory.config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@Slf4j
@Value
public class GitHubConfigParser {

  String[] args;
  private static final Options options = new Options();
  private static final CommandLineParser parser = new DefaultParser();

  static {
    options.addRequiredOption("t", "token", true, "A token with read permissions to the GitHub repo.");
    options.addRequiredOption("o", "owner", true, "GitHub username of the owner of the repo.");
    options.addRequiredOption("r", "repo", true, "GitHub repo name.");
  }

  public GitHubConfig createConfiguration() throws ParseException {
    log.info("Creating configuration from command line arguments..");
    val commandLine = parser.parse(options, args);

    val ownerName = commandLine.getOptionValue("o");
    val repoName = commandLine.getOptionValue("r");
    val token = commandLine.getOptionValue("t");

    val config = GitHubConfig.builder()
        .ownerName(ownerName)
        .repoName(repoName)
        .token(token)
        .build();

    log.info("Configuration created.");
    return config;
  }
}
