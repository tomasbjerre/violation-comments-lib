package se.bjurr.violations.comments.lib.model;

import java.util.List;

public interface CommentsProvider {
 void createCommentWithAllSingleFileComments(String string);

 void createSingleFileComment(ChangedFile file, Integer line, String comment);

 List<Comment> getComments();

 List<ChangedFile> getFiles();

 void removeComments(List<Comment> comments);

 boolean shouldComment(ChangedFile changedFile, Integer line);
}
