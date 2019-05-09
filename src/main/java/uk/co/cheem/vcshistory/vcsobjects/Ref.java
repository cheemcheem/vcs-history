package uk.co.cheem.vcshistory.vcsobjects;

import lombok.Data;

/**
 * The type Ref.
 */
@Data
public class Ref {

  private String name;
  private Commit target;
}
