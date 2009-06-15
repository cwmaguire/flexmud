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
import flexmud.sec.Account;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.apache.log4j.Logger;

public class LoginCommand extends Command{
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class);

    @Override
    public void run() {
        client.setLogin(getCommandArguments().get(0));
        LOGGER.info("Client " + client.getConnectionID() + " logging in with login \"" + client.getLogin() + "\"");
        client.getContextHandler().loadAndSetFirstChildContext();
    }
}