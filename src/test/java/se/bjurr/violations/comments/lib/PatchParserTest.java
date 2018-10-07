package se.bjurr.violations.comments.lib;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.junit.Test;

public class PatchParserTest {
  private static Logger LOG = Logger.getLogger(PatchParser.class.getSimpleName());

  private static final String NEW_DIFF =
      "@@ -1,6 +1,6 @@\n <html>\n  <head></head>\n <body>\n-<font>\n+<font> \n </body> \n </html>";
  private static final String CHANGED_DIFF =
      " @@ -1,4 +1,5 @@\n .klass {\n  font-size: 14px;\n+ \n  font-size: 14px;\n }";
  private static final String CHANGED_DIFF_2 =
      "@@ -6,6 +6,16 @@\n  void npe(String a, String b) {\n   if (a == null) {\n    System.out.println();\n+   System.out.println();\n+  } else {\n+\n+  }\n+  a.length();\n+ }\n+\n+ void npe2(String a, String b) {\n+  if (a == null) {\n+   System.out.println();\n   } else {\n \n   }\n@@ -14,6 +24,6 @@ void npe(String a, String b) {\n \n  @Override\n  public boolean equals(Object obj) {\n-  return true;\n+  return false;\n  }\n }";

  @Test
  public void testThatChangedContentCanBeCommented() {
    assertThat(findLineToComment("filename", "patch", 1)) //
        .isNull();
  }

  @Test
  public void testThatChangedContentCanBeCommentedNewFile() {
    assertThat(findLineToComment("filename", NEW_DIFF, 1)) //
        .isEqualTo(1);

    assertThat(findLineToComment("filename", NEW_DIFF, 1)) //
        .isEqualTo(1);

    assertThat(findLineToComment("filename", NEW_DIFF, 5)) //
        .isEqualTo(6);

    assertThat(findLineToComment("filename", NEW_DIFF, 5)) //
        .isEqualTo(6);
  }

  @Test
  public void testThatChangedContentCanBeCommentedChangedFile() {
    assertThat(findLineToComment("filename", CHANGED_DIFF, 1)) //
        .isEqualTo(2);

    assertThat(findLineToComment("filename", CHANGED_DIFF, 1)) //
        .isEqualTo(2);

    assertThat(findLineToComment("filename", CHANGED_DIFF, 4)) //
        .isEqualTo(5);

    assertThat(findLineToComment("filename", CHANGED_DIFF, 4)) //
        .isEqualTo(5);
  }

  @Test
  public void testThatChangedContentCanBeCommentedChangedPartsOfFile() {
    assertThat(findLineToComment("filename", CHANGED_DIFF_2, 6)) //
        .isEqualTo(1);

    assertThat(findLineToComment("filename", CHANGED_DIFF_2, 8)) //
        .isEqualTo(3);

    assertThat(findLineToComment("filename", CHANGED_DIFF_2, 14)) //
        .isEqualTo(9);

    assertThat(findLineToComment("filename", CHANGED_DIFF_2, 21)) //
        .isEqualTo(16);
  }

  private Integer findLineToComment(String filename, String patch, int commentLint) {
    List<String> list = new ArrayList<>();
    list.add(patch);
    return PatchParser // .
        .findLineToComment(patch, commentLint) //
        .orElse(null);
  }

  @Test
  public void testThatLineTableCanBeRetrieved() {
    String patch =
        "--- a/src/main/java/se/bjurr/violations/lib/example/OtherClass.java\n+++ b/src/main/java/se/bjurr/violations/lib/example/OtherClass.java\n@@ -4,12 +4,15 @@ package se.bjurr.violations.lib.example;\n  * No ending dot\n  */\n public class OtherClass {\n- public static String CoNstANT = \"yes\";\n+ public static String CoNstANT = \"yes\"; \n \n  public void myMethod() {\n   if (CoNstANT.equals(\"abc\")) {\n \n   }\n+  if (CoNstANT.equals(\"abc\")) {\n+\n+  }\n  }\n \n  @Override\n";
    String[] diffLines = patch.split("\n");
    for (int i = 0; i < diffLines.length; i++) {
      LOG.info(i + 1 + " | " + diffLines[i]);
    }
    final Map<Integer, Optional<Integer>> map = PatchParser.getLineTranslation(patch);
    for (Map.Entry<Integer, Optional<Integer>> e : map.entrySet()) {
      LOG.info(e.getKey() + " : " + e.getValue().orElse(null));
    }

    assertThat(map.get(6).orElse(null)) //
        .isEqualTo(6);
    assertThat(map.get(7).orElse(null)) //
        .isNull();
    assertThat(map.get(8).orElse(null)) //
        .isEqualTo(8);

    assertThat(map.get(12).orElse(null)) //
        .isEqualTo(12);
    assertThat(map.get(13).orElse(null)) //
        .isNull();
    assertThat(map.get(14).orElse(null)) //
        .isNull();
    assertThat(map.get(15).orElse(null)) //
        .isNull();
    assertThat(map.get(16).orElse(null)) //
        .isEqualTo(13);
  }
}
