package se.bjurr.violations.comments.lib;

import java.util.List;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.lib.model.Violation;

public class ViolationComments {
  private final List<Comment> comments;
  private final List<Violation> violations;

  public ViolationComments(List<Comment> comments, List<Violation> violations) {
    this.comments = comments;
    this.violations = violations;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public List<Violation> getViolations() {
    return violations;
  }

  @Override
  public String toString() {
    return "ViolationComments [comments=" + comments + ", violations=" + violations + "]";
  }
}
