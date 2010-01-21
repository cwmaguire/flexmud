/**************************************************************************************************
 * Copyright 2009 Chris Maguire (cwmaguire@gmail.com)                                             *
 *                                                                                                *
 * Flexmud is free software: you can redistribute it and/or modify                                *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 *                                                                                                *
 * Flexmud is distributed in the hope that it will be useful,                                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 *                                                                                                *
 * You should have received a copy of the GNU General Public License                              *
 * along with flexmud.  If not, see <http://www.gnu.org/licenses/>.                               *
 **************************************************************************************************/

package flexmud.engine.cmd.menu;

import flexmud.cfg.Constants;
import flexmud.engine.cmd.Command;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.SequenceComparator;
import flexmud.menu.ContextCommandMenuItemRenderer;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class MenuCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(MenuCommand.class);

    private List<ContextCommand> menuContextCommands;

    public void setMenuContextCommands(List<ContextCommand> menuContextCommands) {
        this.menuContextCommands = menuContextCommands;
    }

    @Override
    public void run() {
        StringBuilder menu = new StringBuilder();

        if (menuContextCommands != null) {
            Collections.sort(menuContextCommands, new SequenceComparator());
            for (ContextCommand ctxCommand : menuContextCommands) {
                menu.append(ContextCommandMenuItemRenderer.render(ctxCommand));
                menu.append(Constants.CRLF);
            }
        }

        getClient().sendText(menu.toString());
    }

}