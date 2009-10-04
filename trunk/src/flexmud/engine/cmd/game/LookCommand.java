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

package flexmud.engine.cmd.game;

import flexmud.db.HibernateUtil;
import flexmud.engine.context.Message;
import flexmud.engine.cmd.Command;
import flexmud.engine.cmd.MessageCommand;
import flexmud.engine.cmd.ContextOrGenericPromptCommand;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class LookCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(LookCommand.class);

    @Override
    public void run() {
        Client client = getClient();

        LOGGER.info("Client " + client.getConnectionID() + " ran \"Look\" command");
        client.sendTextLn("You look. Very observant of you.");

        Command promptCommand = client.getClientContextHandler().getPromptCommand();
        promptCommand.setClient(client);
        Executor.exec(promptCommand);
    }

}