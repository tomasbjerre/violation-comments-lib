package se.bjurr.violations.comments.lib;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT_ACC;
import static se.bjurr.violations.comments.lib.CommentsCreator.createComments;
import static se.bjurr.violations.comments.lib.ViolationRenderer.createSingleFileCommentContent;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.ANDROIDLINT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Utils;

public class CommentsCreatorTest {
  private List<Comment> existingComments;
  private boolean shouldKeepOldComments = false;
  private Integer maxNumberOfViolations = null;
  private boolean commentOnlyChangedFiles = true;
  private final CommentsProvider commentsProvider =
      new CommentsProvider() {

        @Override
        public void createComment(final String string) {
          CommentsCreatorTest.this.createCommentWithAllSingleFileComments.add(string);
        }

        @Override
        public void createSingleFileComment(
            final ChangedFile file, final Integer line, final String comment) {
          CommentsCreatorTest.this.createSingleFileComment.add(comment);
        }

        @Override
        public List<Comment> getComments() {
          return CommentsCreatorTest.this.existingComments;
        }

        @Override
        public List<ChangedFile> getFiles() {
          return CommentsCreatorTest.this.files;
        }

        @Override
        public void removeComments(final List<Comment> comments) {
          CommentsCreatorTest.this.removeComments.addAll(comments);
        }

        @Override
        public boolean shouldComment(final ChangedFile changedFile, final Integer line) {
          return true;
        }

        @Override
        public boolean shouldCreateCommentWithAllSingleFileComments() {
          return CommentsCreatorTest.this.shouldCreateCommentWithAllSingleFileComments;
        }

        @Override
        public boolean shouldCreateSingleFileComment() {
          return CommentsCreatorTest.this.shouldCreateSingleFileComment;
        }

        @Override
        public Optional<String> findCommentTemplate() {
          return Optional.ofNullable(CommentsCreatorTest.this.commentTemplate);
        }

        @Override
        public boolean shouldKeepOldComments() {
          return CommentsCreatorTest.this.shouldKeepOldComments;
        }

        @Override
        public Integer getMaxNumberOfViolations() {
          return CommentsCreatorTest.this.maxNumberOfViolations;
        }

        @Override
        public Integer getMaxCommentSize() {
          return CommentsCreatorTest.this.maxCommentSize;
        }

        @Override
        public boolean shouldCommentOnlyChangedFiles() {
          return CommentsCreatorTest.this.commentOnlyChangedFiles;
        }
      };
  private List<String> createCommentWithAllSingleFileComments;
  private List<String> createSingleFileComment;
  private List<ChangedFile> files;
  private Integer maxCommentSize;
  private List<Comment> removeComments;
  private boolean shouldCreateCommentWithAllSingleFileComments = true;
  private boolean shouldCreateSingleFileComment = true;
  private String commentTemplate = null;
  private Set<Violation> violations;
  private final ViolationsLogger logger =
      new ViolationsLogger() {
        @Override
        public void log(final Level level, final String string) {
          Logger.getLogger(ViolationsLogger.class.getSimpleName()).log(level, string);
        }

        @Override
        public void log(final Level level, final String string, final Throwable t) {
          Logger.getLogger(ViolationsLogger.class.getSimpleName()).log(level, string, t);
        }
      };

  private String asFile(final String string) throws Exception {
    return Utils.toString(Utils.getResource(string));
  }

  @Before
  public void before() {
    this.createCommentWithAllSingleFileComments = new ArrayList<>();
    this.createSingleFileComment = new ArrayList<>();
    this.existingComments = new ArrayList<>();
    this.files = new ArrayList<>();
    this.removeComments = new ArrayList<>();
    this.violations = new TreeSet<>();
    this.commentOnlyChangedFiles = true;
  }

  private final Violation violation1 =
      violationBuilder() //
          .setParser(ANDROIDLINT) //
          .setReporter("ToolUsed") //
          .setStartLine(1) //
          .setSeverity(ERROR) //
          .setFile("file1") //
          .setSource("File") //
          .setMessage("1111111111") //
          .build();
  private final Violation violation2 =
      violationBuilder() //
          .setParser(ANDROIDLINT) //
          .setStartLine(1) //
          .setSeverity(ERROR) //
          .setFile("file1") //
          .setMessage("2222222222") //
          .build();
  private final Violation violation3 =
      violationBuilder() //
          .setParser(ANDROIDLINT) //
          .setStartLine(1) //
          .setSeverity(ERROR) //
          .setFile("file2") //
          .setMessage("3333333333") //
          .build();
  private final Violation violation4 =
      violationBuilder() //
          .setParser(ANDROIDLINT) //
          .setStartLine(1) //
          .setSeverity(ERROR) //
          .setFile("file1") //
          .setMessage("one\ntwo") //
          .build();
  private final List<String> specifics = new ArrayList<>();
  private String type;
  private String identifier;

