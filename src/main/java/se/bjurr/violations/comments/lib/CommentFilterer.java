package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.comments.lib.ViolationRenderer.identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.lib.model.Violation;

public class CommentFilterer {

  static List<Comment> filterCommentsWithContent(
      final List<Comment> unfilteredComments, final String containing) {
    final List<Comment> filteredComments = new ArrayList<>();
    for (final Comment comment : unfilteredComments) {
      if (comment.getContent().trim().contains(containing.trim())) {
        filteredComments.add(comment);
      }
    }
    return filteredComments;
  }

  static List<Comment> filterCommentsWithoutContent(
      final List<Comment> unfilteredComments, final String containing) {
    final List<Comment> filteredComments = new ArrayList<>();
    for (final Comment comment : unfilteredComments) {
      if (!comment.getContent().trim().contains(containing.trim())) {
        filteredComments.add(comment);
      }
    }
    return filteredComments;
  }

  static ViolationComments getViolationComments(
      final List<Comment> comments, final Set<Violation> violations) {
    final List<Violation> madeViolations = new ArrayList<>();
    final List<Comment> madeComments = new ArrayList<>();
    for (final Violation violation : violations) {
      for (final Comment candidate : comments) {
        final boolean containsIdentifier =
            candidate.getContent().contains(identifier(violation) + "");
        if (containsIdentifier) {
          madeViolations.add(violation);
          madeComments.add(candidate);
        }
      }
    }
    return new ViolationComments(madeComments, madeViolations);
  }
}
