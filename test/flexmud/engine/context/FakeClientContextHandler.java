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

import flexmud.engine.cmd.Command;
import flexmud.engine.cmd.CommandChainCommand;
import flexmud.net.Client;
import flexmud.util.Util;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.Future;

public class FakeClientContextHandler extends ClientContextHandler {
    private static Logger LOGGER = Logger.getLogger(FakeClientContextHandler.class);
    private List<String> lastCommandArguments;

    public FakeClientContextHandler(Client client) {
        super(client);
    }

    public List<String> getLastCommandArguments() {
        return lastCommandArguments;
    }

    @Override
    protected Future initializeAndExecuteCommand(Command command) {

        LOGGER.debug("init & exec command " + command.getClass().getName());

        lastCommandArguments = command.getCommandArguments();

        try {
            super.initializeAndExecuteCommand(command);//.get();
        } catch (Exception e) {
            LOGGER.error("init & exec command " + command.getClass().getName(), e);
            return null;
        }

        LOGGER.debug("Exec'd command " + command.getClass().getCanonicalName() + " at " + System.currentTimeMillis());
        return null;
    }
}
