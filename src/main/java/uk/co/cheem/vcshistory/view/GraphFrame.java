package uk.co.cheem.vcshistory.view;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.graphstream.ui.swingViewer.ViewPanel;


/**
 * The type Graph frame.
 */
@Slf4j
class GraphFrame extends JFrame {

  private final ViewPanel view;

  /**
   * Instantiates a new Graph frame.
   *
   * @throws HeadlessException the headless exception
   */
  GraphFrame(ViewPanel view) throws HeadlessException {
    super("VCS History");
    this.view = view;
    setUp();
  }

  private void setUp() {
    log.info("Setting up frame.");
    val dimension = new Dimension(1280, 720);

    setSize(dimension);
    setMinimumSize(dimension);
    setLocationRelativeTo(null);
    setUndecorated(true);

    addMouseWheelListener(new MouseWheelListener() {
      private final double maxZoom = 5f;
      private final double minZoom = 0.5f;
      private final double zoomSpeed = 0.1f;
      private double zoomLevel = view.getCamera().getViewPercent();

      private void checkZoomLevel(int zoom) {
        if (zoom > 0) {
          if (this.zoomLevel < this.maxZoom) {
            this.zoomLevel += (this.zoomLevel - this.minZoom) + zoom;
          } else {
            this.zoomLevel = this.maxZoom;
          }

        } else if (zoom < 0) {
          if (this.zoomLevel > this.minZoom) {
            this.zoomLevel = this.minZoom + (this.zoomLevel - this.minZoom)  + zoom;
          } else {
            this.zoomLevel = this.minZoom;
          }
        }
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        val scrolled = e.getWheelRotation();
        checkZoomLevel(scrolled);
        System.out.println("zoomLevel = " + zoomLevel);
        view.getCamera().setViewPercent(zoomLevel);
      }
    });
  }
}
