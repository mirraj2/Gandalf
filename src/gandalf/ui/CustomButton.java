package gandalf.ui;

import jasonlib.Rect;
import jasonlib.swing.Graphics3D;
import jasonlib.swing.component.GButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends GButton {

  private boolean hover;

  public CustomButton(String s) {
    super(s);

    setFont(new Font("Georgia", Font.BOLD, 32));

    Dimension d = getPreferredSize();
    d.height = 64;
    setPreferredSize(d);
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
      }

      @Override
      public void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
      }
    });
  }

  @Override
  protected void paintComponent(Graphics gg) {
    Graphics3D g = Graphics3D.create(gg);

    Rect r = new Rect(0, 0, getWidth() - 1, getHeight() - 1);

    g.color(new Color(0, 0, 0, 50)).fill(r);
    g.color(hover ? new Color(100, 180, 255, 120) : new Color(255, 255, 255, 100)).draw(r);


    g.font(getFont());
    g.color(hover ? new Color(100, 180, 255, 255) : Color.white).text(getText(), r);
  }

}
