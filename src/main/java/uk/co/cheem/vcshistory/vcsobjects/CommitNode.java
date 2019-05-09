package uk.co.cheem.vcshistory.vcsobjects;

import lombok.Data;
import lombok.ToString;

/**
 * The type Commit node.
 */
@Data
public class CommitNode {

  private CommitParents parents;
  @ToString.Exclude
  private String messageHeadline;
  private String oid;
  @ToString.Exclude
  private String message;
  @ToString.Exclude
  private Author author;
}
