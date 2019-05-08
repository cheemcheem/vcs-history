package uk.co.cheem.vcshistory.controller;

import uk.co.cheem.vcshistory.config.Config;

/**
 * The QueryController factory. Returns a config
 */
public class QueryControllerFactory {

  /**
   * Returns a GitHubQueryController from the config. Will return other controllers in future.
   *
   * @param configuration the configuration
   * @return the controller
   */
  public static QueryController fromConfig(Config configuration) {
    return new GitHubQueryController(configuration);
  }
}
