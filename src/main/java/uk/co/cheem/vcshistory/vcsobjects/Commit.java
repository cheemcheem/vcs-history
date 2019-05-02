package uk.co.cheem.vcshistory.vcsobjects;

import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import lombok.Data;

@Data
public class Commit {
  private String id;

  @GraphQLArgument(name = "first", value = "10", type = "Integer")
  private CommitHistoryConnection history;
}
