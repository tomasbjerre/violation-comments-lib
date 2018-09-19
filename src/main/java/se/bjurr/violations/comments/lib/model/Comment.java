package se.bjurr.violations.comments.lib.model;

import java.util.List;
import se.bjurr.violations.comments.lib.CommentsProvider;

public class Comment {
  private final String content;
  private final String identifier;
  /**
   * Things that are specific to the {@link CommentsProvider}. This will be send back to the {@link
   * CommentsProvider} when creating comments.
   */
  private final List<String> specifics;

  /**
   * Can be used to identify if this is a comment on a Pull Request code diff, or on the actual pull
   * request.
   */
  private final String type;

  public Comment(String identifier, String content, String type, List<String> specifics) {
    this.identifier = identifier;
    this.content = content;
    this.type = type;
    this.specifics = specifics;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Comment other = (Comment) obj;
    if (this.content == null) {
      if (other.content != null) {
        return false;
      }
    } else if (!this.content.equals(other.content)) {
      return false;
    }
    if (this.identifier == null) {
      if (other.identifier != null) {
        return false;
      }
    } else if (!this.identifier.equals(other.identifier)) {
      return false;
    }
    if (this.specifics == null) {
      if (other.specifics != null) {
        return false;
      }
    } else if (!this.specifics.equals(other.specifics)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

  public String getContent() {
    return this.content;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public List<String> getSpecifics() {
    return this.specifics;
  }

  public String getType() {
    return this.type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
    result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
    result = prime * result + ((this.specifics == null) ? 0 : this.specifics.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "Comment [content="
        + this.content
        + ", identifier="
        + this.identifier
        + ", specifics="
        + this.specifics
        + ", type="
        + this.type
        + "]";
  }
}
