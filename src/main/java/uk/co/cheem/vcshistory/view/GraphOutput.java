package uk.co.cheem.vcshistory.view;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import uk.co.cheem.vcshistory.vcsobjects.Repository;

@Slf4j
@Value
public class GraphOutput {
  Repository repository;

  public void display() {
    log.info(repository.toString());
  }

}
