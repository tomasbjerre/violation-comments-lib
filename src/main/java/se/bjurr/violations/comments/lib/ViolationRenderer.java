package se.bjurr.violations.comments.lib;

import static java.util.Optional.ofNullable;
import static se.bjurr.violations.comments.lib.ChangedFileUtils.findChangedFile;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT_ACC;
import static se.bjurr.violations.comments.lib.CommentsCreator.FINGERPRINT_SUMMARY;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.IfHelper;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.helper.UnlessHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.comments.lib.model.SummaryData;
import se.bjurr.violations.comments.lib.model.ViolationData;
import se.bjurr.violations.lib.model.Violation;

public class ViolationRenderer {
  private static final String DEFAULT_VIOLATION_TEMPLATE_HBS = "/default-violation-template.hbs";
  private static final String DEFAULT_SUMMARY_TEMPLATE_HBS = "/default-summary-template.hbs";

  static List<String> getAccumulatedComments(
      final Set<Violation> violations,
      final List<ChangedFile> files,
      final String commentTemplate,
      final Integer maxCommentSize) {
    final List<String> partitions = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    sb.append("Found " + violations.size() + " violations:\n\n");
    for (final Violation violation : violations) {
      final ChangedFile changedFile =
          findChangedFile(files, violation)
              .orElse(new ChangedFile(violation.getFile(), new ArrayList<>()));
      final String singleFileCommentContent =
          createSingleFileCommentContent(changedFile, violation, commentTemplate);
      if (maxCommentSize != null) {
        if (sb.length() + singleFileCommentContent.length() >= maxCommentSize) {
          sb.append(" *" + FINGERPRINT_ACC + "*");
          partitions.add(sb.toString());
          sb = new StringBuilder();
        }
      }
      sb.append(singleFileCommentContent + "\n\n");
    }
    sb.append(" *" + FINGERPRINT_ACC + "*");
    partitions.add(sb.toString());
    return partitions;
  }

  static String createSingleFileCommentContent(
      final ChangedFile changedFile, final Violation violation, final String commentTemplate) {

    final String templateContent =
        resolveTemplateContent(commentTemplate, DEFAULT_VIOLATION_TEMPLATE_HBS);
    final Template template = compile(templateContent);

    final Writer writer = new StringWriter();
    final Map<String, Object> context = new HashMap<>();
    context.put("violation", new ViolationData(violation));
    context.put("changedFile", changedFile);

    final Context templateContext = Context.newContext(context);
    try {
      template.apply(templateContext, writer);
    } catch (final IOException e) {
      throw new RuntimeException("Cannot apply template", e);
    }

    return writer.toString() + "\n*" + FINGERPRINT + "* *<" + identifier(violation) + ">*";
  }

  /**
   * Renders a single comment summarizing all violations, distinct from the per-violation comments.
   * Kept separate so it can be posted as its own comment (see {@code
   * CommentsProvider#shouldCreateSummaryComment()}).
   */
  static String createSummaryCommentContent(
      final Set<Violation> violations, final String commentTemplate) {
    final String templateContent =
        resolveTemplateContent(commentTemplate, DEFAULT_SUMMARY_TEMPLATE_HBS);
    final Template template = compile(templateContent);

    final Writer writer = new StringWriter();
    final Map<String, Object> context = new HashMap<>();
    context.put("summary", new SummaryData(violations));

    final Context templateContext = Context.newContext(context);
    try {
      template.apply(templateContext, writer);
    } catch (final IOException e) {
      throw new RuntimeException("Cannot apply template", e);
    }

    return writer.toString() + "\n*" + FINGERPRINT_SUMMARY + "*";
  }

  private static String resolveTemplateContent(
      final String commentTemplate, final String defaultResource) {
    final Optional<String> commentTemplateOpt = ofNullable(commentTemplate);
    if (commentTemplateOpt.isPresent() && !commentTemplateOpt.get().isEmpty()) {
      return commentTemplateOpt.get();
    }
    try (InputStream inputStream = ViolationRenderer.class.getResourceAsStream(defaultResource);
        BufferedReader reader =
            inputStream == null
                ? null
                : new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      if (reader == null) {
        throw new RuntimeException("Did not find " + defaultResource);
      }
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (final IOException e) {
      throw new RuntimeException("Cannot read resource " + defaultResource, e);
    }
  }

  private static Template compile(final String templateContent) {
    final Handlebars handlebars = new Handlebars();
    handlebars.setPrettyPrint(true);
    handlebars.registerHelpers(ConditionalHelpers.class);
    handlebars.registerHelpers(IfHelper.class);
    handlebars.registerHelpers(StringHelpers.class);
    handlebars.registerHelpers(UnlessHelper.class);
    try {
      return handlebars.compileInline(templateContent);
    } catch (final IOException e) {
      throw new RuntimeException("Cannot compile template: " + templateContent, e);
    }
  }

  static String identifier(final Violation violation) {
    // The letter makes it invisible in GitHub.
    return "a" + violation.toString().replaceAll("[^a-zA-Z0-9]", "").hashCode();
  }
}
