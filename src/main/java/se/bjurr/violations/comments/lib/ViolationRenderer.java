package se.bjurr.violations.comments.lib;

import static java.util.Optional.ofNullable;
import static se.bjurr.violations.comments.lib.ChangedFileUtils.findChangedFile;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT_ACC;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.ViolationData;
import se.bjurr.violations.lib.model.Violation;

public class ViolationRenderer {
  private static final String DEFAULT_VIOLATION_TEMPLATE_MUSTACH =
      "default-violation-template.mustach";

  static List<String> getAccumulatedComments(
      final List<Violation> violations,
      final List<ChangedFile> files,
      final String commentTemplate,
      final Integer maxCommentSize) {
    final List<String> partitions = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    sb.append("Found " + violations.size() + " violations:\n\n");
    for (final Violation violation : violations) {
      final Optional<ChangedFile> changedFile = findChangedFile(files, violation);
      final String singleFileCommentContent =
          createSingleFileCommentContent(changedFile.get(), violation, commentTemplate);
      if (maxCommentSize != null) {
        if (sb.length() + singleFileCommentContent.length() >= maxCommentSize) {
          sb.append(" *" + FINGERPRINT_ACC + "*");
          partitions.add(sb.toString());
          sb = new StringBuilder();
        }
      }
      sb.append(singleFileCommentContent + "\n");
    }
    sb.append(" *" + FINGERPRINT_ACC + "*");
    partitions.add(sb.toString());
    return partitions;
  }

  static String createSingleFileCommentContent(
      final ChangedFile changedFile, final Violation violation, final String commentTemplate) {
    final MustacheFactory mf = new DefaultMustacheFactory();
    Reader templateReader = null;
    final Optional<String> commentTemplateOpt = ofNullable(commentTemplate);
    if (commentTemplateOpt.isPresent() && !commentTemplateOpt.get().isEmpty()) {
      templateReader = new StringReader(commentTemplateOpt.get());
    } else {
      templateReader = mf.getReader(DEFAULT_VIOLATION_TEMPLATE_MUSTACH);
    }
    final Mustache mustache = mf.compile(templateReader, "Violation Template");
    final Writer writer = new StringWriter();
    final Map<String, Object> context = new HashMap<>();
    context.put("violation", new ViolationData(violation));
    context.put("changedFile", changedFile);
    final List<Object> scopes = new ArrayList<>();
    scopes.add(context);
    mustache.execute(writer, scopes);
    return writer.toString() + "\n\n*" + FINGERPRINT + "* *<" + identifier(violation) + ">*";
  }

  static String identifier(final Violation violation) {
    // The letter makes it invisible in GitHub.
    return "a" + violation.toString().replaceAll("[^a-zA-Z0-9]", "").hashCode();
  }
}
