package gandalf.ui;

import gandalf.compile.QuickCompiler;
import gandalf.model.Project;
import gandalf.ui.editor.CodeEditor;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.component.GScrollPane;
import jasonlib.swing.component.GSplitPane;
import jasonlib.swing.component.GTextField;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import net.miginfocom.swing.MigLayout;

public class IDE extends GPanel {

  private Project project;

  private GSplitPane split;
  private GTextField projectLabel = new GTextField().color(Color.white).font("Georgia", 26).bold();
  private FileList fileList;
  private CodeEditor editor = new CodeEditor();
  private QuickCompiler compiler;

  public IDE(Project project) {
    super(new MigLayout("insets 0, gap 0"));

    this.project = project;
    this.fileList = new FileList(project.files);
    this.compiler = new QuickCompiler(editor, project);

    setBackground(new Color(40, 40, 40));
    projectLabel.setText(project.name);
    projectLabel.setBackground(new Color(0, 0, 0, 0));
    projectLabel.setOpaque(false);
    projectLabel.setBorder(null);
    projectLabel.setCaretColor(Color.white);

    split = GSplitPane.leftRight(createLeftSide(), createRightSide());
    add(split, "width 100%, height 100%");
    
    editor.edit(fileList.getFile());

    listen();
  }

  private JComponent createLeftSide() {
    GPanel ret = new GPanel();
    JSeparator s = new JSeparator();
    s.setBackground(new Color(0, 0, 0, 0));

    ret.add(projectLabel, "width 100%, wrap 5");
    ret.add(s, "width 100%, height pref!, wrap 5");
    ret.add(fileList, "width 100%, height 100%");
    return ret;
  }

  private JComponent createRightSide() {
    GPanel ret = new GPanel(new MigLayout("insets 5 0 0 0, gap 0"));
    ret.add(new GScrollPane(editor), "width 100%, height 100%");
    return ret;
  }

  private void listen() {
    fileList.list.change(() -> {
      editor.edit(fileList.list.getSelectedValue());
    });

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        split.setDividerLocation(256);
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
