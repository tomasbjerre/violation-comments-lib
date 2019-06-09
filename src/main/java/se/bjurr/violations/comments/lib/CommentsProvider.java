package se.bjurr.violations.comments.lib;

import java.util.List;
import java.util.Optional;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;

public interface CommentsProvider {

  /**
   * Create a single comment containing all file comments and violations.
   *
   * @param string The combined comments.
   */
  void createCommentWithAllSingleFileComments(String string);

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

  /**
   * Should every violation that has been found be commented? If false only changed files will be
   * commented on.
   *
   * @return Should every violation that has been found be commented
   */
  boolean shouldCommentEveryViolation();

  Optional<String> findCommentTemplate();

  Integer getMaxNumberOfViolations();

  Integer getMaxCommentSize();
}
