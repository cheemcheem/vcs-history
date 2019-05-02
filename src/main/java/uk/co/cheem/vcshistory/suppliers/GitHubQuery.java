package uk.co.cheem.vcshistory.suppliers;

import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLRequestEntity.RequestBuilder;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;
import io.aexp.nodes.graphql.GraphQLTemplate.GraphQLMethod;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.cheem.vcshistory.config.GitHubConfig;
import uk.co.cheem.vcshistory.vcsobjects.Repository;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@RequiredArgsConstructor
public
class GitHubQuery {

  final @NonNull GitHubConfig configuration;

  @Getter GraphQLResponseEntity<Repository> response;
  RequestBuilder requestBuilder;

  public void buildQuery() {
    log.info("Setting up GraphQL Query with configuration.");

    this.requestBuilder = GraphQLRequestEntity.Builder();
    addURL();
    addArguments();
    addAuthorization();
    requestBuilder.requestMethod(GraphQLMethod.QUERY).request(Repository.class);

    log.info("GraphQL Query set up.");

  }

  private void addAuthorization() {
    final var authorizationHeader = Map.of("Authorization", "bearer " + configuration.getToken());
    requestBuilder.headers(authorizationHeader);
  }

  private void addArguments() {
    val name = configuration.getRepoName();
    val owner = configuration.getOwnerName();

    requestBuilder.arguments(
        new Arguments(
            "repository",
            new Argument<>("name", name),
            new Argument<>("owner", owner)
        )
    );
  }

  @SneakyThrows
  private void addURL() {
    val url = "https://api.github.com/graphql";
    requestBuilder.url(url);
  }

  public void sendQuery() {
    val graphQLTemplate = new GraphQLTemplate();

    log.info("Sending GraphQL query.");
    this.response = graphQLTemplate.query(requestBuilder.build(), Repository.class);
    log.info("Received GraphQL response.");
  }


}


