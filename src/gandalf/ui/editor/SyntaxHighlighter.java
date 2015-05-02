package gandalf.ui.editor;

import static com.google.common.collect.Iterables.concat;
import gandalf.model.GFile;
import java.awt.Color;
import java.util.Set;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

public class SyntaxHighlighter {

  private static final Set<String> keywords = ImmutableSet.of("abstract", "assert", "boolean", "break", "byte", "case",
      "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
      "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
      "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
      "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while");

  private static final SimpleAttributeSet keywordStyle = new SimpleAttributeSet();
  private static final SimpleAttributeSet classStyle = new SimpleAttributeSet();
  private static final SimpleAttributeSet stringStyle = new SimpleAttributeSet();
  private static final SimpleAttributeSet commentStyle = new SimpleAttributeSet();
  private static final SimpleAttributeSet errorStyle = new SimpleAttributeSet();

  static {
    StyleConstants.setForeground(keywordStyle, new Color(100, 180, 255));
    StyleConstants.setForeground(classStyle, new Color(200, 100, 200));
    StyleConstants.setForeground(stringStyle, new Color(255, 155, 0));
    StyleConstants.setForeground(commentStyle, new Color(100, 255, 100));
    StyleConstants.setBackground(errorStyle, new Color(255, 0, 0));
  }

  public static void highlightProblems(GFile file, Highlighter highlighter) {
    highlighter.removeAllHighlights();

    for (CategorizedProblem problem : concat(file.errors, file.warnings)) {
      int i = problem.getSourceStart();
      int j = problem.getSourceEnd();
      try {
        highlighter.addHighlight(i, j + 1, new SquigglePainter(problem.isError() ? Color.red : Color.yellow));
      } catch (BadLocationException e) {
        throw Throwables.propagate(e);
      }
      // doc.setCharacterAttributes(i, j - i + 1, errorStyle, false);
    }
  }

  public static void apply(DefaultStyledDocument doc) {
    // clear existing styles
    doc.setCharacterAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
    String s;
    try {
      s = doc.getText(0, doc.getLength());
    } catch (BadLocationException e) {
      throw Throwables.propagate(e);
    }

    CharMatcher matcher = CharMatcher.JAVA_LETTER_OR_DIGIT.negate();

    int lastIndex = -1;
    boolean inString = false, inComment = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      if (!inComment && (c == '"' || (inString && c == '\n'))) {
        if (inString) {
          doc.setCharacterAttributes(lastIndex, i - lastIndex + 1, stringStyle, false);
          lastIndex = -1;
          inString = false;
        } else {
          inString = true;
          lastIndex = i;
        }
        continue;
      } else if (inString) {
        continue;
      }

      if (inComment) {
        if (c == '\n') {
          doc.setCharacterAttributes(lastIndex, i - lastIndex + 1, commentStyle, false);
          inComment = false;
        }
        continue;
      } else if (c == '/' && i < s.length() - 1) {
        if (s.charAt(i + 1) == '/') {
          inComment = true;
          lastIndex = i;
          continue;
        }
      }

      if (matcher.matches(c)) {
        if (lastIndex != -1) {
          stylize(s, lastIndex, i, doc);
          lastIndex = -1;
        }
      } else {
        if (lastIndex == -1) {
          lastIndex = i;
        }
      }
    }
  }

  private static void stylize(String s, int i, int j, DefaultStyledDocument doc) {
    String token = s.substring(i, j);
    AttributeSet style = null;
    if (keywords.contains(token)) {
      style = keywordStyle;
    } else if (Character.isUpperCase(token.charAt(0))) {
      style = classStyle;
    }
    if (style != null) {
      doc.setCharacterAttributes(i, j - i, style, false);
    }
  }

}
