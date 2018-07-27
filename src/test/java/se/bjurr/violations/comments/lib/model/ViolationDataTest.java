package se.bjurr.violations.comments.lib.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.CHECKSTYLE;

import org.junit.Test;

public class ViolationDataTest {

  @Test
  public void testThatFileNameCanBeExtracted() {
    final ViolationData violationData =
        new ViolationData(
            violationBuilder() //
                .setParser(CHECKSTYLE) //
                .setFile("c:\\path\\to\\file1.xml") //
                .setMessage("message") //
                .setSeverity(ERROR) //
                .setStartLine(1) //
                .build());
    assertThat(violationData.getFileName()) //
        .isEqualTo("file1.xml");
  }

  @Test
  public void testThatFileNameCanBeExtractedWhenNoSlashes() {
    ;
    final ViolationData violationData =
        new ViolationData(
            violationBuilder() //
                .setParser(CHECKSTYLE) //
                .setFile("file2.xml") //
                .setMessage("message") //
                .setSeverity(ERROR) //
                .setStartLine(1) //
                .build());
    assertThat(violationData.getFileName()) //
        .isEqualTo("file2.xml");
  }
}
