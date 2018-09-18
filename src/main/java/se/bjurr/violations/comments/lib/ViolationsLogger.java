package se.bjurr.violations.comments.lib;

/**
 * When using this library with Jenkins it is convenient to have the logging the build log and not
 * in the global log. This interface allows a Jenkins plugin to do such logging.
 */
public interface ViolationsLogger {
  void log(final String string);
}
