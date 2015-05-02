package gandalf.code;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collections;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class Autocomplete {

  private final ClassIndex index = new ClassIndex();

  public void run(JTextPane pane) {
    String text = pane.getText();
    String token = getTokenAt(pane.getCaretPosition(), text);

    if (token == null) {
      return;
    }

    List<String> classes = Lists.newArrayList(index.getClasses(token));
    for (int i = classes.size() - 1; i >= 0; i--) {
      if (classes.get(i).startsWith("java.lang")) {
        classes.remove(i);
      }
    }

    if (classes.size() == 1) {
      importClass(getOnlyElement(classes), pane, text);
    } else if (classes.size() > 1) {
      importClass(getBestClass(classes), pane, text);
    }
  }

  private String getBestClass(List<String> classes) {
    Collections.sort(classes, (a, b) -> {
      return score(b) - score(a);
    });
    return classes.get(0);
  }

  private int score(String clazz) {
    int score = 0;
    if (clazz.contains("internal")) {
      score -= 200;
    }
    if (clazz.contains("xml")) {
      score -= 100;
    }
    if (clazz.contains("swing")) {
      score -= 50;
    }
    if (clazz.startsWith("java.util")) {
      score += 10;
    }
    return score;
  }

  private void importClass(String clazz, JTextPane pane, String text) {
    try {
      pane.getDocument().insertString(0, "import " + clazz + ";\n", null);
    } catch (BadLocationException e) {
      throw Throwables.propagate(e);
    }
  }

  private String getTokenAt(int index, String text) {
    CharMatcher matcher = CharMatcher.JAVA_LETTER_OR_DIGIT;
    int i = index - 1, j = index - 1;
    for (; i >= 0; i--) {
      if (!matcher.matches(text.charAt(i))) {
        i++;
        break;
      }
    }
    for (; j < text.length(); j++) {
      if (!matcher.matches(text.charAt(j))) {
        break;
      }
    }
    if (j <= i) {
      return null;
    }
    return text.substring(i, j);
  }

}
