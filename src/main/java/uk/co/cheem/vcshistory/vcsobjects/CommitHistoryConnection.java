package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

/**
 * The type Commit history connection.
 */
@Data
public class CommitHistoryConnection {

  private PageInfo pageInfo;
  private List<CommitEdge> edges;
}
