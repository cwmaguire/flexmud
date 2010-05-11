package flexmud.cfg.editor

import flexmud.cfg.editor.gui.TabbedConfigFrame

public class FxCfg {

  public static void main(final String[] args) throws Exception {
    Controller controller = new Controller()
    TabbedConfigFrame tabbedConfigFrame = new TabbedConfigFrame(controller)
    controller.mainFrame = tabbedConfigFrame

    tabbedConfigFrame.pack()
    tabbedConfigFrame.setVisible(true)
  }
}
