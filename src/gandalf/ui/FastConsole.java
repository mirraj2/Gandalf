package gandalf.ui;

import jasonlib.swing.global.Components;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.Scrollable;

/**
 * JTextArea was locking up the entire UI when the program did a println() in a while loop
 */
public class FastConsole extends JComponent implements Scrollable {

  private static final int MAX_BUFFER_SIZE = 999999;

  private char[] buffer = new char[1024];
  private int size = 0;

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setFont(getFont());
    g.setColor(getForeground());

    render(g);
  }

  private Dimension render(Graphics g) {
    Rectangle clip = g == null ? null : g.getClipBounds();
    int last = -1;
    int y = 14;
    int maxLineSize = 0;
    for (int i = 0; i < size; i++) {
      if (buffer[i] == '\n' || i == size - 1) {
        if (last != -1) {
          maxLineSize = Math.max(maxLineSize, i - last);
          if (g != null && clip.y < y + 14 && clip.y + clip.height > y) {
            g.drawChars(buffer, last, i - last, 0, y);
          }
          last = -1;
        }
        y += 14;
      } else if (last == -1) {
        last = i;
      }
    }
    return new Dimension(maxLineSize * 10, y);
  }

  @Override
  public Dimension getPreferredSize() {
    return render(null);
  }

  private void ensureCapacity(int newSize) {
    if (buffer.length == MAX_BUFFER_SIZE) {
      // we're already at max capacity
      size = 0;
    } else if (newSize > MAX_BUFFER_SIZE) {
      buffer = new char[MAX_BUFFER_SIZE];
      size = 0;
    } else {
      char[] newBuffer = new char[Math.max(buffer.length * 2, newSize)];
      System.arraycopy(buffer, 0, newBuffer, 0, size);
      buffer = newBuffer;
    }
  }

  public synchronized void append(char[] chars, int offset, int len) {
    if (len + size > this.buffer.length) {
      ensureCapacity(len + size);
    }

    System.arraycopy(chars, offset, this.buffer, size, len);
    size += len;

    Components.refresh(this);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 100;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 14;
  }

}
