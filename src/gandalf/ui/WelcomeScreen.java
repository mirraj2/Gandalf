package gandalf.ui;

import gandalf.Gandalf;
import gandalf.model.Project;
import jasonlib.IO;
import jasonlib.Json;
import jasonlib.swing.Graphics3D;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.component.GSeparator;
import jasonlib.swing.global.Components;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class WelcomeScreen extends GPanel {

  private final BufferedImage bi = IO.from(getClass(), "gandalf.jpg").toImage();

  CustomButton newProjectButton = new CustomButton("New Project");

  public WelcomeScreen() {
    add(Components.center(createContent()), "width 100%, height 100%");

    newProjectButton.click(() -> {
      Gandalf.newProject();
    });
  }

  private JComponent createContent() {
    File[] projectFiles = Project.projectsDir.listFiles();

    GPanel ret = new GPanel();
    ret.add(newProjectButton, "alignx center,wrap");
    if (projectFiles.length > 0) {
      ret.add(new GSeparator(), "width 100%, wrap");
    }
    for (File file : projectFiles) {
      Json json = IO.from(new File(file, "project.json")).toJson();
      String name = json.get("name");
      if (name.equals(Project.DEFAULT_NAME)) {
        name = "Untitled Project";
      }
      CustomButton button = new CustomButton(name).fontSize(20).size(newProjectButton.getPreferredSize().width, 40);
      ret.add(button, "alignx center, wrap");

      button.click(() -> {
        Gandalf.loadProject(json.getInt("id"));
      });

      JPopupMenu popup = new JPopupMenu();
      JMenuItem delete = new JMenuItem(new AbstractAction("Delete") {
        @Override
        public void actionPerformed(ActionEvent e) {
          Project.delete(json.getInt("id"));
          ret.remove(button);
          Components.refresh(ret);
        }
      });
      popup.add(delete);
      button.setComponentPopupMenu(popup);
    }
    return ret;
  }

  @Override
  protected void paintComponent(Graphics gg) {
    Graphics3D g = Graphics3D.create(gg);
    double r = 1.0 * bi.getWidth() / bi.getHeight();
    double h1 = getWidth() / r;
    if (h1 >= getHeight()) {
      g.draw(bi, 0, 0, getWidth(), h1);
    } else {
      double w = getHeight() * r;
      g.draw(bi, getWidth() - w, 0, getHeight() * r, getHeight());
    }
  }

}
