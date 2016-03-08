package se.bjurr.violations.comments.lib.model;

import java.util.List;

public class ChangedFile {
 private final String filename;
 /**
  * Things that are specific to the {@link CommentsProvider}. This will be send
  * back to the {@link CommentsProvider} when creating comments.
  */
 private final List<String> specifics;

 public ChangedFile(String filename, List<String> specifics) {
  this.filename = filename;
  this.specifics = specifics;
 }

 public String getFilename() {
  return filename;
 }

 public List<String> getSpecifics() {
  return specifics;
 }

 @Override
 public String toString() {
  return "ChangedFile [filename=" + filename + ", specifics=" + specifics + "]";
 }
}
