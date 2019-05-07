package uk.co.cheem.vcshistory.vcsobjects;

import lombok.Data;

@Data
public class CommitNode {

  private CommitParents parents;
  private String messageHeadline;
  private String oid;
  private String message;
  private Author author;
}
