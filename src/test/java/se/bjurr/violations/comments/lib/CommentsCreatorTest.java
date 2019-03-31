package se.bjurr.violations.comments.lib;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Optional.empty;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.Comment;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Utils;

public class CommentsCreatorTest {
  private List<Comment> existingComments;
  private boolean shouldKeepOldComments = false;
  private int maxNumberOfComments = MAX_VALUE;
  private final CommentsProvider commentsProvider =
      new CommentsProvider() {
        @Override
        public void createCommentWithAllSingleFileComments(final String string) {
          createCommentWithAllSingleFileComments.add(string);
        }

        @Override
        public void createSingleFileComment(
            final ChangedFile file, final Integer line, final String comment) {
          createSingleFileComment.add(comment);
        }

        @Override
        public List<Comment> getComments() {
          return existingComments;
        }

        @Override
        public List<ChangedFile> getFiles() {
          return files;
        }

        @Override
        public void removeComments(final List<Comment> comments) {
          removeComments.addAll(comments);
        }

        @Override
        public boolean shouldComment(final ChangedFile changedFile, final Integer line) {
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
        public Optional<String> findCommentTemplate() {
          return empty();
        }

        @Override
        public boolean shouldKeepOldComments() {
          return shouldKeepOldComments;
        }

        @Override
        public int getMaxNumberOfComments() {
          return maxNumberOfComments;
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
    createCommentWithAllSingleFileComments = new ArrayList<>();
    createSingleFileComment = new ArrayList<>();
    existingComments = new ArrayList<>();
    files = new ArrayList<>();
    removeComments = new ArrayList<>();
    violations = new ArrayList<>();
    maxCommentSize = MAX_VALUE;
    maxNumberOfComments = MAX_VALUE;
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
  private final List<String> specifics = new ArrayList<>();
  private String type;
  private String identifier;

  @Test
  public void testShouldKeepOldCommentsFalse() throws Exception {
    violations.add(violation1);
    violations.add(violation2);
    violations.add(violation3);

    files.add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    final CommentsCreator commentsCreator =
        new CommentsCreator(logger, commentsProvider, violations, maxCommentSize);

    existingComments.add(new Comment("id1", FINGERPRINT, type, specifics));
    existingComments.add(new Comment("id2", FINGERPRINT, type, specifics));
    existingComments.add(new Comment("id3", FINGERPRINT + " " + FINGERPRINT_ACC, type, specifics));
    existingComments.add(new Comment("id4", "another comment", type, specifics));

    shouldKeepOldComments = false;
    shouldCreateCommentWithAllSingleFileComments = true;
    shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(createSingleFileComment) //
        .hasSize(3);
    assertThat(removeComments.get(0).getIdentifier()) //
        .isEqualTo("id3");
    assertThat(removeComments.get(1).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(removeComments.get(2).getIdentifier()) //
        .isEqualTo("id2");
    assertThat(removeComments) //
        .hasSize(3);
  }

  @Test
  public void testShouldKeepOldCommentsFalseSameAcc() throws Exception {
    violations.add(violation1);
    violations.add(violation2);
    violations.add(violation3);

    files.add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    final CommentsCreator commentsCreator =
        new CommentsCreator(logger, commentsProvider, violations, maxCommentSize);

    existingComments.add(new Comment("id1", FINGERPRINT, type, specifics));
    existingComments.add(new Comment("id2", FINGERPRINT, type, specifics));
    existingComments.add(
        new Comment(
            "id3",
            ViolationRenderer.getAccumulatedComments(
                    violations,
                    files,
                    commentsProvider.findCommentTemplate().orElse(null),
                    maxCommentSize)
                .get(0),
            type,
            specifics));
    existingComments.add(new Comment("id4", "another comment", type, specifics));

    shouldKeepOldComments = false;
    shouldCreateCommentWithAllSingleFileComments = true;
    shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(0);
    assertThat(createSingleFileComment) //
        .hasSize(3);
    assertThat(removeComments.get(0).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(removeComments.get(1).getIdentifier()) //
        .isEqualTo("id2");
    assertThat(removeComments) //
        .hasSize(2);
  }

  @Test
  public void testShouldKeepOldCommentsFalseOneSameViolation() throws Exception {
    violations.add(violation1);
    violations.add(violation2);
    violations.add(violation3);

    final ChangedFile file1 = new ChangedFile("file1", null);
    files.add(file1);
    final ChangedFile file2 = new ChangedFile("file2", null);
    files.add(file2);

    final CommentsCreator commentsCreator =
        new CommentsCreator(logger, commentsProvider, violations, maxCommentSize);

    existingComments.add(new Comment("id1", FINGERPRINT, type, specifics));
    existingComments.add(
        new Comment(
            "id2", createSingleFileCommentContent(file1, violation1, null), type, specifics));
    existingComments.add(new Comment("id3", FINGERPRINT + FINGERPRINT_ACC, type, specifics));
    existingComments.add(new Comment("id4", "another comment", type, specifics));

    shouldKeepOldComments = false;
    shouldCreateCommentWithAllSingleFileComments = true;
    shouldCreateSingleFileComment = true;

    commentsCreator.createComments();

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(createSingleFileComment) //
        .hasSize(2);
    assertThat(removeComments.get(0).getIdentifier()) //
        .isEqualTo("id3");
    assertThat(removeComments.get(1).getIdentifier()) //
        .isEqualTo("id1");
    assertThat(removeComments) //
        .hasSize(2);
  }

  @Test
  public void testShouldKeepOldCommentsTrue() throws Exception {
    violations.add(violation1);
    violations.add(violation2);
    violations.add(violation3);

    files.add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    existingComments.add(new Comment(identifier, FINGERPRINT, type, specifics));
    existingComments.add(new Comment(identifier, FINGERPRINT, type, specifics));
    existingComments.add(
        new Comment(identifier, FINGERPRINT + " " + FINGERPRINT_ACC, type, specifics));
    existingComments.add(new Comment(identifier, "another comment", type, specifics));

    shouldKeepOldComments = true;
    shouldCreateCommentWithAllSingleFileComments = true;
    shouldCreateSingleFileComment = true;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(createSingleFileComment) //
        .hasSize(3);
    assertThat(removeComments) //
        .hasSize(0);
  }

  @Test
  public void testMarkdown() throws Exception {
    violations.add(violation1);
    violations.add(violation2);
    violations.add(violation3);

    files.add(new ChangedFile("file1", null));

    maxCommentSize = MAX_VALUE;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments.get(0).trim()) //
        .isEqualTo(asFile("testMarkdownCommentWithSource.md"));
    assertThat(createSingleFileComment.get(0).trim()) //
        .isEqualTo(asFile("testMarkdownSingleFileCommentWithSource.md"));
    assertThat(createSingleFileComment.get(1).trim()) //
        .isEqualTo(asFile("testMarkdownSingleFileCommentWithoutSource.md"));
  }

  @Test
  public void testMarkdownCustomReporter() throws Exception {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setReporter("ToolUsed") //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setSource("File") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    files.add(new ChangedFile("file1", null));

    maxCommentSize = MAX_VALUE;

    Utils.setReporter(violations, "CustomReporter");

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments.get(0).trim()) //
        .isEqualTo(asFile("testMarkdownCustomReporter1.md"));
    assertThat(createSingleFileComment.get(0).trim()) //
        .isEqualTo(asFile("testMarkdownCustomReporter2.md"));
    assertThat(createSingleFileComment.get(1).trim()) //
        .isEqualTo(asFile("testMarkdownCustomReporter3.md"));
  }

  @Test
  public void testWithBigComment() {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file3") //
            .setMessage("3333333333") //
            .build());

    files.add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    maxCommentSize = 10;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(3);
    assertThat(createSingleFileComment) //
        .hasSize(2);
    assertThat(removeComments) //
        .isEmpty();
    assertThat(existingComments) //
        .isEmpty();
  }

  @Test
  public void testWithNoComments() {
    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .isEmpty();
    assertThat(createSingleFileComment) //
        .isEmpty();
    assertThat(removeComments) //
        .isEmpty();
    assertThat(existingComments) //
        .isEmpty();
    assertThat(files) //
        .isEmpty();
  }

  @Test
  public void testWithOnlyOne() {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());

    shouldCreateSingleFileComment = false;
    shouldCreateCommentWithAllSingleFileComments = true;

    files.add(new ChangedFile("file1", null));

    maxCommentSize = MAX_VALUE;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(createSingleFileComment) //
        .hasSize(0);
  }

  @Test
  public void testWithOnlySingle() {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("2222222222") //
            .build());

    shouldCreateSingleFileComment = true;
    shouldCreateCommentWithAllSingleFileComments = false;

    files.add(new ChangedFile("file1", null));

    maxCommentSize = MAX_VALUE;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(0);
    assertThat(createSingleFileComment) //
        .hasSize(2);
  }

