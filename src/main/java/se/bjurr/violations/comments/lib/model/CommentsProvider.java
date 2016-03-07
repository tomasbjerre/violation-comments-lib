package se.bjurr.violations.comments.lib.model;

import java.util.List;

public interface CommentsProvider {
 List<Comment> getComments();

 List<ChangedFile> getFiles();

 void createSingleFileComment(ChangedFile file, Integer line, String comment);

 void removeComments(List<Comment> comments);

 void createCommentWithAllSingleFileComments(String string);
}
