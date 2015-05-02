package gandalf.ui.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;
import com.google.common.base.Throwables;

public class SquigglePainter extends DefaultHighlighter.DefaultHighlightPainter {

  public SquigglePainter(Color color) {
    super(color);
  }

  @Override
  public Shape paintLayer(Graphics g, int i, int j, Shape bounds, JTextComponent c, View view) {
    Rectangle r = getDrawingArea(i, j, bounds, view);

    g.setColor(getColor());

    int squiggle = 2;
    int twoSquiggles = squiggle * 2;
    int y = r.y + r.height - squiggle;

    for (int x = r.x; x <= r.x + r.width - twoSquiggles; x += twoSquiggles) {
      g.drawArc(x, y, squiggle, squiggle, 0, 180);
      g.drawArc(x + squiggle, y, squiggle, squiggle, 180, 181);
    }

    return r;
  }

  private Rectangle getDrawingArea(int i, int j, Shape bounds, View view) {
    if (i != view.getStartOffset() || j != view.getEndOffset()) {
      // Should only render part of View.
      try {
        bounds = view.modelToView(i, Position.Bias.Forward, j, Position.Bias.Backward, bounds);
      } catch (BadLocationException e) {
        throw Throwables.propagate(e);
      }
    }
    return bounds instanceof Rectangle ? (Rectangle) bounds : bounds.getBounds();
  }
}