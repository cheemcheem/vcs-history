package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

@Data
public class CommitParents {

  private List<CommitParentNode> nodes;
}
