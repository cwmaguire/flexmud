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
import flexmud.net.Client;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class CommandChainCommand extends Command {
    List<? extends Command> commands;
    Context startingContext;

    public CommandChainCommand(List<? extends Command> commands) {
        this.commands = commands;
    }

    @Override
    public void setClient(Client client) {
        super.setClient(client);
        if (commands != null) {
            for (Command command : commands) {
                command.setClient(client);
            }
        }
    }

    @Override
    public List<String> getCommandArguments() {
        List<String> arguments = new ArrayList<String>();
        if (commands != null) {
            for (Command command : commands) {
                arguments.addAll(command.getCommandArguments());
            }
        }
        return arguments;
    }

    @Override
    public void run() {
        if (commands != null && !commands.isEmpty()) {
            Client client = commands.get(0).getClient();
            startingContext = client.getContext();

            for (Iterator<? extends Command> it = commands.iterator(); it.hasNext() && startingContext.equals(client.getContext());) {
                Command command = it.next();
                command.run();
            }
        }
    }
}
