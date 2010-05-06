package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller

import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import javax.swing.JTree

public class ContextEditLayoutPanel extends JPanel {
  Controller controller
  ContextEditPanel contextEditPanel;
  ContextCommandTablePanel contextCommandTreePanel
  String title = "Contexts"
  JTree tree

  def ContextEditLayoutPanel(Controller controller) {

    super(new GridLayout(1, 0))

    this.controller = controller

    contextEditPanel = new ContextEditPanel(controller);
    controller.contextEditPanel = contextEditPanel;

    contextCommandTreePanel = new ContextCommandTablePanel(controller)
    controller.contextCommandTreePanel = contextCommandTreePanel

    JTabbedPane tabbedPane = new JTabbedPane()
    tabbedPane.addTab(contextCommandTreePanel.title, contextCommandTreePanel)

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
    splitPane.setTopComponent(contextEditPanel)
    splitPane.setBottomComponent(tabbedPane)
    splitPane.setDividerLocation(200)
    splitPane.setPreferredSize(new Dimension(600, 300))

    add(splitPane);
  }
}
