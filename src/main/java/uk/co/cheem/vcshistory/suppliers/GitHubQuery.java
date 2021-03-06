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
import uk.co.cheem.vcshistory.config.Config;
import uk.co.cheem.vcshistory.vcsobjects.Repository;
import uk.co.cheem.vcshistory.vcsobjects.Response;

/**
 * The type Git hub query.
 */
@RequiredArgsConstructor
@Slf4j
public class GitHubQuery {

  private final Config configuration;
  private HttpRequest httpRequest;
  private @Getter
  Repository repository;
  private HttpResponse<String> response;

  /**
   * Make request.
   */
  public void makeRequest() {
    try {
      val query = getQuery();
      val authorization = "token " + this.configuration.getToken();
      httpRequest = HttpRequest
          .newBuilder(URI.create(configuration.getUrl()))
          .POST(BodyPublishers.ofString(query))
          .header("Authorization", authorization)
          .build();
    } catch (RuntimeException e) {
      throw new QueryException("Failed to create the request.", e);
    }
  }

  /**
   * Send request.
   */
  public void sendRequest() {
    val httpClient = HttpClient.newHttpClient();
    try {
      response = httpClient.send(httpRequest, BodyHandlers.ofString());
      log.debug(response.body());

    } catch (IOException | InterruptedException e) {
      throw new QueryException("Failed to send the request.", e);
    }
  }

  /**
   * Parse response.
   */
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
        + "      refs(first: 100, refPrefix:\\\"refs/heads/\\\") {\\n"
        + "        nodes {\\n"
        + "          name\\n"
        + "          target {\\n"
        + "          ... on Commit {\\n"
        + "            id\\n"
        + "            history {\\n"
        + "              pageInfo {\\n"
        + "                hasNextPage\\n"
        + "              }\\n"
        + "              edges {\\n"
        + "                node {\\n"
        + "                  parents(first: 10) {\\n"
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

  /**
   * The type Query exception.
   */
  public static class QueryException extends RuntimeException {

    private QueryException(String message, Throwable cause) {
      super(message, cause);
    }
  }


}
