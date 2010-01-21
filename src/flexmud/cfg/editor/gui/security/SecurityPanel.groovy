package flexmud.cfg.editor.gui.security

import flexmud.cfg.editor.Controller
import javax.swing.JLabel
import javax.swing.JPanel

public class SecurityPanel extends JPanel {
  Controller controller
  String title = "Security"

  public SecurityPanel() {
    add(new JLabel("Security Tab"))
  }

}