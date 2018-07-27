package se.bjurr.violations.comments.lib.model;

import static se.bjurr.violations.lib.util.StringUtils.escapeHTML;

import se.bjurr.violations.lib.model.Violation;

public class ViolationData extends Violation {

  private static final long serialVersionUID = 5123064911100468010L;
  private final String messageEscaped;
  private final String fileName;

  public ViolationData(final Violation violation) {
    super(violation);
    messageEscaped = escapeHTML(violation.getMessage());
    final String[] fileParts = violation.getFile().split("\\/");
    fileName = fileParts[fileParts.length - 1];
  }

  public String getFileName() {
    return fileName;
  }

  public String getMessageEscaped() {
    return messageEscaped;
  }
}
