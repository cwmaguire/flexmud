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

package flexmud.engine.cmd;

import flexmud.engine.context.Context;
import flexmud.db.HibernateUtil;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class SetContextCommand extends Command{

    @Override
    public void run() {
        List<String> cmdArguments = getCommandArguments();
        if(cmdArguments.isEmpty()){
           return;
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(Context.class);
        criteria.add(Restrictions.eq(Context.ID_PROPERTY, Long.parseLong(cmdArguments.get(0))));

        List<Context> contexts = HibernateUtil.fetch(criteria);

        if(contexts != null && !contexts.isEmpty()){
            client.setContext(contexts.get(0));
        }
    }
}
