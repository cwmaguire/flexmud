package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller

import flexmud.engine.context.Context
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.JTable

public class ContextCommandTablePanel extends JPanel {
  String title = "Commands"
  Controller controller
  JTable table

  def ContextCommandTablePanel(Controller controller) {

    super(new GridLayout(1, 0))

    this.controller = controller

    table = new JTable()
    table.setFillsViewportHeight(true)

    JScrollPane scrollPane = new JScrollPane(table)
    scrollPane.setMinimumSize([100, 50] as Dimension)

    add(scrollPane);
  }

  public void load(Context context) {
    context.getContextCommands()
    table.setModel(new ContextCommandTableModel(context))
    repaint()
  }

}