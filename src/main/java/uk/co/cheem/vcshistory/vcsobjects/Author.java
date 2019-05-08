package uk.co.cheem.vcshistory.vcsobjects;

import java.util.Date;
import lombok.Data;

/**
 * The type Author.
 */
@Data
public class Author {

  private String name;
  private String email;
  private Date date;
}
