package uk.co.cheem.vcshistory.controller;

import uk.co.cheem.vcshistory.config.GitHubConfig;

public class ControllerFactory {

  public static Controller fromConfig(GitHubConfig configuration) {
    return new GraphQLController(configuration);
  }
}
