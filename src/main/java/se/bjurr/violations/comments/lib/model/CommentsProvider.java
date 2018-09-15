package se.bjurr.violations.comments.lib.model;

import java.util.List;

import se.bjurr.violations.lib.util.Optional;

/**
 * Callback interface for applications that supply the comments to a consumer.
 * The implementation of it defines which kind of callback should be made.
 *
 * @author Tomas Bjerre, Patrizio Bonzani
 */
public interface CommentsProvider {

	/**
	 * Create a single comment containing all file comments and violations.
	 *
	 * @param string The combined comments.
	 */
	void createBulkComment(String string);

	/**
	 * Create a comment on the on the line the violation occurred.
	 *
	 * @param file The file in which the violation occurred.
	 * @param content The text of the comment.
	 * @param line The line number on which the violation occurred.
	 */
	void createViolationComment(ChangedFile file, String content, Integer line);

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
	boolean shouldCreateBulkComment();

	/**
	 * Returns if a comment per violation should be made on the diff.
	 *
	 * @return <code>true</code> if each comment containing the violation
	 *         should be posted on the diff, <code>false</code> otherwise.
	 */
	boolean shouldCreateCommentPerViolation();

	boolean shouldKeepOldComments();

	Optional<String> findCommentTemplate();
}
