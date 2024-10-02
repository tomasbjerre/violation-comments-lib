package se.bjurr.violations.comments.lib.model;

import java.util.List;
import se.bjurr.violations.comments.lib.CommentsProvider;

public class ChangedFile {
  private final String filename;

  /**
   * Things that are specific to the {@link CommentsProvider}. This will be send back to the {@link
   * CommentsProvider} when creating comments.
   */
  private final List<String> specifics;

  public ChangedFile(String filename, List<String> specifics) {
    this.filename = filename;
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
    ChangedFile other = (ChangedFile) obj;
    if (this.filename == null) {
      if (other.filename != null) {
        return false;
      }
    } else if (!this.filename.equals(other.filename)) {
      return false;
    }
    if (this.specifics == null) {
      if (other.specifics != null) {
        return false;
      }
    } else if (!this.specifics.equals(other.specifics)) {
      return false;
    }
    return true;
  }

  public String getFilename() {
    return this.filename;
  }

  public List<String> getSpecifics() {
    return this.specifics;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.filename == null) ? 0 : this.filename.hashCode());
    result = prime * result + ((this.specifics == null) ? 0 : this.specifics.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "ChangedFile [filename=" + this.filename + ", specifics=" + this.specifics + "]";
  }
}
