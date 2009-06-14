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

package flexmud.engine.context;

import flexmud.net.Client;
import flexmud.engine.cmd.Command;
import flexmud.util.Util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class FakeClientContextHandler extends ClientContextHandler {
    private static Logger LOGGER = Logger.getLogger(FakeClientContextHandler.class);
    private List<String> lastCommandArguments;

    public FakeClientContextHandler(Client client) {
        super(client);
    }

    protected void initializeAndExecuteCommands(List<Command> commands) {
        if (commands != null) {
            for (Command command : commands) {
                LOGGER.debug("Received command " + command.getClass().getCanonicalName());
                lastCommandArguments = command.getCommandArguments();
            }

            for (Command command : commands) {

                LOGGER.debug("init & exec command " + command.getClass().getName());

                try {
                    initializeAndExecuteCommand(new SleepingCommand(command)).get();
                } catch (Exception e) {
                    LOGGER.error("init & exec command " + command.getClass().getName(), e);
                    return;
                }

                LOGGER.debug("Exec'd command " + command.getClass().getCanonicalName() + " at " + System.currentTimeMillis());
            }
        }
    }

    public List<String> getLastCommandArguments() {
        return lastCommandArguments;
    }

    private class SleepingCommand extends Command {
        private Command innerCommand;

        public SleepingCommand(Command innerCommand) {
            this.innerCommand = innerCommand;
        }

        @Override
        public void run() {
            innerCommand.run();
            Util.pause(3);
        }
    }
}
