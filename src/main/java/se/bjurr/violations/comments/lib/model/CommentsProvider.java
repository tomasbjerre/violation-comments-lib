package se.bjurr.violations.comments.lib.model;

import java.util.List;
import se.bjurr.violations.lib.util.Optional;

/**
 * @deprecated Replaced by {@link CommentSupplier}.
 */
@Deprecated
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

	/**
	 * Returns if comments and violations should be combined into one comment.
	 *
	 * @return <code>true</code> if the comments should be combined,
	 *         <code>false</code> otherwise.
	 */
	boolean shouldCreateCommentWithAllSingleFileComments();

	/**
	 * Returns if a comment per violation should be made on the diff.
	 *
	 * @return <code>true</code> if each comment containing the violation
	 *         should be posted on the diff, <code>false</code> otherwise.
	 */
	boolean shouldCreateSingleFileComment();

	boolean shouldKeepOldComments();

	Optional<String> findCommentTemplate();
}
