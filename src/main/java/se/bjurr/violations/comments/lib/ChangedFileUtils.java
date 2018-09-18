package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.lib.util.Optional.absent;
import static se.bjurr.violations.lib.util.Optional.fromNullable;

import java.util.List;
import se.bjurr.violations.comments.lib.model.ChangedFile;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Optional;

public class ChangedFileUtils {

  /**
   * When creating comment, the call should use the file as it is specified by the comments
   * provider. Not the one specified by the {@link Violation}. The one in the {@link Violation} may
   * not be recognized. <br>
   * <br>
   * Here we make a guess on which file in the {@link CommentsProvider} to use.
   */
  static Optional<ChangedFile> findChangedFile(
      final List<ChangedFile> files, final Violation violation) {
    for (final ChangedFile providerFile : files) {
      String violationFile = violation.getFile();
      String fileFile = providerFile.getFilename();
      if (violationFile.endsWith(fileFile) || fileFile.endsWith(violationFile)) {
        return fromNullable(providerFile);
      }
    }
    return absent();
  }
}