  @Test
  public void testWithSmallComment() {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file3") //
            .setMessage("3333333333") //
            .build());

    files.add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    maxCommentSize = MAX_VALUE;

    createComments(logger, violations, maxCommentSize, commentsProvider);

    assertThat(createCommentWithAllSingleFileComments) //
        .hasSize(1);
    assertThat(createSingleFileComment) //
        .hasSize(2);
    assertThat(removeComments) //
        .isEmpty();
  }

  @Test
  public void testThatNumberOfCommentsCanBeLimited() {
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setMessage("1111111111") //
            .build());
    violations.add(
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file2") //
            .setMessage("2222222222") //
            .build());

    commentsProvider.getFiles().add(new ChangedFile("file1", null));
    files.add(new ChangedFile("file2", null));

    createComments(logger, violations, maxCommentSize, commentsProvider);
    assertThat(createSingleFileComment) //
        .hasSize(2);

    createCommentWithAllSingleFileComments.clear();
    createSingleFileComment.clear();
    this.maxNumberOfComments = 0;
    createComments(logger, violations, maxCommentSize, commentsProvider);
    assertThat(createSingleFileComment) //
        .hasSize(0);

    createCommentWithAllSingleFileComments.clear();
    createSingleFileComment.clear();
    this.maxNumberOfComments = 1;
    createComments(logger, violations, maxCommentSize, commentsProvider);
    assertThat(createSingleFileComment) //
        .hasSize(1);

    createCommentWithAllSingleFileComments.clear();
    createSingleFileComment.clear();
    this.maxNumberOfComments = 2;
    createComments(logger, violations, maxCommentSize, commentsProvider);
    assertThat(createSingleFileComment) //
        .hasSize(2);
  }
}
