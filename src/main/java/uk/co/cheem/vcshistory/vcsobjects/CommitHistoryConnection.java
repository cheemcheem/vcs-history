package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

@Data
public class CommitHistoryConnection {
  private PageInfo pageInfo;
  private List<CommitEdge> edges;
}
