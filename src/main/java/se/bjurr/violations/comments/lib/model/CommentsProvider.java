package se.bjurr.violations.comments.lib.model;

import java.util.List;
import se.bjurr.violations.lib.util.Optional;

public interface CommentsProvider {

	/**
	 * Create a single comment containing all file comments and violations.
	 * @param string The combined comments.
	 */
	void createBulkComment(String string);

	/**
	 * Create a discussion on the diff on the line the violation occurred.
	 *
	 * @param file The file in which the violation occurred.
	 * @param discussionContent The text of the discussion.
	 * @param newLine The line number after the patch.
	 * @param oldLine The line number before the patch.
	 */
	void createDiffDiscussion(ChangedFile file, String discussionContent,
			Integer newLine, Integer oldLine);

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
	 * Returns if comments should be made on to the diff.
	 *
	 * @return <code>true</code> if the comments containing the violations
	 *         should be posted on the diff, <code>false</code> otherwise.
	 */
	boolean shouldCommentOnTheDiff();

	boolean shouldKeepOldComments();

	Optional<String> findCommentTemplate();
}
