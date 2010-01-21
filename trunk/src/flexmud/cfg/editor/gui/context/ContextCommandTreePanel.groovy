package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import flexmud.cfg.editor.gui.context.ContextCommandTreeSelectionListener
import flexmud.cfg.editor.gui.context.ContextToStringWrapper
import flexmud.engine.context.Context
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

public class ContextCommandTreePanel extends JPanel {
  String title = "Commands"
  Controller controller
  JTree tree

  def ContextCommandTreePanel(Controller controller) {

    super(new GridLayout(1, 0));

    this.controller = controller

    tree = new JTree(new DefaultMutableTreeNode("Commands"));
    tree.addTreeSelectionListener(new ContextCommandTreeSelectionListener(controller))
    tree.setShowsRootHandles(true)

    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setMinimumSize([100, 50] as Dimension)

    add(scrollPane);
  }

  public void load(Context context) {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new ContextToStringWrapper(context))
    tree.setModel(new DefaultTreeModel(rootNode))
    context.getChildGroup()?.childContexts?.each {addNode(rootNode, it)}
    tree.expandPath(tree.getSelectionPath())
    repaint()
  }

  private void addNode(DefaultMutableTreeNode parentNode, Context context) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ContextToStringWrapper(context))
    parentNode.add(node)
    context.getChildGroup()?.childContexts?.each {addNode(node, it)}
  }

}