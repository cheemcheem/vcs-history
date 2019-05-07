package uk.co.cheem.vcshistory.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class Controller {

  @Getter
  protected boolean failed;

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

  protected abstract void query();

  protected abstract void displayOutput();
}
