package se.bjurr.violations.comments.lib.model;

public class Comment {
 private final String content;
 private final String identifier;
 /**
  * Can be used to identify if this is a comment on a Pull Request code diff, or
  * on the actual pull request.
  */
 private final String type;

 public Comment(String identifier, String content, String type) {
  this.identifier = identifier;
  this.content = content;
  this.type = type;
 }

 public String getType() {
  return type;
 }

 public String getContent() {
  return content;
 }

 public String getIdentifier() {
  return identifier;
 }
}
