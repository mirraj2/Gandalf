package gandalf;

import gandalf.code.CodeRunner;
import gandalf.model.Project;
import gandalf.ui.IDE;
import gandalf.ui.WelcomeScreen;
import jasonlib.Log;
import jasonlib.swing.component.GFrame;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.global.Components;
import javax.swing.SwingUtilities;

public class Gandalf extends GPanel {

  private static GFrame frame;

  public static void newProject() {
    frame.content(new IDE(new Project()));
  }
  
  public static void loadProject(int id) {
    frame.content(new IDE(Project.load(id)));
  }

  public static void goHome() {
    frame.content(new WelcomeScreen());
  }

  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeLater(() -> {
      frame = new GFrame("Gandalf").content(new WelcomeScreen()).size(1200, 800).start();
    });

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      Log.info("Shutting down...");
      CodeRunner.shutdown();

      IDE ide = Components.getChildOfClass(IDE.class, frame);
      if (ide != null) {
        ide.shutdown();
      }
    }));
  }

}
