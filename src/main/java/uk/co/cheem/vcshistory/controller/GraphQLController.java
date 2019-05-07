package uk.co.cheem.vcshistory.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.cheem.vcshistory.config.GitHubConfig;
import uk.co.cheem.vcshistory.suppliers.GitHubQuery;
import uk.co.cheem.vcshistory.suppliers.GitHubQuery.QueryException;
import uk.co.cheem.vcshistory.vcsobjects.Repository;
import uk.co.cheem.vcshistory.view.GraphOutput;

@RequiredArgsConstructor
@Slf4j
class GraphQLController extends Controller {

  private final GitHubConfig configuration;
  private Repository repository;

  @Override
  protected void query() {
    final var gitHubQuery = new GitHubQuery(configuration);
    try {
      gitHubQuery.makeRequest();
      gitHubQuery.sendRequest();
      gitHubQuery.parseResponse();
    } catch (QueryException e) {
      log.error(e.getMessage().trim() + " Reason: " + e.getCause().getLocalizedMessage());
      this.failed = true;
    }
    this.repository = gitHubQuery.getRepository();
  }


  @Override
  protected void displayOutput() {
    new GraphOutput(repository).display();
  }

}
