package uk.co.cheem.vcshistory.view;

import java.awt.HeadlessException;
import java.awt.Taskbar;
import java.util.Objects;
import javax.swing.ImageIcon;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.ThreadingModel;
import uk.co.cheem.vcshistory.vcsobjects.Repository;
import uk.co.cheem.vcshistory.view.objects.CommitViewNode;

/**
 * The type Graph output.
 */
@Slf4j
@RequiredArgsConstructor
public class GraphOutput {

  private final Repository repository;
  @Getter
  private boolean failed;
  private MultiGraph graph;
  private GraphFrame frame;

  /**
   * Display.
   */
  public void display() {

    log.debug(repository.toString());

    setUpView();
    if (isFailed()) {
      return;
    }

    setUpGraph();
    if (isFailed()) {
      return;
    }

    log.info("Displaying.");
    frame.setVisible(true);
  }

  private void setUpGraph() {
    log.info("Setting up graph.");
    setUpGraph(CommitViewNode.fromRepository(repository), null);
  }

  private void setUpGraph(@NonNull CommitViewNode node, Node parentGraphNode) {
    var graphNode = graph.getNode(node.getOid() + " " + node.getBranchName());
    if (graphNode == null) {
      graphNode = graph.addNode(node.getOid() + " " + node.getBranchName());
    }
    graphNode.addAttribute("ui.style",
        "size: 20px, 20px; fill-color: #" + node.getColour() + "; text-size: 20px;");

    val y = parentGraphNode == null ? 0 : Toolkit.nodePosition(parentGraphNode)[1] - 10;
    graphNode.addAttribute("y", y);
    graphNode.addAttribute("x", -200 + node.getBranchPrecedence() * 2);

    if (parentGraphNode == null || !parentGraphNode.getId().endsWith(node.getBranchName())) {
      graphNode.addAttribute("label", node.getBranchName() + " " + node.getOid().substring(0, 6));
    } else {
      graphNode.addAttribute("label", node.getOid().substring(0, 6));
    }
    if (parentGraphNode != null) {
      var edge = graph.getEdge(parentGraphNode.getId() + graphNode.getId());
      if (edge == null) {
        edge = graph
            .addEdge(parentGraphNode.getId() + graphNode.getId(), parentGraphNode, graphNode, true);
      }
      edge.addAttribute("ui.style",
          "fill-color: #" + node.getColour() + "; text-size: 20px; shape: cubic-curve; size: 3px;");

    }
    for (CommitViewNode child : node.getChildren()) {
      setUpGraph(child, graphNode);
    }
  }

  private void setUpView() {
    System.setProperty("org.graphstream.ui.renderer",
        "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    graph = new MultiGraph("VCS");
    val viewer = new Viewer(graph, ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    viewer.disableAutoLayout();
    val view = viewer.addDefaultView(false);

    final var resource = ClassLoader.getSystemClassLoader().getResource("icon.png");
    val imageIcon = new ImageIcon(Objects.requireNonNull(resource));
    Taskbar.getTaskbar().setIconImage(imageIcon.getImage());

    try {
      frame = new GraphFrame();
    } catch (HeadlessException e) {
      log.error("Unable to create window. Try running with a head.");
      log.debug("Headless Exception when creating window.", e);
      failed = true;
    }

    frame.add(view);

  }

}
