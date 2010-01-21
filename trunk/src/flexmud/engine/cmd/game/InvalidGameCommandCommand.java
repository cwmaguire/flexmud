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

import flexmud.engine.cmd.Command;
import flexmud.engine.context.ClientContextHandler;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class InvalidGameCommandCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(InvalidGameCommandCommand.class);

    @Override
    public void run() {
        List<Command> commands = new ArrayList<Command>();
        Client client = getClient();
        ClientContextHandler clientContextHandler = client.getClientContextHandler();

        client.sendTextLn("Huh?");

        Command promptCommand = clientContextHandler.getPromptCommand();

        promptCommand.setClient(client);

        Executor.exec(promptCommand);
    }
}