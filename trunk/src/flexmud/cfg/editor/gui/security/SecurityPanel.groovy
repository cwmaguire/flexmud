package flexmud.cfg.editor.gui.security

import flexmud.cfg.editor.Controller
import javax.swing.JLabel
import javax.swing.JPanel

public class SecurityPanel extends JPanel {
  Controller controller
  String title = "Security"

  /**
   * Creates a new security panel for managing user security (not just player security)
   */
  public SecurityPanel() {
    add(new JLabel("Security Tab"))
  }

}