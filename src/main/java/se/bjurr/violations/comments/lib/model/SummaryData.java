package se.bjurr.violations.comments.lib.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;

/** Aggregated statistics about a set of violations, exposed to the summary comment template. */
public class SummaryData {

  private final int violationsCount;
  private final int filesCount;
  private final List<ViolationData> violations;
  private final List<SeverityCount> perSeverity;
  private final List<ReporterCount> perReporter;

  public SummaryData(final Set<Violation> violations) {
    this.violations = new ArrayList<>();
    final Set<String> files = new TreeSet<>();
    final Map<SEVERITY, Integer> severityCounts = new LinkedHashMap<>();
    final Map<String, Integer> reporterCounts = new LinkedHashMap<>();
    for (final Violation violation : violations) {
      this.violations.add(new ViolationData(violation));
      files.add(violation.getFile());
      severityCounts.merge(violation.getSeverity(), 1, Integer::sum);
      reporterCounts.merge(violation.getReporter(), 1, Integer::sum);
    }
    this.violationsCount = violations.size();
    this.filesCount = files.size();

    this.perSeverity = new ArrayList<>();
    for (final Map.Entry<SEVERITY, Integer> entry : severityCounts.entrySet()) {
      this.perSeverity.add(new SeverityCount(entry.getKey(), entry.getValue()));
    }

    this.perReporter = new ArrayList<>();
    for (final Map.Entry<String, Integer> entry : reporterCounts.entrySet()) {
      this.perReporter.add(new ReporterCount(entry.getKey(), entry.getValue()));
    }
  }

  public int getViolationsCount() {
    return this.violationsCount;
  }

  public int getFilesCount() {
    return this.filesCount;
  }

  public List<ViolationData> getViolations() {
    return this.violations;
  }

  public List<SeverityCount> getPerSeverity() {
    return this.perSeverity;
  }

  public List<ReporterCount> getPerReporter() {
    return this.perReporter;
  }

  public static class SeverityCount {
    private final SEVERITY severity;
    private final int count;

    public SeverityCount(final SEVERITY severity, final int count) {
      this.severity = severity;
      this.count = count;
    }

    public SEVERITY getSeverity() {
      return this.severity;
    }

    public int getCount() {
      return this.count;
    }
  }

  public static class ReporterCount {
    private final String reporter;
    private final int count;

    public ReporterCount(final String reporter, final int count) {
      this.reporter = reporter;
      this.count = count;
    }

    public String getReporter() {
      return this.reporter;
    }

    public int getCount() {
      return this.count;
    }
  }
}
