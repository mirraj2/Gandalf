package gandalf.ui.editor;

import jasonlib.Log;
import com.google.common.base.CharMatcher;

public class CodeFormatter {

  private static final CharMatcher spaceBefore = CharMatcher.anyOf("{/*+=");
  private static final CharMatcher spaceAfter = CharMatcher.anyOf("/*+=,");

  public static String format(String s) {
    Log.debug("Formatting...");

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (spaceBefore.matches(c)) {
        if (sb.length() > 0) {
          char before = sb.charAt(sb.length() - 1);
          if (before != ' ' && before != '\n') {
            sb.append(' ');
          }
        }
      }
      sb.append(c);
      if (spaceAfter.matches(c)) {
        if (i < s.length() - 1) {
          char next = s.charAt(i + 1);
          if (next != ' ' && next != '\n') {
            sb.append(' ');
          }
        }
      }
    }
    return sb.toString().replace("+ =", "+=");
  }

}
