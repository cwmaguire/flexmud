package flexmud.cfg.editor.gui

import flexmud.cfg.editor.Controller
import flexmud.cfg.editor.gui.context.ContextPanel
import flexmud.cfg.editor.gui.security.SecurityPanel
import java.awt.event.ActionListener
import javax.swing.*

public class TabbedConfigFrame extends JFrame {
  public Controller controller

  public TabbedConfigFrame(Controller controller) {
    super('FX CFG')
    this.controller = controller
    setSize(1500, 1000)
    setDefaultCloseOperation(EXIT_ON_CLOSE)

    setJMenuBar(createMenuBar())

    add(createAndPopulateTabbedPane())
  }

  private JTabbedPane createAndPopulateTabbedPane() {
    JTabbedPane tabbedPane = new JTabbedPane()
    ContextPanel contextPanel = new ContextPanel(controller)
    SecurityPanel securityPanel = new SecurityPanel(controller: controller)

    tabbedPane.addTab(contextPanel.title, contextPanel)
    tabbedPane.addTab(securityPanel.title, securityPanel)

    return tabbedPane
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar()
    JMenu databaseMenu = new JMenu("Database")
    JMenuItem loadContextsMenuItem = new JMenuItem("Load Contexts")
    JMenuItem saveContextsMenuItem = new JMenuItem("Save Contexts")

    loadContextsMenuItem.addActionListener({controller.loadContexts()} as ActionListener)

    databaseMenu.add(loadContextsMenuItem)
    databaseMenu.add(saveContextsMenuItem)

    menuBar.add(databaseMenu)
    return menuBar
  }


}