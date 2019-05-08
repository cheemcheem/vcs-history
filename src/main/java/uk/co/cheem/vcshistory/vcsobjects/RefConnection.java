package uk.co.cheem.vcshistory.vcsobjects;

import java.util.List;
import lombok.Data;

/**
 * The type Ref connection.
 */
@Data
public class RefConnection {

  private List<Ref> nodes;
}
