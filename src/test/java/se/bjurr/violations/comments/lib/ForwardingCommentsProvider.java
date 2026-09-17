package se.bjurr.violations.comments.lib;

import java.util.List;
import java.util.Optional;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;

/**
 * Forwards every {@link CommentsProvider} method to a wrapped delegate, so a test can subclass this
 * and override only the one method it cares about.
 */
class ForwardingCommentsProvider implements CommentsProvider {
  private final CommentsProvider delegate;

  ForwardingCommentsProvider(final CommentsProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public void createComment(final String string) {
    this.delegate.createComment(string);
  }

  @Override
  public void createSingleFileComment(
      final ChangedFile file, final Integer line, final String comment) {
    this.delegate.createSingleFileComment(file, line, comment);
  }

  @Override
  public List<Comment> getComments() {
    return this.delegate.getComments();
  }

  @Override
  public List<ChangedFile> getFiles() {
    return this.delegate.getFiles();
  }

  @Override
  public void removeComments(final List<Comment> comments) {
    this.delegate.removeComments(comments);
  }

  @Override
  public boolean shouldComment(final ChangedFile changedFile, final Integer line) {
    return this.delegate.shouldComment(changedFile, line);
  }

  @Override
  public boolean shouldCreateCommentWithAllSingleFileComments() {
    return this.delegate.shouldCreateCommentWithAllSingleFileComments();
  }

  @Override
  public boolean shouldCreateSingleFileComment() {
    return this.delegate.shouldCreateSingleFileComment();
  }

  @Override
  public boolean shouldKeepOldComments() {
    return this.delegate.shouldKeepOldComments();
  }

  @Override
  public boolean shouldCommentOnlyChangedFiles() {
    return this.delegate.shouldCommentOnlyChangedFiles();
  }

  @Override
  public Optional<String> findCommentTemplate() {
    return this.delegate.findCommentTemplate();
  }

  @Override
  public Integer getMaxNumberOfViolations() {
    return this.delegate.getMaxNumberOfViolations();
  }

  @Override
  public Integer getMaxCommentSize() {
    return this.delegate.getMaxCommentSize();
  }

  @Override
  public boolean shouldCreateSummaryComment() {
    return this.delegate.shouldCreateSummaryComment();
  }

  @Override
  public Optional<String> findSummaryCommentTemplate() {
    return this.delegate.findSummaryCommentTemplate();
  }

  @Override
  public void resolveComments(final List<Comment> comments) {
    this.delegate.resolveComments(comments);
  }
}
