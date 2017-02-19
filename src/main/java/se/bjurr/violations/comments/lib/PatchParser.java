package se.bjurr.violations.comments.lib;

import static se.bjurr.violations.lib.util.Optional.absent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.bjurr.violations.lib.util.Optional;

public class PatchParser {

  /**
   * http://en.wikipedia.org/wiki/Diff_utility#Unified_format
   */
  static Optional<Integer> findLineToComment(String patchString, Integer lineToComment) {
   int currentLine = -1;
   int patchLocation = 0;
   for (String line : patchString.split("\n")) {
    if (line.startsWith("@")) {
     Matcher matcher = Pattern
       .compile("@@\\p{IsWhite_Space}-[0-9]+(?:,[0-9]+)?\\p{IsWhite_Space}\\+([0-9]+)(?:,[0-9]+)?\\p{IsWhite_Space}@@.*")
       .matcher(line);
     if (!matcher.matches()) {
      throw new IllegalStateException("Unable to parse patch line " + line + "\nFull patch: \n" + patchString);
     }
     currentLine = Integer.parseInt(matcher.group(1));
    } else if (line.startsWith("+") || line.startsWith(" ")) {
     // Added or unmodified
     if (currentLine == lineToComment) {
      return Optional.fromNullable(patchLocation);
     }
     currentLine++;
    }
    patchLocation++;
   }
   return absent();
  }
}
