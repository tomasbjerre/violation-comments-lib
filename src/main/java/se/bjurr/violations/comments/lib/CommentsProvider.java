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
   * Called, instead of {@link #removeComments(List)} being called directly, for comments whose
   * violation is no longer reported (when {@link #shouldKeepOldComments()} is {@code false}).
   *
   * <p>Implementations that support some form of resolvable/trackable comment (a GitLab discussion,
   * a Bitbucket task, ...) should resolve such a comment here instead of removing it - whether the
   * comment was created as one by this tool, or a user turned it into one afterwards. There is
   * deliberately no separate "should create resolvable comments" flag to gate this: that would only
   * describe how comments are created going forward, not what a specific existing comment actually
   * is right now, which can only be known by looking at each comment's current state on the
   * platform. A comment that isn't a resolvable/trackable type should still be removed here.
   *
   * <p>Defaults to calling {@link #removeComments(List)} for every comment, so implementations that
   * don't override this keep today's behavior unchanged.
   *
   * @param comments The comments to resolve or remove.
   */
  default void resolveComments(final List<Comment> comments) {
    this.removeComments(comments);
  }
}
