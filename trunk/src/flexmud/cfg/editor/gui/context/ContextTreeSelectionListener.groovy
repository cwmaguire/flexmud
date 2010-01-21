package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import flexmud.cfg.editor.gui.context.ContextToStringWrapper
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode

public class ContextTreeSelectionListener implements TreeSelectionListener {
  public Controller controller

  public ContextTreeSelectionListener(Controller controller) {
    this.controller = controller;
  }

  /*
  e.source is the tree
  e.newLeadSelectionPath is the path of user objects to the user object for the node that was clicked
   */

  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()
    ContextToStringWrapper contextToStringWrapper = (ContextToStringWrapper) lastPathComponent.getUserObject()
    controller.loadContext(contextToStringWrapper.getContext())
  }


}