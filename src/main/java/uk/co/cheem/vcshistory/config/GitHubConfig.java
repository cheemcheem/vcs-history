package uk.co.cheem.vcshistory.config;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class GitHubConfig {
  @NonNull String ownerName;
  @NonNull String repoName;
  @NonNull String token;
}
