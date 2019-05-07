package uk.co.cheem.vcshistory.suppliers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.cheem.vcshistory.config.GitHubConfig;
import uk.co.cheem.vcshistory.vcsobjects.Repository;
import uk.co.cheem.vcshistory.vcsobjects.Response;

@RequiredArgsConstructor
@Slf4j
public class GitHubQuery {

  private final GitHubConfig configuration;
  private HttpRequest httpRequest;
  private @Getter
  Repository repository;
  private HttpResponse<String> response;

  public void makeRequest() {
    try {
      val query = getQuery();
      val authorization = "token " + this.configuration.getToken();
      httpRequest = HttpRequest
          .newBuilder(URI.create("https://api.github.com/graphql"))
          .POST(BodyPublishers.ofString(query))
          .header("Authorization", authorization)
          .build();
    } catch (RuntimeException e) {
      throw new QueryException("Failed to create the request.", e);
    }
  }

  public void sendRequest() {
    val httpClient = HttpClient.newHttpClient();
    try {
      response = httpClient.send(httpRequest, BodyHandlers.ofString());
      log.debug(response.body());

    } catch (IOException | InterruptedException e) {
      throw new QueryException("Failed to send the request.", e);
    }
  }

  public void parseResponse() {
    try {
      val parsedResponse = new ObjectMapper().readValue(response.body(), Response.class);
      repository = parsedResponse.getData().getRepository();
    } catch (IOException e) {
      throw new QueryException("Failed to parse response.", e);
    }

    log.debug("Repository details in response: " + repository.toString());

  }

  private String getQuery() {
    val ownerName = this.configuration.getOwnerName();
    val repoName = this.configuration.getRepoName();
    return "{\"query\": \""
        + "query {\\n"
        + "    repository(owner: \\\"" + ownerName + "\\\", name: \\\"" + repoName + "\\\") {\\n"
        + "      refs(first: 10, refPrefix:\\\"refs/heads/\\\") {\\n"
        + "        nodes {\\n"
        + "          name\\n"
        + "          target {\\n"
        + "          ... on Commit {\\n"
        + "            id\\n"
        + "            history(first: 99) {\\n"
        + "              pageInfo {\\n"
        + "                hasNextPage\\n"
        + "              }\\n"
        + "              edges {\\n"
        + "                node {\\n"
        + "                  parents(first: 1) {\\n"
        + "                    nodes {\\n"
        + "                      oid\\n"
        + "                    }\\n"
        + "                  }\\n"
        + "                  messageHeadline\\n"
        + "                  oid\\n"
        + "                  message\\n"
        + "                  author {\\n"
        + "                    name\\n"
        + "                    email\\n"
        + "                    date\\n"
        + "                  }\\n"
        + "                }\\n"
        + "              }\\n"
        + "            }\\n"
        + "          }\\n"
        + "        }\\n"
        + "        }\\n"
        + "      }\\n"
        + "    }\\n"
        + "  }\\n\""
        + "}";
  }

  public static class QueryException extends RuntimeException {

    private QueryException(String message, Throwable cause) {
      super(message, cause);
    }
  }


}
