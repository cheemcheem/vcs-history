package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

/**
 * The type Commit.
 */
@Data
public class Commit {

  private String id;

  private CommitHistoryConnection history;

  private List<CommitEdge> edges;
}
