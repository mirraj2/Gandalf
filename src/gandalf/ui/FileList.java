package gandalf.ui;

import gandalf.code.CodeRunner;
import gandalf.model.GFile;
import jasonlib.swing.component.GLabel;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.global.Components;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import net.miginfocom.swing.MigLayout;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class FileList extends GPanel {

  private GFile selected;
  private List<Runnable> callbacks = Lists.newArrayList();
  private final List<GFile> files;
  private CustomButton newClassButton = new CustomButton("New Class").fontSize(16);

  public FileList(List<GFile> files) {
    super(new MigLayout("insets 0, gap 0"));

    this.files = files;

    init();

    selected = getFirstVisibleFile();
    newClassButton.click(this::addClass);
  }

  private void init() {
    for (GFile file : files) {
      if (!file.hidden) {
        add(new FileDiv(file), "width 100%, wrap");
      }
    }

    add(Box.createVerticalGlue(), "height 100%, wrap");
    add(newClassButton, "growx");
  }

  private void remakeUI() {
    removeAll();
    init();
    Components.refresh(this);
  }

  private void addClass() {
    String name = JOptionPane.showInputDialog("Enter class name:");
    if (!Strings.isNullOrEmpty(name)) {
      GFile newFile = new GFile(name + ".java", "\npublic class " + name + " {\n  \n}\n");
      files.add(newFile);
      setSelectedFile(newFile);
      sortFiles();
    }
  }

  private void sortFiles() {
    Collections.sort(files, (a, b) -> {
      return a.name.compareToIgnoreCase(b.name);
    });
    remakeUI();
  }

  public void change(Runnable callback) {
    callbacks.add(callback);
  }

  public GFile getFile() {
    return selected;
  }

  private void setSelectedFile(GFile file) {
    if (selected == file) {
      return;
    }
    selected = file;
    for (Runnable callback : callbacks) {
      callback.run();
    }
    repaint();
  }

  private GFile getFirstVisibleFile() {
    for (GFile file : files) {
      if (!file.hidden) {
        return file;
      }
    }
    return null;
  }

  private class FileDiv extends GPanel {
    public final GFile file;
    public final GLabel label;
    private CustomButton runButton;
    private CodeRunner runner;

    public FileDiv(GFile file) {
      super(new MigLayout("insets 5 5 5 5, gap 0"));

      this.file = file;
      label = new GLabel(file.name).font("Georgia", 20).color(Color.white);
      runButton = new CustomButton().fontSize(12).size(60, 20);

      syncRunButton();

      add(label, "width 100%");
      add(runButton, "aligny center");

      setComponentPopupMenu(createPopup());

      listen();
    }

    private JPopupMenu createPopup() {
      JPopupMenu ret = new JPopupMenu();
      JMenuItem rename = new JMenuItem(new AbstractAction("Rename") {
        @Override
        public void actionPerformed(ActionEvent e) {
          String s = JOptionPane.showInputDialog("Name:");
          if (s != null) {
            file.name = s + ".java";
            sortFiles();
          }
        }
      });
      JMenuItem hide = new JMenuItem(new AbstractAction("Hide") {
        @Override
        public void actionPerformed(ActionEvent e) {
          file.hidden = true;
          removeFromList();
        }
      });
      JMenuItem delete = new JMenuItem(new AbstractAction("Delete") {
        @Override
        public void actionPerformed(ActionEvent e) {
          files.remove(file);
          removeFromList();
        }
      });
      ret.add(rename);
      ret.add(hide);
      ret.add(delete);
      return ret;
    }

    private void removeFromList() {
      FileList.this.remove(FileDiv.this);
      setSelectedFile(getFirstVisibleFile());
      Components.refresh(FileList.this);
    }

    private void syncRunButton() {
      syncRunVisibility();

      if (runner == null) {
        runButton.setText("Run");
        runButton.hoverColor = new Color(180, 255, 100);
      } else {
        runButton.setText("Stop");
        runButton.hoverColor = new Color(255, 100, 100);
      }
      runButton.repaint();
    }

    private void syncRunVisibility() {
      runButton.setVisible(file.getContent().contains("void main"));
    }

    @Override
    protected void paintComponent(Graphics g) {
      if (file == selected) {
        g.setColor(new Color(50, 100, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    }

    private void listen() {
      file.change(() -> {
        syncRunVisibility();
      });

      runButton.click(() -> {
        if (runner == null) {
          runner = new CodeRunner().run(file, () -> {
            runner = null;
            syncRunButton();
          });
        } else {
          runner.stop();
          runner = null;
        }
        syncRunButton();
      });

      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          setSelectedFile(file);
        }
      });
    }
  }

}
