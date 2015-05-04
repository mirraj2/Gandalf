package gandalf.ui;

import jasonlib.Rect;
import jasonlib.swing.Graphics3D;
import jasonlib.swing.component.GButton;
import jasonlib.util.Utils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends GButton {

  private boolean hover;
  public Color hoverColor = new Color(100, 180, 255);

  public CustomButton(){
    this("");
  }

  public CustomButton(String s) {
    super(s);

    fontSize(32);
    
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

  public CustomButton fontSize(int size) {
    setFont(new Font("Georgia", Font.BOLD, size));
    return height(size * 2);
  }

  public CustomButton height(int h) {
    Dimension d = getPreferredSize();
    return size(d.width, h);
  }

  public CustomButton size(int w, int h) {
    Dimension d = new Dimension(w, h);
    setPreferredSize(d);
    setMinimumSize(d);
    return this;
  }

  @Override
  protected void paintComponent(Graphics gg) {
    Graphics3D g = Graphics3D.create(gg);

    Rect r = new Rect(0, 0, getWidth() - 1, getHeight() - 1);

    g.color(new Color(0, 0, 0, 100)).fill(r);
    g.color(hover ? Utils.withAlpha(hoverColor, 220) : new Color(255, 255, 255, 100)).draw(r);

    g.font(getFont());
    g.color(hover ? hoverColor : Color.white).text(getText(), r);
  }

}
