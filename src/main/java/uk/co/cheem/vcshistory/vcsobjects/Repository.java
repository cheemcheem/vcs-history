package uk.co.cheem.vcshistory.vcsobjects;


import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLArguments;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;
import lombok.Data;

@Data
@GraphQLProperty(
    name = "repository",
    arguments = {
        @GraphQLArgument(name = "owner", type = "String!"),
        @GraphQLArgument(name = "name", type = "String!")
    }
)
public class Repository {

  @GraphQLArguments({
      @GraphQLArgument(name = "first", value = "10", type = "Integer"),
      @GraphQLArgument(name = "refPrefix", value = "refs/heads/")
  })
  private RefConnection refs;

}
