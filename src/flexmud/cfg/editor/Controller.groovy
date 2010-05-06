package flexmud.cfg.editor

import flexmud.cfg.editor.gui.context.ContextCommandTablePanel
import flexmud.cfg.editor.gui.context.ContextEditPanel
import flexmud.cfg.editor.gui.context.ContextTreePanel
import flexmud.cfg.editor.gui.security.SecurityPanel
import flexmud.db.HibernateUtil
import flexmud.engine.context.Context
import flexmud.engine.context.ContextCommand
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Restrictions

public class Controller {
  ContextEditPanel contextEditPanel
  ContextTreePanel contextTreePanel
  ContextCommandTablePanel contextCommandTreePanel

  SecurityPanel securityPanel

  def loadContexts() {
    DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Context.class);
    detachedCriteria.add(Restrictions.isNull(Context.PARENT_GROUP_PROPERTY));
    List<Context> contexts = (List<Context>) HibernateUtil.fetch(detachedCriteria);
    contextTreePanel.load(contexts[0])
  }

  def loadContextTree(Context context) {
    contextTreePanel.load(context)
  }

  def loadContext(Context context) {
    contextEditPanel.load(context)
    contextCommandTreePanel.load(context)
  }

  def loadContextCommand(ContextCommand contextCommand) {
    //contextCommandTreePanel.load(contextCommand)
  }
}