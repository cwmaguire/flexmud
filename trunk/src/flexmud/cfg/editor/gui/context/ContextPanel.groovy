package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import flexmud.cfg.editor.gui.context.ContextEditLayoutPanel
import flexmud.cfg.editor.gui.context.ContextTreePanel
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTree

public class ContextPanel extends JPanel {

  Controller controller
  ContextTreePanel contextTreePanel
  ContextEditLayoutPanel contextEditLayoutPanel
  String title = "Contexts"
  JTree tree

  public ContextPanel(Controller controller) {
    super(new GridLayout(1, 0))
    this.controller = controller

    contextTreePanel = new ContextTreePanel(controller)
    controller.contextTreePanel = contextTreePanel

    contextEditLayoutPanel = new ContextEditLayoutPanel(controller)

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setTopComponent(contextTreePanel);
    splitPane.setBottomComponent(contextEditLayoutPanel);
    splitPane.setDividerLocation(200);
    splitPane.setPreferredSize(new Dimension(1200, 700));

    add(splitPane);
  }
}