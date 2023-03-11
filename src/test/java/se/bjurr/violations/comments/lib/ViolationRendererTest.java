package se.bjurr.violations.comments.lib;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.ANDROIDLINT;

import java.util.ArrayList;
import org.junit.Test;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.lib.model.Violation;

public class ViolationRendererTest {

  @Test
  public void testHelper() {
    final ChangedFile changedFile = new ChangedFile("the-filename", new ArrayList<>());

    final Violation violation =
        violationBuilder() //
            .setParser(ANDROIDLINT) //
            .setReporter("ToolUsed") //
            .setStartLine(1) //
            .setSeverity(ERROR) //
            .setFile("file1") //
            .setSource("File") //
            .setMessage("jada jada jada jada jada jada jada") //
            .build();

    final String commentTemplate =
        "Message:\n{{violation.message}}\n\nSubstring:\n{{substring violation.message 0 10}}";

    final String actual =
        ViolationRenderer.createSingleFileCommentContent(changedFile, violation, commentTemplate);

    assertThat(actual)
        .isEqualTo(
            "Message:\n"
                + "jada jada jada jada jada jada jada\n"
                + "\n"
                + "Substring:\n"
                + "jada jada \n"
                + "*<this is a auto generated comment from violation-comments-lib F7F8ASD8123FSDF>* *<a-873281584>*");
  }
}
