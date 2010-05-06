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

class ContextCommandTableModel extends AbstractTableModel {

  List commands;
  def fields = ["id", "commandClassName", "contextCommandFlag", "description", "name", "sequence"]

  public ContextCommandTableModel(Context context) {
    commands = new ArrayList(context.getContextCommands());
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

  def Class<?> getColumnClass(int columnIndex) {
    return ContextCommand.getDeclaredField(fields[columnIndex]).class
  }

  def String getColumnName(int column) {
    return fields[column]
  }

  


}
