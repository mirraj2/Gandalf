package gandalf.ui.editor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

public class CustomFilter extends DocumentFilter {

  @Override
  public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
    if (s.equals("\n")) {
      s = addWhiteSpace(fb.getDocument(), offset);
    } else if (s.equals("\t")) {
      s = "  ";
    }
    super.insertString(fb, offset, s, a);
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a) throws BadLocationException {
    if (s.equals("\n")) {
      s = addWhiteSpace(fb.getDocument(), offset);
    } else if (s.equals("\t")) {
      s = "  ";
    }

    super.replace(fb, offset, length, s, a);
  }

  private String addWhiteSpace(Document doc, int offset) throws BadLocationException {
    StringBuilder whiteSpace = new StringBuilder("\n");
    Element rootElement = doc.getDefaultRootElement();
    int line = rootElement.getElementIndex(offset);
    int i = rootElement.getElement(line).getStartOffset();

    while (true) {
      String s = doc.getText(i, 1);

      if (s.equals(" ") || s.equals("\t")) {
        whiteSpace.append(s);
        i++;
      }
      else {
        break;
      }
    }

    return whiteSpace.toString();
  }

}
