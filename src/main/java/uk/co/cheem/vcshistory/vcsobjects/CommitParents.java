package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

/**
 * The type Commit parents.
 */
@Data
public class CommitParents {

  private List<CommitParentNode> nodes;
}
