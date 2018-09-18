package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.comments.lib.ChangedFileUtils.findChangedFile;
import static se.bjurr.violations.comments.lib.CommentFilterer.filterCommentsWithContent;
import static se.bjurr.violations.comments.lib.CommentFilterer.filterCommentsWithoutContent;
import static se.bjurr.violations.comments.lib.CommentFilterer.getViolationComments;
import static se.bjurr.violations.comments.lib.ViolationRenderer.createSingleFileCommentContent;
import static se.bjurr.violations.comments.lib.ViolationRenderer.getAccumulatedComments;
import static se.bjurr.violations.lib.util.Utils.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Optional;

public class CommentsCreator {
  public static final String FINGERPRINT =
      "<this is a auto generated comment from violation-comments-lib F7F8ASD8123FSDF>";
  private static final Logger LOG = LoggerFactory.getLogger(CommentsCreator.class);
  public static final String FINGERPRINT_ACC = "<ACCUMULATED-VIOLATIONS>";
  private final ViolationsLogger violationsLogger;

  public static void createComments(
      final ViolationsLogger violationsLogger,
      final List<Violation> violations,
      final Integer maxCommentSize,
      final CommentsProvider commentsProvider) {

    final CommentsCreator commentsCreator =
        new CommentsCreator(violationsLogger, commentsProvider, violations, maxCommentSize);
    commentsCreator.createComments();
  }

  private final CommentsProvider commentsProvider;
  private final List<ChangedFile> files;

  private final Integer maxCommentSize;
  private final List<Violation> violations;

  CommentsCreator(
      ViolationsLogger violationsLogger,
      final CommentsProvider commentsProvider,
      final List<Violation> violations,
      final Integer maxCommentSize) {
    checkNotNull(violations, "violations");
    checkNotNull(commentsProvider, "commentsProvider");
    this.violationsLogger = checkNotNull(violationsLogger, "violationsLogger");
    this.commentsProvider = commentsProvider;
    files = commentsProvider.getFiles();
    this.violations = filterChanged(violations);
    this.maxCommentSize = maxCommentSize;
  }

  public void createComments() {
    if (commentsProvider.shouldCreateCommentWithAllSingleFileComments()) {
      createCommentWithAllSingleFileComments();
    }
    if (commentsProvider.shouldCreateSingleFileComment()) {
      createSingleFileComments();
    }
    if (!commentsProvider.shouldCreateCommentWithAllSingleFileComments()
        && !commentsProvider.shouldCreateSingleFileComment()) {
      violationsLogger.log(
          "Will not comment because both 'CreateCommentWithAllSingleFileComments' and 'CreateSingleFileComment' is false.");
    }
  }

  private void createCommentWithAllSingleFileComments() {
    final List<String> accumulatedComments =
        getAccumulatedComments(
            violations, files, commentsProvider.findCommentTemplate().orNull(), maxCommentSize);
    for (final String accumulatedComment : accumulatedComments) {
      violationsLogger.log(
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
        commentsProvider.createCommentWithAllSingleFileComments(accumulatedComment);
      }
    }
  }

  private void createSingleFileComments() {
    List<Comment> oldComments = commentsProvider.getComments();
    oldComments = filterCommentsWithContent(oldComments, FINGERPRINT);
    oldComments = filterCommentsWithoutContent(oldComments, FINGERPRINT_ACC);
    violationsLogger.log("Asking " + commentsProvider.getClass().getSimpleName() + " to comment:");

    final ViolationComments alreadyMadeComments = getViolationComments(oldComments, violations);

    removeOldCommentsThatAreNotStillReported(oldComments, alreadyMadeComments.getComments());

    for (final Violation violation : violations) {
      final boolean violationCommentExistsSinceBefore =
          alreadyMadeComments.getViolations().contains(violation);
      if (violationCommentExistsSinceBefore) {
        continue;
      }
      final Optional<ChangedFile> changedFile = findChangedFile(files, violation);
      if (changedFile.isPresent()) {
        final String commentTemplate = commentsProvider.findCommentTemplate().orNull();
        final String singleFileCommentContent =
            createSingleFileCommentContent(changedFile.get(), violation, commentTemplate);
        violationsLogger.log(
            violation.getReporter()
                + " "
                + violation.getSeverity()
                + " "
                + violation.getRule()
                + " "
                + changedFile.get().getFilename()
                + " "
                + violation.getStartLine()
                + " "
                + violation.getSource());
        commentsProvider.createSingleFileComment(
            changedFile.get(), violation.getStartLine(), singleFileCommentContent);
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
    String changedFiles =
        files //
            .stream() //
            .map((f) -> f.getFilename()) //
            .sorted() //
            .collect(Collectors.joining("\n"));
    violationsLogger.log("Files changed:\n" + changedFiles);

    String violationFiles =
        mixedViolations //
            .stream() //
            .map((f) -> f.getFile()) //
            .distinct() //
            .sorted() //
            .collect(Collectors.joining("\n"));
    violationsLogger.log("Files with violations:\n" + violationFiles);

    final List<Violation> isChanged = new ArrayList<>();
    for (final Violation violation : mixedViolations) {
      final Optional<ChangedFile> file = findChangedFile(files, violation);
      if (file.isPresent()) {
        final boolean shouldComment =
            commentsProvider.shouldComment(file.get(), violation.getStartLine());
        if (shouldComment) {
          isChanged.add(violation);
          violationsLogger.log(
              "Will include violation on: " + violation.getFile() + " " + violation.getStartLine());
        } else {
          violationsLogger.log(
              "Will not include violation on changed file: "
                  + violation.getFile()
                  + " "
                  + violation.getStartLine()
                  + ".");
        }
      } else {
        violationsLogger.log(
            "Will not include violation on un-changed file: "
                + violation.getFile()
                + " "
                + violation.getStartLine()
                + ".");
      }
    }
    return isChanged;
  }
}
