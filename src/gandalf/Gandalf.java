package gandalf;

import gandalf.model.Project;
import gandalf.ui.IDE;
import gandalf.ui.WelcomeScreen;
import jasonlib.swing.component.GFrame;
import jasonlib.swing.component.GPanel;
import javax.swing.SwingUtilities;

public class Gandalf extends GPanel {

  private static GFrame frame;

  public static void newProject() {
    frame.content(new IDE(new Project()));
  }

  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeLater(() -> {
      frame = new GFrame("Gandalf").content(new WelcomeScreen()).size(1200, 800).start();
    });
  }

}
