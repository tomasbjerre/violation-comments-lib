package se.bjurr.violations.comments.lib;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.comments.lib.model.CommentsProvider;
import se.bjurr.violations.lib.model.Violation;

import com.google.common.base.Optional;

public class CommentsCreator {
 private static final Logger LOG = LoggerFactory.getLogger(CommentsCreator.class);
 private static final String FINGERPRINT = "<this is a auto generated comment from violation-comments-lib F7F8ASD8123FSDF>";
 private final CommentsProvider commentsProvider;
 private final List<Violation> violations;
 private final List<String> files;

 private CommentsCreator(CommentsProvider commentsProvider, List<Violation> violations) {
  checkNotNull(violations, "violations");
  checkNotNull(commentsProvider, "commentsProvider");
  this.commentsProvider = commentsProvider;
  this.violations = violations;
  LOG.info(violations.size() + " violations.");
  this.files = commentsProvider.getFiles();
 }

 public static void createComments(CommentsProvider commentsProvider, List<Violation> violations) {
  CommentsCreator commentsCreator = new CommentsCreator(commentsProvider, violations);
  commentsCreator.createComments();
 }

 public void createComments() {
  List<Comment> oldComments = commentsProvider.getComments();
  LOG.info(oldComments.size() + " comments found.");
  oldComments = filterCommentsCreatedByThisLib(oldComments);
  LOG.info(oldComments.size() + " comments found from " + CommentsCreator.class.getSimpleName() + ", asking "
    + commentsProvider.getClass().getSimpleName() + " to remove them.");
  commentsProvider.removeComments(oldComments);
  createSingleFileComments();
  createCommentWithAllSingleFileComments();
 }

 private void createCommentWithAllSingleFileComments() {
  StringBuilder sb = new StringBuilder();
  sb.append("Found " + violations.size() + ":\n\n");
  for (Violation violation : violations) {
   String singleFileCommentContent = createSingleFileCommentContent(violation);
   sb.append(singleFileCommentContent + "\n");
  }
  LOG.info("Asking " + commentsProvider.getClass().getSimpleName()
    + " to create comment with all single file comments.");
  commentsProvider.createCommentWithAllSingleFileComments(sb.toString());
 }

 private void createSingleFileComments() {
  LOG.info("Asking " + commentsProvider.getClass().getSimpleName() + " to comment:");
  for (Violation violation : violations) {
   String singleFileCommentContent = createSingleFileCommentContent(violation);
   Optional<String> file = getFile(violation);
   if (file.isPresent()) {
    LOG.info(violation.getReporter() + " " + violation.getSeverity() + " " + violation.getRule().or("") + " "
      + file.get() + " " + violation.getStartLine() + " " + violation.getSource().or(""));
    commentsProvider.createSingleFileComment(file.get(), violation.getStartLine(), singleFileCommentContent);
   }
  }
 }

 /**
  * When creating comment, the call should use the file as it is specified by
  * the comments provider. Not the one specified by the {@link Violation}. The
  * one in the {@link Violation} may not be recognized. <br>
  * <br>
  * Here we make a guess on which file in the {@link CommentsProvider} to use.
  */
 private Optional<String> getFile(Violation violation) {
  for (String providerFile : files) {
   if (violation.getFile().endsWith(providerFile) || providerFile.endsWith(violation.getFile())) {
    return Optional.of(providerFile);
   }
  }
  return absent();
 }

 private String createSingleFileCommentContent(Violation violation) {
  String source = violation.getSource().isPresent() ? ("(" + violation.getSource().get() + ")") : "";
  return ""
    + //
    "Violation: " + violation.getReporter() + " Rule: " + violation.getRule().or("?") + " Severity: "
    + violation.getSeverity() + "\n" + //
    "File: " + violation.getFile() + " " + source + "\n" + //
    violation.getMessage() + "\n" + //
    "\n" + //
    FINGERPRINT + "\n";
 }

 private List<Comment> filterCommentsCreatedByThisLib(List<Comment> unfilteredComments) {
  List<Comment> filteredComments = newArrayList();
  for (Comment comment : unfilteredComments) {
   if (comment.getContent().contains(FINGERPRINT)) {
    filteredComments.add(comment);
   }
  }
  return filteredComments;
 }
}
