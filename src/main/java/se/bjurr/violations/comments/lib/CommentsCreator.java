package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.lib.util.Optional.absent;
import static se.bjurr.violations.lib.util.Optional.fromNullable;
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
 public static final String FINGERPRINT = "<this is a auto generated comment from violation-comments-lib F7F8ASD8123FSDF>";
 private static final Logger LOG = LoggerFactory.getLogger(CommentsCreator.class);

 public static void createComments(CommentsProvider commentsProvider, List<Violation> violations,
   Integer maxCommentSize) {
  CommentsCreator commentsCreator = new CommentsCreator(commentsProvider, violations, maxCommentSize);
  commentsCreator.createComments();
 }

 private final CommentsProvider commentsProvider;
 private final List<ChangedFile> files;

 private final Integer maxCommentSize;
 private final List<Violation> violations;

 private CommentsCreator(CommentsProvider commentsProvider, List<Violation> violations, Integer maxCommentSize) {
  checkNotNull(violations, "violations");
  checkNotNull(commentsProvider, "commentsProvider");
  this.commentsProvider = commentsProvider;
  LOG.debug(violations.size() + " violations.");
  files = commentsProvider.getFiles();
  this.violations = filterChanged(violations);
  this.maxCommentSize = maxCommentSize;
 }

 public void createComments() {
  List<Comment> oldComments = commentsProvider.getComments();
  LOG.debug(oldComments.size() + " comments found.");
  oldComments = filterCommentsCreatedByThisLib(oldComments);
  LOG.debug(oldComments.size() + " comments found from " + CommentsCreator.class.getSimpleName() + ", asking "
    + commentsProvider.getClass().getSimpleName() + " to remove them.");
  commentsProvider.removeComments(oldComments);
  if (commentsProvider.shouldCreateSingleFileComment()) {
   createSingleFileComments();
  }
  if (commentsProvider.shouldCreateCommentWithAllSingleFileComments()) {
   createCommentWithAllSingleFileComments();
  }
 }

 private void createCommentWithAllSingleFileComments() {
  if (violations.isEmpty()) {
   LOG.debug("Found no violations, not creating any comment.");
   return;
  }
  StringBuilder sb = new StringBuilder();
  sb.append("Found " + violations.size() + " violations:\n\n");
  for (Violation violation : violations) {
    Optional<ChangedFile> changedFile = getFile(violation);
   String singleFileCommentContent = createSingleFileCommentContent(changedFile.get(),violation);
   if (sb.length() + singleFileCommentContent.length() >= maxCommentSize) {
    LOG.debug("Asking " + commentsProvider.getClass().getSimpleName()
      + " to create comment with a subset of all single file comments.");
    commentsProvider.createCommentWithAllSingleFileComments(sb.toString());
    sb = new StringBuilder();
   }
   sb.append(singleFileCommentContent + "\n");
  }
  LOG.debug(
    "Asking " + commentsProvider.getClass().getSimpleName() + " to create comment with all single file comments.");
  commentsProvider.createCommentWithAllSingleFileComments(sb.toString());
 }

 private String createSingleFileCommentContent(ChangedFile changedFile, Violation violation) {
  Optional<String> providedCommentFormat = commentsProvider.findCommentFormat(changedFile, violation);
  if (providedCommentFormat.isPresent()) {
   return providedCommentFormat.get();
  }

  String source = violation.getSource().isPresent() ? "**Source**: " + violation.getSource().get() + "\n" : "";
  return "" + //
    "**Reporter**: " + violation.getReporter() + "\n" + //
    "**Rule**: " + violation.getRule().or("?") + "\n" + //
    "**Severity**: " + violation.getSeverity() + "\n" + //
    "**File**: " + changedFile.getFilename() + " L" + violation.getStartLine() + "\n" + //
    source + //
    "\n" + //
    violation.getMessage() + "\n" + //
    "\n" + //
    "*" + FINGERPRINT + "*" + "\n";
 }

 private void createSingleFileComments() {
  LOG.debug("Asking " + commentsProvider.getClass().getSimpleName() + " to comment:");
  for (Violation violation : violations) {
   Optional<ChangedFile> file = getFile(violation);
   if (file.isPresent()) {
     String singleFileCommentContent = createSingleFileCommentContent(file.get(),violation);
    LOG.debug(violation.getReporter() + " " + violation.getSeverity() + " " + violation.getRule().or("") + " "
      + file.get() + " " + violation.getStartLine() + " " + violation.getSource().or(""));
    commentsProvider.createSingleFileComment(file.get(), violation.getStartLine(), singleFileCommentContent);
   }
  }
 }

 private List<Violation> filterChanged(List<Violation> mixedViolations) {
  List<Violation> isChanged = new ArrayList<>();
  for (Violation violation : mixedViolations) {
   Optional<ChangedFile> file = getFile(violation);
   if (file.isPresent()) {
    boolean shouldComment = commentsProvider.shouldComment(file.get(), violation.getStartLine());
    if (shouldComment) {
     isChanged.add(violation);
    }
   }
  }
  return isChanged;
 }

 private List<Comment> filterCommentsCreatedByThisLib(List<Comment> unfilteredComments) {
  List<Comment> filteredComments = new ArrayList<>();
  for (Comment comment : unfilteredComments) {
   if (comment.getContent().contains(FINGERPRINT)) {
    filteredComments.add(comment);
   }
  }
  return filteredComments;
 }

 /**
  * When creating comment, the call should use the file as it is specified by
  * the comments provider. Not the one specified by the {@link Violation}. The
  * one in the {@link Violation} may not be recognized. <br>
  * <br>
  * Here we make a guess on which file in the {@link CommentsProvider} to use.
  */
 public Optional<ChangedFile> getFile(Violation violation) {
  for (ChangedFile providerFile : files) {
   if (violation.getFile().endsWith(providerFile.getFilename())
     || providerFile.getFilename().endsWith(violation.getFile())) {
    return fromNullable(providerFile);
   }
  }
  return absent();
 }
}
