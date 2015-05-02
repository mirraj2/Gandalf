package gandalf.ui;

import gandalf.Gandalf;
import gandalf.code.QuickCompiler;
import gandalf.model.Project;
import gandalf.ui.editor.CodeEditor;
import jasonlib.IO;
import jasonlib.swing.component.GLabel;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.component.GScrollPane;
import jasonlib.swing.component.GSeparator;
import jasonlib.swing.component.GSplitPane;
import jasonlib.swing.component.GTextArea;
import jasonlib.swing.component.GTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import net.miginfocom.swing.MigLayout;

public class IDE extends GPanel {

  public static GTextArea console = new GTextArea();

  private Project project;

  private GSplitPane leftRightSplit, topBottomSplit;
  private GTextField projectLabel = new GTextField().color(Color.white).font("Georgia", 26).bold();
  private FileList fileList;
  private CodeEditor editor = new CodeEditor();

  public IDE(Project project) {
    super(new MigLayout("insets 0, gap 0"));

    this.project = project;
    this.fileList = new FileList(project.files);
    new QuickCompiler(editor, project);

    setBackground(new Color(40, 40, 40));
    console.setBackground(getBackground());
    console.setForeground(Color.white);
    console.setFont(new Font("Courier", Font.PLAIN, 14));

    projectLabel.setText(project.name);
    projectLabel.setBackground(new Color(0, 0, 0, 0));
    projectLabel.setOpaque(false);
    projectLabel.setBorder(null);
    projectLabel.setCaretColor(Color.white);

    leftRightSplit = GSplitPane.leftRight(createLeftSide(), createRightSide());
    add(leftRightSplit, "width 100%, height 100%");
    
    editor.edit(fileList.getFile());

    listen();
  }

  private JComponent createLeftSide() {
    GPanel ret = new GPanel(new MigLayout("insets 10, gap 0"));
    GSeparator s = new GSeparator();
    
    BufferedImage gandalfIcon = IO.from(getClass(), "gandalf-small.png").toImage();
    GLabel backButton = new GLabel(gandalfIcon).click(this::goHome);

    backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    ret.add(backButton, "split");
    ret.add(projectLabel, "width 100%, wrap 0");
    ret.add(s, "width 100%, height pref!, wrap 0");
    ret.add(fileList, "width 100%, height 100%");
    return ret;
  }

  private JComponent createRightSide() {
    GScrollPane editorScroll = new GScrollPane(editor);
    GScrollPane consoleScroll = new GScrollPane(console);
    
    return topBottomSplit = GSplitPane.topBottom(editorScroll, consoleScroll);
  }

  private void goHome() {
    project.name = projectLabel.getText();
    project.save();
    Gandalf.goHome();
  }

  public void shutdown() {
    project.save();
  }

  private void listen() {
    fileList.change(() -> {
      editor.edit(fileList.getFile());
    });

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        leftRightSplit.setDividerLocation(256);
        topBottomSplit.setDividerLocation(getHeight() - 200);
      }
    });

    projectLabel.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (projectLabel.getText().equals(Project.DEFAULT_NAME)) {
          projectLabel.setText("");
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        String text = projectLabel.getText();
        if (text.isEmpty()) {
          text = Project.DEFAULT_NAME;
          projectLabel.setText(text);
        }
        project.name = text;
      }
    });
  }

}
