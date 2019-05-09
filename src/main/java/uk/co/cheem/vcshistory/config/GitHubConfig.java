package uk.co.cheem.vcshistory.config;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * The type Git hub config.
 */
@Builder
@Value
class GitHubConfig implements Config {

  private final String url = "https://api.github.com/graphql";
  @NonNull String ownerName;
  @NonNull String repoName;
  @NonNull String token;
}
