package uk.co.cheem.vcshistory.view;

import java.awt.HeadlessException;
import java.awt.Taskbar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.ThreadingModel;
import uk.co.cheem.vcshistory.vcsobjects.CommitEdge;
import uk.co.cheem.vcshistory.vcsobjects.CommitParentNode;
import uk.co.cheem.vcshistory.vcsobjects.Ref;
import uk.co.cheem.vcshistory.vcsobjects.Repository;

/**
 * The type Graph output.
 */
@Slf4j
@RequiredArgsConstructor
public class GraphOutput {

  private static final List<String> colours = List.of("EE4266", "00A896", "05668D", "3A7CA5");

  private final Repository repository;
  @Getter
  private boolean failed;
  private MultiGraph graph;
  private GraphFrame frame;

  /**
   * Display.
   */
  public void display() {
    log.info(repository.toString());

    setUpView();
    if (isFailed()) {
      return;
    }
    graph.display();

    setUpGraph();
    if (isFailed()) {
      return;
    }

    frame.setVisible(true);
  }

  private void setUpGraph() {
    val nodes = new HashMap<String, Node>();
    val refForNode = new HashMap<String, String>();
    val colours = new java.util.ArrayList<String>(GraphOutput.colours);
    val colourForRef = new HashMap<String, String>();
    val xForRef = new HashMap<String, Integer>();
    var currentX = 0;

    val refs = new HashMap<String, Integer>();
    val refLinks = new HashMap<String, String>();

    // set colours and x-axis for branches
    for (Ref currentRef : repository.getRefs().getNodes()) {
      refs.put(currentRef.getName(), 0);

      String currentColour;
      if (colours.isEmpty()) {
        currentColour = "000000";
      } else {
        currentColour = colours.get(0);
        colours.remove(0);
      }
      colourForRef.put(currentRef.getName(), currentColour);
      xForRef.put(currentRef.getName(), currentX);
      currentX -= 1;
    }

    // add nodes to graph and record oid->node->ref association
    for (Ref currentRef : repository.getRefs().getNodes()) {
      val edges = currentRef.getTarget().getHistory().getEdges();
      if (edges == null) {
        continue;
      }
      for (CommitEdge edge : edges) {
        val commitNode = edge.getNode();
        val oid = commitNode.getOid() + ">" + currentRef.getName();
        val node = graph.addNode(oid);
        nodes.put(oid, node);
        refForNode.put(oid, currentRef.getName());

        node.setAttribute("ui.label",
            commitNode.getOid().substring(0, 6) + " " + currentRef.getName());
        node.setAttribute("ui.style", "text-size: 20px;");
      }
    }

    // order refs
    for (Ref currentRef : repository.getRefs().getNodes()) {
      if (refs.get(currentRef.getName()) != 0) {
        continue;
      }
      val edges = currentRef.getTarget().getHistory().getEdges();
      if (edges == null) {
        continue;
      }
      for (CommitEdge edge : edges) {
        val commitNode = edge.getNode();
        val parents = commitNode.getParents().getNodes();
        if (parents.size() != 1) {
          continue;
        }
        nodes.keySet().stream()
            .filter(node -> node.startsWith(parents.get(0).getOid()))
            .map(refForNode::get)
            .filter(Predicate.not(currentRef.getName()::equals))
            .peek(s -> System.out.println(Map.of(currentRef.getName(), s)))
            .forEach(ref -> {
              refLinks.put(currentRef.getName(), ref);
//              refs.put(currentRef.getName(), 1);
            });
      }
    }

    System.out.println("refLinks = " + refLinks);
    while (refs.values().contains(0)) {
      for (Ref ref : repository.getRefs().getNodes()) {
        final var parentRef = refLinks.get(ref.getName());

        if (refs.get(parentRef) == null) {
          refs.put(ref.getName(), 1);
          continue;
        }

        if (refs.get(parentRef) != 0) {
          refs.put(parentRef, refs.get(parentRef) - 1);
        }
      }
    }

    // remove duplicate parent nodes from graph
    for (Ref currentRef : repository.getRefs().getNodes()) {
      val edges = currentRef.getTarget().getHistory().getEdges();
      if (edges == null) {
        continue;
      }
      for (CommitEdge edge : edges) {
        val commitNode = edge.getNode();
        val parents = commitNode.getParents().getNodes();
        val potentialParents = parents.stream()
            .map(CommitParentNode::getOid)
            .collect(Collectors.toList());

        for (String parentOid : potentialParents) {
          val parentNodes = nodes.entrySet().stream()
              .filter((potentialId) -> potentialId.getKey().startsWith(parentOid))
              .map(Entry::getValue)
              .collect(Collectors.toList());

          if (parentNodes.size() == 1) {
            continue;
          }

          for (Node parentNode : parentNodes) {
            val parentId = parentNode.getId();
            if (parentId.endsWith(currentRef.getName())) {
              nodes.remove(parentId);
              refForNode.remove(parentId);
              graph.removeNode(parentId);
            }
          }
        }
      }
    }

    // add edges to graph
    for (Ref currentRef : repository.getRefs().getNodes()) {
      val edges = currentRef.getTarget().getHistory().getEdges();
      if (edges == null) {
        continue;
      }
      for (CommitEdge edge : edges) {
        val commitNode = edge.getNode();
        val oid = commitNode.getOid() + ">" + currentRef.getName();
        val node = graph.getNode(oid);
        if (node == null) {
          continue;
        }
        val parents = commitNode.getParents().getNodes();
        val potentialParents = parents.stream()
            .map(CommitParentNode::getOid)
            .collect(Collectors.toList());

        for (String parentId : potentialParents) {
          val parentNode = nodes.entrySet().stream()
              .filter((potentialId) -> potentialId.getKey().startsWith(parentId))
              .map(Entry::getValue)
              .findAny();
          if (parentNode.isEmpty()) {
            continue;
          }
          val edgeId = parentId + ">" + oid;

          final var edge1 = graph.addEdge(edgeId, parentNode.get(), node, true);
          edge1.setAttribute("ui.label",
              parentNode.get().getId().split(">")[1] + " > " + currentRef.getName());
          edge1.setAttribute("ui.style", "text-size: 20px;");
        }
      }
    }

    val yForRefs = new HashMap<String, Integer>();
    // colour remaining nodes with their branch colours and set their x-positions
    for (Entry<String, String> entry : refForNode.entrySet()) {
      val oid = entry.getKey();
      val refName = entry.getValue();
      val node = nodes.get(oid);
      node.addAttribute("ui.style", "fill-color: #" + colourForRef.get(refName) + ";");
      node.addAttribute("x", refs.get(refName));

      var y = yForRefs.getOrDefault(refName, -1);
      if (y == -1) {
        y = yForRefs.values().stream()
            .mapToInt(Integer::intValue)
            .filter(v -> v >= 0)
            .max()
            .orElse(0);
      }

      yForRefs.put(refName, y + 1);

      node.addAttribute("y", y);
    }

  }

  private void setUpView() {
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
