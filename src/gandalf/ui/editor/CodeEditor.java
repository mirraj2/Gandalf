package gandalf.ui.editor;

import gandalf.code.Autocomplete;
import gandalf.model.GFile;
import gandalf.ui.IDE;
import jasonlib.swing.component.GPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import com.google.common.collect.Lists;

public class CodeEditor extends GPanel {

  private final DefaultStyledDocument doc = new DefaultStyledDocument();
  private final JTextPane textPane = new JTextPane(doc);
  private List<Runnable> onEditListeners = Lists.newArrayList();
  private GFile file;
  private Autocomplete autocomplete = new Autocomplete();

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

    textPane.setVisible(file != null);
    textPane.setText(file == null ? "" : file.getContent());

    SyntaxHighlighter.apply(doc);
  }

  public void onCompileFinished() {
    if (file != null) {
      SyntaxHighlighter.highlightProblems(file, textPane.getHighlighter());
    }
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

    textPane.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE && (e.isControlDown() || e.isMetaDown())) {
          autocomplete.run(textPane);
        } else if (code == KeyEvent.VK_F && e.isMetaDown() && e.isShiftDown()) {
          String text = CodeFormatter.format(textPane.getText());
          textPane.setText(text);
        }
      }
    });

    textPane.addCaretListener(new CaretListener() {
      CategorizedProblem lastProblem = null;
      @Override
      public void caretUpdate(CaretEvent e) {
        CategorizedProblem problem = file.getProblemAt(e.getDot());
        if (problem == lastProblem) {
          return;
        }
        lastProblem = problem;
        if (problem != null) {
          IDE.console.setText(problem.getMessage() + "\n");
        }
      }
    });
  }

  public void change(Runnable callback) {
    onEditListeners.add(callback);
  }

  private void onTextChanged() {
    IDE.console.setText("");
    textPane.getHighlighter().removeAllHighlights();
    file.setContent(textPane.getText());
    SwingUtilities.invokeLater(() -> {
      SyntaxHighlighter.apply(doc);
    });
    for (Runnable listener : onEditListeners) {
      listener.run();
    }
  }

}
