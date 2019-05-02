package uk.co.cheem.vcshistory.config;


import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.apache.commons.cli.MissingOptionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GitHubConfigParserTest {

  @Test
  void handlesOwnerNameParameter() {
    val ownerName = "owner";
    val args = new String[]{"-r", "", "-o", ownerName, "-t", ""};
    val gitHubConfigParser = new GitHubConfigParser(args);
    val configuration = Assertions.assertDoesNotThrow(gitHubConfigParser::createConfiguration);
    assertEquals(configuration.getOwnerName(), ownerName);
  }

  @Test
  void handlesRepoNameParameter() {
    val repoName = "repo";
    val args = new String[]{"-r", repoName, "-o", "", "-t", ""};
    val gitHubConfigParser = new GitHubConfigParser(args);
    val configuration = Assertions.assertDoesNotThrow(gitHubConfigParser::createConfiguration);
    assertEquals(configuration.getRepoName(), repoName);
  }

  @Test
  void handlesTokenParameter() {
    val token = "token";
    val args = new String[]{"-r", "", "-o", "", "-t", token};
    val gitHubConfigParser = new GitHubConfigParser(args);
    val configuration = Assertions.assertDoesNotThrow(gitHubConfigParser::createConfiguration);
    assertEquals(configuration.getToken(), token);
  }

  @Test
  void handlesAllParameters() {
    val ownerName = "owner";
    val repoName = "repo";
    val token = "token";
    val args = new String[]{"-r", repoName, "-o", ownerName, "-t", token};
    val gitHubConfigParser = new GitHubConfigParser(args);
    val configuration = Assertions.assertDoesNotThrow(gitHubConfigParser::createConfiguration);
    assertEquals(configuration.getOwnerName(), ownerName);
    assertEquals(configuration.getRepoName(), repoName);
    assertEquals(configuration.getToken(), token);
  }

  @Test
  void handlesNoParameters() {
    val args = new String[]{};
    val gitHubConfigParser = new GitHubConfigParser(args);
    val missingOptionException = Assertions.assertThrows(
        MissingOptionException.class,
        gitHubConfigParser::createConfiguration
    );
  }

  @Test
  void handlesMissingOwnerNameParameter() {
    val repoName = "repo";
    val token = "token";
    var args = new String[]{"-r", repoName, "-t", token};
    var gitHubConfigParser = new GitHubConfigParser(args);
    Assertions.assertThrows(
        MissingOptionException.class,
        gitHubConfigParser::createConfiguration
    );
  }
  @Test
  void handlesMissingRepoNameParameter() {
    val ownerName = "owner";
    val token = "token";
    var args = new String[]{"-o", ownerName, "-t", token};
    var gitHubConfigParser = new GitHubConfigParser(args);
    Assertions.assertThrows(
        MissingOptionException.class,
        gitHubConfigParser::createConfiguration
    );
  }
  @Test
  void handlesMissingTokenParameter() {
    val ownerName = "owner";
    val repoName = "repo";
    var args = new String[]{"-r", repoName, "-o", ownerName};
    var gitHubConfigParser = new GitHubConfigParser(args);
    Assertions.assertThrows(
        MissingOptionException.class,
        gitHubConfigParser::createConfiguration
    );
  }
}