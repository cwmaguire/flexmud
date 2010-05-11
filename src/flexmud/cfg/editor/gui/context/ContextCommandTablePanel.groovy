package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import flexmud.engine.context.Context
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.JComboBox
import flexmud.engine.context.ContextCommandFlag

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
    table.setModel(new ContextCommandTableModel(context, controller))

    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()))
    table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox(ContextCommandFlag.values())))
    table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()))
    table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()))
    table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField()))

    repaint()
  }

}