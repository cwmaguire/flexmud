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

package flexmud.menu;

import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.ContextCommandAlias;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ContextCommandMenuItemRenderer {
    public static String render(ContextCommand contextCommand){
        String contextDescription = contextCommand.getDescription();

        List<ContextCommandAlias> aliases = new ArrayList <ContextCommandAlias>(contextCommand.getAliases());
        // this will ensure accelerators are taken care of before bullets, but
        // mix and match multiple bullets and accelerators at your own risk.
        //Collections.sort(aliases);

        if(!aliases.isEmpty()){
            for(ContextCommandAlias alias : aliases){
                contextDescription = ContextMenuAliasDecorator.decorate(contextDescription, alias);
            }
        }
        return contextDescription;
    }

}
