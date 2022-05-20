package se.bjurr.violations.comments.lib;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;
import java.io.IOException;
import java.io.Writer;
import se.bjurr.violations.lib.util.StringUtils;

/**
 * Extends the DefaultMustacheFactory to use an encode method that does not HTML encode the carriage
 * return and new line characters. HTML Encoding the `\n` and `\r` characters does not display
 * correctly in GitHub markdown and does not need to be encoded per the HTML RFC.
 */
public class ViolationMustacheFactory extends DefaultMustacheFactory {

  @Override
  public void encode(String value, Writer writer) {
    try {
      String encoded = StringUtils.escapeHTML(value);
      writer.write(encoded);
    } catch (IOException e) {
      throw new MustacheException("Unable to encode value", e);
    }
  }
}
