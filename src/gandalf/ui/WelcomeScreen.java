package gandalf.ui;

import gandalf.Gandalf;
import jasonlib.IO;
import jasonlib.swing.Graphics3D;
import jasonlib.swing.component.GPanel;
import jasonlib.swing.global.Components;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class WelcomeScreen extends GPanel {

  private final BufferedImage bi = IO.from(getClass(), "gandalf.jpg").toImage();

  public WelcomeScreen() {
    CustomButton button = new CustomButton("New Project");
    add(Components.center(button), "width 100%, height 100%");

    button.click(() -> {
      Gandalf.newProject();
    });
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
