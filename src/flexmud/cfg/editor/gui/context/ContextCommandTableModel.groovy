/*
 * Copyright 2009 Chris Maguire (cwmaguire@gmail.com)
 *
 * Flexmud is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flexmud is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with flexmud.  If not, see <http://www.gnu.org/licenses/>.
 */

package flexmud.cfg.editor.gui.context

import flexmud.engine.context.Context
import javax.swing.table.AbstractTableModel
import flexmud.engine.context.ContextCommand

import flexmud.cfg.editor.Controller
import flexmud.db.HibernateUtil
import flexmud.engine.context.ContextCommandAlias

class ContextCommandTableModel extends AbstractTableModel {

  List commands
  def fields = ["id", "commandClassName", "contextCommandFlag", "description", "name", "sequence"]
  Controller controller

  public ContextCommandTableModel(Context context, Controller controller) {
    commands = new ArrayList(context.getContextCommands())
    this.controller = controller
  }

  @Override
  public int getRowCount() {
    return commands.size()
  }

  @Override
  public int getColumnCount() {
    return fields.size()
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return commands[rowIndex]."${fields[columnIndex]}"
  }

  def void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Class fieldClass = ContextCommand.getDeclaredField(fields[columnIndex]).type;
    if(fieldClass == String.class){
      commands[rowIndex]."${fields[columnIndex]}" = aValue
      HibernateUtil.save commands[rowIndex]
    }else if(fieldClass == Integer.class){
      if(((String)aValue).isInteger()){
        commands[rowIndex]."${fields[columnIndex]}" = ((String) aValue).toInteger()
        HibernateUtil.save commands[rowIndex]
      }else{
        controller.showMessage("Could not convert $aValue to an Integer")
      }
    }else{
      controller.showMessage("Uh, I'm not sure how to set the value on that column")
    }
  }

  def Class<?> getColumnClass(int columnIndex) {
    return ContextCommand.getDeclaredField(fields[columnIndex]).class
  }

  def String getColumnName(int column) {
    return fields[column]
  }

  def boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex != 0
  }


}
