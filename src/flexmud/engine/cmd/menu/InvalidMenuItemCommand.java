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

import flexmud.engine.exec.Executor;
import flexmud.engine.context.ClientContextHandler;
import flexmud.engine.cmd.Command;
import flexmud.engine.cmd.CommandChainCommand;
import flexmud.net.Client;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

public class InvalidMenuItemCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(InvalidMenuItemCommand.class);

    @Override
    public void run() {
        List<Command> commands = new ArrayList<Command>();
        Command menuCommand;
        CommandChainCommand commandChainCommand;
        Client client = getClient();
        ClientContextHandler clientContextHandler = client.getClientContextHandler();

        client.sendTextLn("Invalid menu item");

        menuCommand = clientContextHandler.createMenuCommand();
        if (menuCommand != null) {
            commands.add(menuCommand);
        }

        commands.add(clientContextHandler.getPromptCommand());

        commandChainCommand = new CommandChainCommand(commands);
        commandChainCommand.setClient(client);

        Executor.exec(commandChainCommand);
    }
}