  @Test
  public void testShouldKeepOldCommentsFalse() throws Exception {
    this.violations.add(this.violation1);
    this.violations.add(this.violation2);
    this.violations.add(this.violation3);

    this.files.add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    final CommentsCreator commentsCreator =
        new CommentsCreator(this.logger, this.commentsProvider, this.violations);

    this.existingComments.add(new Comment("id1", FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(new Comment("id2", FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(
        new Comment("id3", FINGERPRINT + " " + FINGERPRINT_ACC, this.type, this.specifics));
    this.existingComments.add(new Comment("id4", "another comment", this.type, this.specifics));

    this.shouldKeepOldComments = false;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    this.shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createSingleFileComment) //
        .hasSize(3);
    assertThat(this.removeComments.get(0).getIdentifier()) //
        .isEqualTo("id3");
    assertThat(this.removeComments.get(1).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(this.removeComments.get(2).getIdentifier()) //
        .isEqualTo("id2");
    assertThat(this.removeComments) //
        .hasSize(3);
  }

  @Test
  public void testShouldKeepOldCommentsFalseSameAcc() throws Exception {
    this.violations.add(this.violation1);
    this.violations.add(this.violation2);
    this.violations.add(this.violation3);

    this.files.add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    final CommentsCreator commentsCreator =
        new CommentsCreator(this.logger, this.commentsProvider, this.violations);

    this.existingComments.add(new Comment("id1", FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(new Comment("id2", FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(
        new Comment(
            "id3",
            ViolationRenderer.getAccumulatedComments(
                    this.violations,
                    this.files,
                    this.commentsProvider.findCommentTemplate().orElse(null),
                    this.maxCommentSize)
                .get(0),
            this.type,
            this.specifics));
    this.existingComments.add(new Comment("id4", "another comment", this.type, this.specifics));

    this.shouldKeepOldComments = false;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    this.shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(0);
    assertThat(this.createSingleFileComment) //
        .hasSize(3);
    assertThat(this.removeComments.get(0).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(this.removeComments.get(1).getIdentifier()) //
        .isEqualTo("id2");
    assertThat(this.removeComments) //
        .hasSize(2);
  }

  @Test
  public void testShouldKeepOldCommentsFalseOneSameViolation() throws Exception {
    this.violations.add(this.violation1);
    this.violations.add(this.violation2);
    this.violations.add(this.violation3);

    final ChangedFile file1 = new ChangedFile("file1", null);
    this.files.add(file1);
    final ChangedFile file2 = new ChangedFile("file2", null);
    this.files.add(file2);

    final CommentsCreator commentsCreator =
        new CommentsCreator(this.logger, this.commentsProvider, this.violations);

    this.existingComments.add(new Comment("id1", FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(
        new Comment(
            "id2",
            createSingleFileCommentContent(file1, this.violation1, null),
            this.type,
            this.specifics));
    this.existingComments.add(
        new Comment("id3", FINGERPRINT + FINGERPRINT_ACC, this.type, this.specifics));
    this.existingComments.add(new Comment("id4", "another comment", this.type, this.specifics));

    this.shouldKeepOldComments = false;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    this.shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);
    assertThat(this.removeComments.get(0).getIdentifier()) //
        .isEqualTo("id3");
    assertThat(this.removeComments.get(1).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(this.removeComments) //
        .hasSize(2);
  }

  @Test
  public void testShouldKeepOldCommentsTrue() throws Exception {
    this.violations.add(this.violation1);
    this.violations.add(this.violation2);
    this.violations.add(this.violation3);

    this.files.add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    this.existingComments.add(new Comment(this.identifier, FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(new Comment(this.identifier, FINGERPRINT, this.type, this.specifics));
    this.existingComments.add(
        new Comment(
            this.identifier, FINGERPRINT + " " + FINGERPRINT_ACC, this.type, this.specifics));
    this.existingComments.add(
        new Comment(this.identifier, "another comment", this.type, this.specifics));

    this.shouldKeepOldComments = true;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    this.shouldCreateSingleFileComment = true;

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createSingleFileComment) //
        .hasSize(3);
    assertThat(this.removeComments) //
        .hasSize(0);
  }

  @Test
  public void testMarkdown() throws Exception {
    this.violations.add(this.violation1);
    this.violations.add(this.violation2);
    this.violations.add(this.violation3);

    this.files.add(new ChangedFile("file1", null));

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments.get(0).trim()) //
        .isEqualTo(this.asFile("testMarkdownCommentWithSource.md"));
    assertThat(this.createSingleFileComment.get(0).trim())
        //
        .isEqualTo(this.asFile("testMarkdownSingleFileCommentWithSource.md"));
    assertThat(this.createSingleFileComment.get(1).trim())
        //
        .isEqualTo(this.asFile("testMarkdownSingleFileCommentWithoutSource.md"));
  }

  @Test
  public void testNewLine() throws Exception {
    this.violations.add(this.violation4);
    this.files.add(new ChangedFile("file1", null));
    this.commentTemplate = "{{{violation.message}}}";

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createSingleFileComment.get(0).trim())
        .isEqualTo(this.asFile("testMarkdownSingleFileCommentWithNewLine.md"));
  }

  @Test
  public void testMarkdownCustomReporter() throws Exception {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setReporter("ToolUsed") //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setSource("File") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    this.files.add(new ChangedFile("file1", null));

    Utils.setReporter(this.violations, "CustomReporter");

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments.get(0).trim()) //
        .isEqualTo(this.asFile("testMarkdownCustomReporter1.md"));
    assertThat(this.createSingleFileComment.get(0).trim()) //
        .isEqualTo(this.asFile("testMarkdownCustomReporter2.md"));
    assertThat(this.createSingleFileComment.get(1).trim()) //
        .isEqualTo(this.asFile("testMarkdownCustomReporter3.md"));
  }

  @Test
  public void testWithBigComment() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file3") //
            .setMessage("3333333333") //
            .build());

    this.files.add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    this.maxCommentSize = 10;

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(3);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);
    assertThat(this.removeComments) //
        .isEmpty();
    assertThat(this.existingComments) //
        .isEmpty();
  }

  @Test
  public void testWithNoComments() {
    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .isEmpty();
    assertThat(this.createSingleFileComment) //
        .isEmpty();
    assertThat(this.removeComments) //
        .isEmpty();
    assertThat(this.existingComments) //
        .isEmpty();
    assertThat(this.files) //
        .isEmpty();
  }

  @Test
  public void testWithOnlyOne() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());

    this.shouldCreateSingleFileComment = false;
    this.shouldCreateCommentWithAllSingleFileComments = true;

    this.files.add(new ChangedFile("file1", null));

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createSingleFileComment) //
        .hasSize(0);
  }

  @Test
  public void testWithOnlySingle() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());

    this.shouldCreateSingleFileComment = true;
    this.shouldCreateCommentWithAllSingleFileComments = false;

    this.files.add(new ChangedFile("file1", null));

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(0);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);
  }

