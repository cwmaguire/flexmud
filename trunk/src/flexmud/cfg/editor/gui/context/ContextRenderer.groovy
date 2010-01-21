package flexmud.cfg.editor.gui.context

import flexmud.engine.context.Context
import java.awt.Color
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellRenderer

public class ContextRenderer implements TreeCellRenderer {

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Context context = null
    if (((DefaultMutableTreeNode) value).getUserObject() instanceof Context) {
      context = (Context) ((DefaultMutableTreeNode) value).getUserObject()
    }
    JPanel panel = new JPanel(background: Color.WHITE)
    JLabel label = new JLabel(context ? context.getName() : (String) value)
    panel.add(label)
    return panel
  }

}