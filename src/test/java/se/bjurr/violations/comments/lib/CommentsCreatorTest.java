package se.bjurr.violations.comments.lib;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.comments.lib.CommentsCreator.createComments;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Reporter.ANDROIDLINT;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.comments.lib.model.CommentsProvider;
import se.bjurr.violations.lib.model.Violation;

public class CommentsCreatorTest {
 private List<String> createCommentWithAllSingleFileComments;
 private List<String> createSingleFileComment;
 private List<Comment> comments;
 private List<ChangedFile> files;
 private List<Comment> removeComments;
 private final CommentsProvider commentsProvider = new CommentsProvider() {

  @Override
  public void createCommentWithAllSingleFileComments(String string) {
   createCommentWithAllSingleFileComments.add(string);
  }

  @Override
  public void createSingleFileComment(ChangedFile file, Integer line, String comment) {
   createSingleFileComment.add(comment);
  }

  @Override
  public List<Comment> getComments() {
   return comments;
  }

  @Override
  public List<ChangedFile> getFiles() {
   return files;
  }

  @Override
  public void removeComments(List<Comment> comments) {
   removeComments = comments;
  }
 };
 private List<Violation> violations;
 private Integer maxCommentSize;

 private String asFile(String string) throws Exception {
  return Resources.toString(Resources.getResource(string), UTF_8).trim();
 }

 @Before
 public void before() {
  createCommentWithAllSingleFileComments = newArrayList();
  createSingleFileComment = newArrayList();
  comments = newArrayList();
  files = newArrayList();
  removeComments = newArrayList();
  violations = newArrayList();
  maxCommentSize = MAX_VALUE;
 }

 @Test
 public void testMarkdown() throws Exception {
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file1")//
    .setSource("File")//
    .setMessage("1111111111")//
    .build());
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file1")//
    .setMessage("2222222222")//
    .build());
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file2")//
    .setMessage("2222222222")//
    .build());

  files.add(new ChangedFile("file1", null));

  maxCommentSize = MAX_VALUE;

  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments.get(0).trim())//
    .isEqualTo(asFile("testMarkdownCommentWithSource.md"));
  assertThat(createSingleFileComment.get(0).trim())//
    .isEqualTo(asFile("testMarkdownSingleFileCommentWithSource.md"));
  assertThat(createSingleFileComment.get(1).trim())//
    .isEqualTo(asFile("testMarkdownSingleFileCommentWithoutSource.md"));
 }

 @Test
 public void testWithBigComment() {
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file1")//
    .setMessage("1111111111")//
    .build());
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file2")//
    .setMessage("2222222222")//
    .build());

  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file3")//
    .setMessage("3333333333")//
    .build());

  files.add(new ChangedFile("file1", null));
  files.add(new ChangedFile("file2", null));

  maxCommentSize = 10;

  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments)//
    .hasSize(3);
  assertThat(createSingleFileComment)//
    .hasSize(2);
  assertThat(removeComments)//
    .isEmpty();
  assertThat(comments)//
    .isEmpty();
 }

 @Test
 public void testWithNoComments() {
  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments)//
    .isEmpty();
  assertThat(createSingleFileComment)//
    .isEmpty();
  assertThat(removeComments)//
    .isEmpty();
  assertThat(comments)//
    .isEmpty();
  assertThat(files)//
    .isEmpty();
 }

 @Test
 public void testWithSmallComment() {
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file1")//
    .setMessage("1111111111")//
    .build());
  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file2")//
    .setMessage("2222222222")//
    .build());

  violations.add(violationBuilder()//
    .setReporter(ANDROIDLINT)//
    .setStartLine(1)//
    .setSeverity(ERROR)//
    .setFile("file3")//
    .setMessage("3333333333")//
    .build());

  files.add(new ChangedFile("file1", null));
  files.add(new ChangedFile("file2", null));

  maxCommentSize = MAX_VALUE;

  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments)//
    .hasSize(1);
  assertThat(createSingleFileComment)//
    .hasSize(2);
  assertThat(removeComments)//
    .isEmpty();
  assertThat(comments)//
    .isEmpty();
 }

}
