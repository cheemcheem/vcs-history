package uk.co.cheem.vcshistory.vcsobjects;

import lombok.Data;

@Data
public class Ref {
  private String name;

  private Commit target;
}
