package se.bjurr.violations.comments.lib.model;

import java.util.List;

import se.bjurr.violations.lib.util.Optional;

/**
 * Callback interface for applications that supply the comments to a consumer.
 * The implementation of it defines which kind of callback should be made.
 *
 * @author Patrizio Bonzani
 */
public interface CommentSupplier {

	/**
	 * Create a single comment containing all file comments and violations.
	 *
	 * @param string The combined comments.
	 */
	void createBulkComment(String string);

	/**
	 * Create a discussion on the diff on the line the violation occurred.
	 *
	 * @param file The file in which the violation occurred.
	 * @param content The text of the comment.
	 * @param newLine The line number after the patch.
	 * @param oldLine The line number before the patch.
	 */
	void createDiffComment(ChangedFile file, String content, Integer newLine,
			Integer oldLine);

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
