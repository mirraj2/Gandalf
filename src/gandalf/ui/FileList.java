package gandalf.ui;

import gandalf.model.GFile;
import jasonlib.swing.component.GList;
import jasonlib.swing.component.GPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.Border;

public class FileList extends GPanel {

  public final GList<GFile> list;

  public FileList(List<GFile> files) {
    setLayout(new GridLayout(1, 1));

    list = new GList<>(files);
    list.setCellRenderer(new MyCellRenderer());
    list.setOpaque(false);
    list.setBackground(new Color(0, 0, 0, 0));
    list.setForeground(Color.white);
    list.setFont(new Font("Georgia", Font.PLAIN, 20));
    list.setSelectedIndex(0);

    add(list, "width 100%, height 100%");
  }

  public GFile getFile() {
    return list.getSelectedValue();
  }

  private static class MyCellRenderer extends DefaultListCellRenderer {
    final Border border2 = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (c instanceof JComponent) {
        ((JComponent) c).setBorder(border2);
      }
      if (isSelected) {
        c.setBackground(new Color(40, 80, 200, 150));
      } else {
        c.setBackground(new Color(0, 0, 0, 0));
      }
      return c;
    }
  }

}
