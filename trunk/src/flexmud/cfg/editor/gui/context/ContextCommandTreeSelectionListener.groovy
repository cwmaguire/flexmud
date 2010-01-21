package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

public class ContextCommandTreeSelectionListener implements TreeSelectionListener {

  Controller controller

  def ContextCommandTreeSelectionListener(Controller controller) {
    this.controller = controller
  }

  /*
  e.source is the tree
  e.newLeadSelectionPath is the path of user objects to the user object for the node that was clicked
   */

  public void valueChanged(TreeSelectionEvent e) {
    controller.loadContextCommand(e.getNewLeadSelectionPath().getPath()[-1].userObject.contextCommand)
  }
}