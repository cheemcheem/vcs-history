package uk.co.cheem.vcshistory.view.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import uk.co.cheem.vcshistory.vcsobjects.Author;
import uk.co.cheem.vcshistory.vcsobjects.CommitEdge;
import uk.co.cheem.vcshistory.vcsobjects.CommitNode;
import uk.co.cheem.vcshistory.vcsobjects.CommitParentNode;
import uk.co.cheem.vcshistory.vcsobjects.Ref;
import uk.co.cheem.vcshistory.vcsobjects.Repository;


/**
 * The type Commit view node.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Value
public class CommitViewNode {

  private static final List<String> BRANCH_COLOURS = new ArrayList<>(
      List.of("EE4266", "00A896", "05668D", "3A7CA5")
  );
  private static final Map<String, String> BRANCH_COLOUR_MAP = new HashMap<>();
  private static final Map<String, Integer> BRANCH_PRECEDENCE_MAP = new HashMap<>();
  private static final Set<CommitViewNode> ALL_NODES = new HashSet<>();


  @EqualsAndHashCode.Include
  private String oid;
  @ToString.Exclude
  private String messageHeadline;
  @EqualsAndHashCode.Include
  private String branchName;
  private List<String> parentOids;
  @ToString.Exclude
  private String message;
  @ToString.Exclude
  private Author author;

  private static void fromCommit(CommitNode commit, String branchName) {
    val commitViewNode = new CommitViewNode(
        commit.getOid(),
        commit.getMessageHeadline(),
        branchName,
        commit.getParents().getNodes().stream()
            .map(CommitParentNode::getOid)
            .collect(Collectors.toList()),
        commit.getMessage(),
        commit.getAuthor()
    );
    addNode(commitViewNode);
  }

  private static void addNode(CommitViewNode node) {

    // add node branch colour
    if (!BRANCH_COLOUR_MAP.containsKey(node.getBranchName())) {
      if (!BRANCH_COLOURS.isEmpty()) {
        val iterator = BRANCH_COLOURS.iterator();
        BRANCH_COLOUR_MAP.put(node.getBranchName(), iterator.next());
        iterator.remove();
      } else {
        BRANCH_COLOUR_MAP.put(node.getBranchName(), "000000");
      }
    }

    ALL_NODES.add(node);

  }

  /**
   * From repository commit view node.
   *
   * @param repository the repository
   * @return the commit view node
   */
  public static CommitViewNode fromRepository(Repository repository) {
    for (Ref ref : repository.getRefs().getNodes()) {
      val branchName = ref.getName();
      ref.getTarget().getHistory().getEdges()
          .stream()
          .map(CommitEdge::getNode)
          .forEach(c -> fromCommit(c, branchName));
    }
    val firstCommit = ALL_NODES.stream().filter(n -> n.getParents().isEmpty()).findAny()
        .orElse(null);
    Objects.requireNonNull(firstCommit).setBranchPrecedence(0);
    sortPrecedence(firstCommit, 0);
    cleanWithPrecedence();
    return firstCommit;

  }

  private static void cleanWithPrecedence() {
    val iterator = ALL_NODES.iterator();
    while (iterator.hasNext()) {
      val next = iterator.next();
      val duplicates = ALL_NODES.stream()
          .filter(n -> n.getOid().equals(next.getOid()))
          .collect(Collectors.toList());
      for (CommitViewNode duplicate : duplicates) {
        if (next.getBranchPrecedence() > duplicate.getBranchPrecedence()) {
          iterator.remove();
          break;
        }
      }
    }
  }

  private static void sortPrecedence(CommitViewNode commit, int precedence) {
    if (commit.getBranchPrecedence() == null) {
      commit.setBranchPrecedence(precedence + 1);
    }

    for (CommitViewNode child : commit.getChildren()) {
      sortPrecedence(child, commit.getBranchPrecedence());
    }
  }

  /**
   * Gets parents.
   *
   * @return the parents
   */
  public List<CommitViewNode> getParents() {
    return ALL_NODES.stream()
        .filter(c -> getParentOids().contains(c.getOid()))
        .collect(Collectors.toList());
  }

  /**
   * Gets children.
   *
   * @return the children
   */
  public List<CommitViewNode> getChildren() {
    return ALL_NODES.stream()
        .filter(c -> c.getParentOids().contains(getOid()))
        .collect(Collectors.toList());
  }

  /**
   * Gets colour.
   *
   * @return the colour
   */
  public String getColour() {
    return BRANCH_COLOUR_MAP.get(getBranchName());
  }

  /**
   * Gets branch precedence.
   *
   * @return the branch precedence
   */
  public Integer getBranchPrecedence() {
    return BRANCH_PRECEDENCE_MAP.get(getBranchName());
  }

  private void setBranchPrecedence(Integer precedence) {
    BRANCH_PRECEDENCE_MAP.put(getBranchName(), precedence);
  }
}
