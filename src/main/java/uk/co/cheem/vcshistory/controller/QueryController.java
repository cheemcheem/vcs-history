package uk.co.cheem.vcshistory.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The type QueryController.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class QueryController {

  /**
   * The Failed.
   */
  @Getter
  protected boolean failed;

  /**
   * Start.
   */
  public void start() {
    log.info("Sending request.");
    this.query();
    if (failed) {
      log.error("Error occurred when sending request.");
      return;
    }
    log.info("Displaying output.");
    this.displayOutput();
  }

  /**
   * Query.
   */
  protected abstract void query();

  /**
   * Display output.
   */
  protected abstract void displayOutput();
}
