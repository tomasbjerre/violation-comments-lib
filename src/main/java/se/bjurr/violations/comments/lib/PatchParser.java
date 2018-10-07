package se.bjurr.violations.comments.lib;

import static java.util.Optional.empty;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatchParser {

  private static final Pattern RANGE_PATTERN = Pattern.compile(
      "@@\\p{IsWhite_Space}-[0-9]+(?:,[0-9]+)?\\p{IsWhite_Space}\\+([0-9]+)(?:,[0-9]+)?\\p{IsWhite_Space}@@.*");

  /** http://en.wikipedia.org/wiki/Diff_utility#Unified_format */
  public static Optional<Integer> findLineToComment(String patchString, Integer lineToComment) {
    if (patchString == null) {
      return empty();
    }
    int currentLine = -1;
    int patchLocation = 0;
    for (String line : patchString.split("\n")) {
      if (line.startsWith("@")) {
        Matcher matcher = RANGE_PATTERN.matcher(line);
        if (!matcher.matches()) {
          throw new IllegalStateException(
              "Unable to parse patch line " + line + "\nFull patch: \n" + patchString);
        }
        currentLine = Integer.parseInt(matcher.group(1));
      } else if (line.startsWith("+") || line.startsWith(" ")) {
        // Added or unmodified
        if (currentLine == lineToComment) {
          return Optional.ofNullable(patchLocation);
        }
        currentLine++;
      }
      patchLocation++;
    }
    return empty();
  }

  public static Map<Integer, Optional<Integer>> getLineTranslation(final String patchString) {
    final Map<Integer, Optional<Integer>> map = new TreeMap<>();
    if (patchString == null) {
      return map;
    }
    int currentLine = -1;
    int patchLocation = 1;
    for (String line : patchString.split("\n")) {
      if (line.startsWith("@")) {
        Matcher matcher = RANGE_PATTERN.matcher(line);
        if (!matcher.matches()) {
          throw new IllegalStateException(
              "Unable to parse patch line " + line + "\nFull patch: \n" + patchString);
        }
        currentLine = Integer.parseInt(matcher.group(1));
        patchLocation = currentLine;
      } else if (line.startsWith("+") && !line.startsWith("++")) {
        map.put(currentLine, Optional.empty());
        currentLine++;
      } else if (line.startsWith(" ")) {
        map.put(currentLine, Optional.of(patchLocation));
        currentLine++;
        patchLocation++;
      } else {
        patchLocation++;
      }
    }
    return map;
  }
}
