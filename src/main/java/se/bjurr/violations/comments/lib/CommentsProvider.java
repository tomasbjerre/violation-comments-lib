package se.bjurr.violations.comments.lib;

import java.util.List;
import java.util.Optional;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;

public interface CommentsProvider {

  /**
   * Creates a comment.
   *
   * @param string The comment.
   */
  void createComment(String string);

  /**
   * Create a discussion on the diff on the line the violation occurred.
   *
   * @param file The file in which the violation occurred.
   * @param line The line number after the patch.
   * @param comment The text of the comment.
   */
  void createSingleFileComment(ChangedFile file, Integer line, String comment);

  List<Comment> getComments();

  List<ChangedFile> getFiles();

  void removeComments(List<Comment> comments);

  boolean shouldComment(ChangedFile changedFile, Integer line);

  boolean shouldCreateCommentWithAllSingleFileComments();

  boolean shouldCreateSingleFileComment();

  boolean shouldKeepOldComments();

  boolean shouldCommentOnlyChangedFiles();

  Optional<String> findCommentTemplate();

  Integer getMaxNumberOfViolations();

  Integer getMaxCommentSize();

  /**
   * Whether a separate summary comment, distinct from {@link
   * #shouldCreateCommentWithAllSingleFileComments()}, should be created. Defaults to {@code false}
   * so existing implementations keep their current behavior unchanged.
   */
  default boolean shouldCreateSummaryComment() {
    return false;
  }

  /**
   * Template used for the summary comment, see {@link #shouldCreateSummaryComment()}. Defaults to
   * empty, meaning the library's built-in default template is used.
   */
  default Optional<String> findSummaryCommentTemplate() {
    return Optional.empty();
  }

  /**
   * Whether comments for violations that are no longer reported should be resolved, rather than
   * removed, see {@link #resolveComments(List)}. Defaults to {@code false} so existing
   * implementations keep their current behavior of removing them via {@link #removeComments(List)}.
   */
  default boolean shouldCreateResolvableComments() {
    return false;
  }

  /**
   * Called instead of {@link #removeComments(List)}, when {@link #shouldCreateResolvableComments()}
   * is {@code true}, for comments whose violation is no longer reported. Implementations that
   * support some form of resolvable/trackable comment (a GitLab discussion, a Bitbucket task, ...)
   * should mark it resolved here instead of deleting it.
   *
   * <p>Defaults to calling {@link #removeComments(List)}, so implementations that don't override
   * this keep today's delete behavior even if {@link #shouldCreateResolvableComments()} were ever
   * mistakenly set to {@code true}.
   *
   * @param comments The comments to resolve.
   */
  default void resolveComments(final List<Comment> comments) {
    this.removeComments(comments);
  }
}
