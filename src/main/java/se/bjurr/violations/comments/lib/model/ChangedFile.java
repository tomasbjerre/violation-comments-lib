package se.bjurr.violations.comments.lib.model;

import static com.google.common.base.Optional.fromNullable;

import com.google.common.base.Optional;

public class ChangedFile {
 private final String filename;
 private final String sha;

 public ChangedFile(String filename, String sha) {
  this.filename = filename;
  this.sha = sha;
 }

 public String getFilename() {
  return filename;
 }

 public Optional<String> getSha() {
  return fromNullable(sha);
 }

 @Override
 public String toString() {
  return "ChangedFile [filename=" + filename + ", sha=" + sha + "]";
 }
}
