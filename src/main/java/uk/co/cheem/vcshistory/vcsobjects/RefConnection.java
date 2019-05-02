package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

@Data
public class RefConnection {
  private List<Ref> nodes;
}
