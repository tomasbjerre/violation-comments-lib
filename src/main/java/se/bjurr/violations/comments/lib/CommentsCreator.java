package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.comments.lib.ChangedFileUtils.getFile;
import static se.bjurr.violations.comments.lib.CommentFilterer.filterCommentsWithContent;
import static se.bjurr.violations.comments.lib.CommentFilterer.filterCommentsWithoutContent;
import static se.bjurr.violations.comments.lib.CommentFilterer.getViolationComments;
import static se.bjurr.violations.comments.lib.ViolationRenderer.createSingleFileCommentContent;
import static se.bjurr.violations.comments.lib.ViolationRenderer.getAccumulatedComments;
import static se.bjurr.violations.lib.util.Utils.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.comments.lib.model.CommentsProvider;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Optional;

public class CommentsCreator {
  public static final String FINGERPRINT =
      "<this is a auto generated comment from violation-comments-lib F7F8ASD8123FSDF>";
  private static final Logger LOGGER = LoggerFactory.getLogger(CommentsCreator.class);
  public static final String FINGERPRINT_ACC = "<ACCUMULATED-VIOLATIONS>";

  public static void createComments(
      final CommentsProvider commentsProvider,
      final List<Violation> violations,
      final Integer maxCommentSize) {
    final CommentsCreator commentsCreator =
        new CommentsCreator(commentsProvider, violations, maxCommentSize);
    commentsCreator.createComments();
  }

  private final CommentsProvider commentsProvider;
  private final List<ChangedFile> files;

  private final Integer maxCommentSize;
  private final List<Violation> violations;

  CommentsCreator(
      final CommentsProvider commentsProvider,
      final List<Violation> violations,
      final Integer maxCommentSize) {
    checkNotNull(violations, "violations");
    checkNotNull(commentsProvider, "commentsProvider");
    this.commentsProvider = commentsProvider;
    LOGGER.debug(violations.size() + " violations.");
    files = commentsProvider.getFiles();
    this.violations = filterChanged(violations);
    this.maxCommentSize = maxCommentSize;
  }

  public void createComments() {
    if (commentsProvider.shouldCreateBulkComment()) {
      createBulkComments();
    }
    if (commentsProvider.shouldCreateCommentPerViolation()) {
      createDiffFileComments();
    }
  }

  /**
   * Create a comment containing the accumulated violations and comments of all
   * files. The comment is split into several comments if it is longer than the
   * maximum comment length.
   */
  private void createBulkComments() {
    final List<String> accumulatedComments =
        getAccumulatedComments(
            violations, files, commentsProvider.findCommentTemplate().orNull(), maxCommentSize);
    for (final String accumulatedComment : accumulatedComments) {
      LOGGER.debug(
          "Asking "
              + commentsProvider.getClass().getSimpleName()
              + " to create comment with all single file comments.");
      List<Comment> oldComments = commentsProvider.getComments();
      oldComments = filterCommentsWithContent(oldComments, FINGERPRINT_ACC);
      final List<Comment> alreadyMadeComments =
          filterCommentsWithContent(oldComments, accumulatedComment);

      removeOldCommentsThatAreNotStillReported(oldComments, alreadyMadeComments);

      if (violations.isEmpty()) {
        return;
      }

      final boolean commentHasNotBeenMade = alreadyMadeComments.isEmpty();
      if (commentHasNotBeenMade) {
        commentsProvider.createBulkComment(accumulatedComment);
      }
    }
  }

  /**
   * Create a discussion on the diff for each violation.
   */
  private void createDiffFileComments() {
    List<Comment> oldComments = commentsProvider.getComments();
    oldComments = filterCommentsWithContent(oldComments, FINGERPRINT);
    oldComments = filterCommentsWithoutContent(oldComments, FINGERPRINT_ACC);
    LOGGER.debug("Asking " + commentsProvider.getClass().getSimpleName() + " to comment:");

    final ViolationComments alreadyMadeComments = getViolationComments(oldComments, violations);

    removeOldCommentsThatAreNotStillReported(oldComments, alreadyMadeComments.getComments());

    for (final Violation violation : violations) {
      final boolean violationCommentExistsSinceBefore =
          alreadyMadeComments.getViolations().contains(violation);
      if (violationCommentExistsSinceBefore) {
        continue;
      }
      final Optional<ChangedFile> file = getFile(files, violation);
      if (file.isPresent()) {
        final String commentTemplate = commentsProvider.findCommentTemplate().orNull();
        final String discussionContent =
            createSingleFileCommentContent(file.get(), violation, commentTemplate);
        LOGGER.debug(violation.getReporter() + " " + violation.getSeverity() +
                " " + violation.getRule() + " " + file.get() + " " +
                violation.getStartLine() + " " + violation.getSource());
        commentsProvider.createDiffComment(file.get(), discussionContent,
                violation.getStartLine(), null);
      }
    }
  }

  private void removeOldCommentsThatAreNotStillReported(
      final List<Comment> oldComments, final List<Comment> comments) {
    if (!commentsProvider.shouldKeepOldComments()) {
      final List<Comment> existingWithoutViolation = new ArrayList<>();
      existingWithoutViolation.addAll(oldComments);
      existingWithoutViolation.removeAll(comments);
      commentsProvider.removeComments(existingWithoutViolation);
    }
  }

  private List<Violation> filterChanged(final List<Violation> mixedViolations) {
    final List<Violation> isChanged = new ArrayList<>();
    for (final Violation violation : mixedViolations) {
      final Optional<ChangedFile> file = getFile(files, violation);
      if (file.isPresent()) {
        final boolean shouldComment =
            commentsProvider.shouldComment(file.get(), violation.getStartLine());
        if (shouldComment) {
          isChanged.add(violation);
        }
      }
    }
    return isChanged;
  }
}