  @Test
  public void testWithSmallComment() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file3") //
            .setMessage("3333333333") //
            .build());

    this.files.add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);
    assertThat(this.removeComments) //
        .isEmpty();
  }

  @Test
  public void testThatNumberOfCommentsCanBeLimited() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    this.commentsProvider.getFiles().add(new ChangedFile("file1", null));
    this.files.add(new ChangedFile("file2", null));

    createComments(this.logger, this.violations, this.commentsProvider);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);

    this.createCommentWithAllSingleFileComments.clear();
    this.createSingleFileComment.clear();
    this.maxNumberOfViolations = 0;
    createComments(this.logger, this.violations, this.commentsProvider);
    assertThat(this.createSingleFileComment) //
        .hasSize(0);

    this.createCommentWithAllSingleFileComments.clear();
    this.createSingleFileComment.clear();
    this.maxNumberOfViolations = 1;
    createComments(this.logger, this.violations, this.commentsProvider);
    assertThat(this.createSingleFileComment) //
        .hasSize(1);

    this.createCommentWithAllSingleFileComments.clear();
    this.createSingleFileComment.clear();
    this.maxNumberOfViolations = 2;
    createComments(this.logger, this.violations, this.commentsProvider);
    assertThat(this.createSingleFileComment) //
        .hasSize(2);
  }

  @Test(expected = IllegalStateException.class)
  public void testCannotCommentSingleFilesWhenCommentingEverything() {
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());

    this.files.add(new ChangedFile("file1", null));

    this.commentOnlyChangedFiles = false;
    this.shouldCreateSingleFileComment = true;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    createComments(this.logger, this.violations, this.commentsProvider);
  }

  @Test
  public void testUsingFileFromViolationWhenCommentingEverything() {
    final String file1Name = "file1/is/changed.java";
    final String file2Name = "file2/from/violation.java";
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile(file1Name) //
            .setMessage("1111111111") //
            .build());
    this.violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile(file2Name) //
            .setMessage("1111111111") //
            .build());

    this.files.add(new ChangedFile(file1Name, null));

    this.commentOnlyChangedFiles = false;
    this.shouldCreateSingleFileComment = false;
    this.shouldCreateCommentWithAllSingleFileComments = true;
    createComments(this.logger, this.violations, this.commentsProvider);

    assertThat(this.createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(this.createCommentWithAllSingleFileComments.get(0))
        //
        .startsWith("Found 2 violations")
        .contains(file2Name, file1Name);
    assertThat(this.createSingleFileComment) //
        .isEmpty();
    assertThat(this.removeComments) //
        .isEmpty();
  }
}
