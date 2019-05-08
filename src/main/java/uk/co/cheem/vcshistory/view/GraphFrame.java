package uk.co.cheem.vcshistory.view;

import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import lombok.val;


@Slf4j
class GraphFrame extends JFrame {

  GraphFrame() throws HeadlessException {
    super("VCS History");
    setUp();
  }

  private void setUp() {
    log.info("Setting up frame.");
    val dimension = new Dimension(1280, 720);

    setSize(dimension);
    setMinimumSize(dimension);
    setLocationRelativeTo(null);
    setUndecorated(true);

  }
}
