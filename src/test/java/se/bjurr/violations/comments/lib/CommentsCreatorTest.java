package se.bjurr.violations.comments.lib;

import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.comments.lib.CommentsCreator.createComments;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Reporter.ANDROIDLINT;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.comments.lib.model.CommentsProvider;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Optional;
import se.bjurr.violations.lib.util.Utils;

public class CommentsCreatorTest {
 private List<Comment> comments;
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

  @Override
  public boolean shouldComment(ChangedFile changedFile, Integer line) {
   return true;
  }

  @Override
  public boolean shouldCreateCommentWithAllSingleFileComments() {
   return shouldCreateCommentWithAllSingleFileComments;
  }

  @Override
  public boolean shouldCreateSingleFileComment() {
   return shouldCreateSingleFileComment;
  }

  @Override
  public Optional<String> findCommentFormat(ChangedFile changedFile, Violation violation) {
    return Optional.absent();
  }
 };
 private List<String> createCommentWithAllSingleFileComments;
 private List<String> createSingleFileComment;
 private List<ChangedFile> files;
 private Integer maxCommentSize;
 private List<Comment> removeComments;
 private boolean shouldCreateCommentWithAllSingleFileComments = true;
 private boolean shouldCreateSingleFileComment = true;
 private List<Violation> violations;

 private String asFile(String string) throws Exception {
  return Utils.toString(Utils.getResource(string));
 }

 @Before
 public void before() {
  createCommentWithAllSingleFileComments = new ArrayList<>();
  createSingleFileComment = new ArrayList<>();
  comments = new ArrayList<>();
  files = new ArrayList<>();
  removeComments = new ArrayList<>();
  violations = new ArrayList<>();
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
 public void testWithOnlyOne() {
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
    .setFile("file1")//
    .setMessage("2222222222")//
    .build());

  shouldCreateSingleFileComment = false;
  shouldCreateCommentWithAllSingleFileComments = true;

  files.add(new ChangedFile("file1", null));

  maxCommentSize = MAX_VALUE;

  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments)//
    .hasSize(1);
  assertThat(createSingleFileComment)//
    .hasSize(0);
 }

 @Test
 public void testWithOnlySingle() {
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
    .setFile("file1")//
    .setMessage("2222222222")//
    .build());

  shouldCreateSingleFileComment = true;
  shouldCreateCommentWithAllSingleFileComments = false;

  files.add(new ChangedFile("file1", null));

  maxCommentSize = MAX_VALUE;

  createComments(commentsProvider, violations, maxCommentSize);

  assertThat(createCommentWithAllSingleFileComments)//
    .hasSize(0);
  assertThat(createSingleFileComment)//
    .hasSize(2);
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
