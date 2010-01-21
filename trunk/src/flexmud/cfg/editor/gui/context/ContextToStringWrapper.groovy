package flexmud.cfg.editor.gui.context

import flexmud.engine.context.Context

public class ContextToStringWrapper {
  Context context;

  def ContextToStringWrapper(Context context) {
    this.context = context;
  }

  public String toString() {
    return context?.name
  }
}