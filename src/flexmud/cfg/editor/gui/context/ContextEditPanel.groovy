package flexmud.cfg.editor.gui.context

import flexmud.cfg.editor.Controller
import flexmud.engine.context.Context
import javax.swing.JLabel
import javax.swing.JPanel

public class ContextEditPanel extends JPanel {
  Controller controller
  JLabel nameLabel

  def ContextEditPanel(Controller controller) {
    this.controller = controller
    nameLabel = new JLabel()
    this.add(nameLabel)
  }

  def load(Context context) {
    // load up the context for editing
    nameLabel.text = context.name
  }
}