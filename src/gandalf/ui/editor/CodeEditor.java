package gandalf.ui.editor;

import gandalf.model.GFile;
import jasonlib.swing.component.GPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import com.google.common.collect.Lists;

public class CodeEditor extends GPanel {

  private final DefaultStyledDocument doc = new DefaultStyledDocument();
  private final JTextPane textPane = new JTextPane(doc);
  private List<Runnable> onEditListeners = Lists.newArrayList();
  private GFile file;

  public CodeEditor() {
    super(new GridLayout(1, 1));

    textPane.setOpaque(false);
    textPane.setForeground(Color.white);
    textPane.setCaretColor(Color.white);
    textPane.setFont(new Font("Courier New", Font.BOLD, 14));
    textPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    doc.setDocumentFilter(new CustomFilter());

    add(textPane);

    listen();
  }

  public void edit(GFile file) {
    this.file = file;

    textPane.setText(file.content);

    SyntaxHighlighter.apply(doc);
  }

  public void onCompileFinished() {
    SyntaxHighlighter.highlightProblems(file, textPane.getHighlighter());
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    if (getParent() instanceof JViewport) {
      dim.width = Math.max(dim.width, getParent().getWidth());
      dim.height = Math.max(dim.height, getParent().getHeight());
    }
    return dim;
  }

  private void listen() {
    doc.addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        onTextChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        onTextChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
  }

  public void change(Runnable callback) {
    onEditListeners.add(callback);
  }

  private void onTextChanged() {
    textPane.getHighlighter().removeAllHighlights();
    file.content = textPane.getText();
    SwingUtilities.invokeLater(() -> {
      SyntaxHighlighter.apply(doc);
    });
    for (Runnable listener : onEditListeners) {
      listener.run();
    }
  }

}
