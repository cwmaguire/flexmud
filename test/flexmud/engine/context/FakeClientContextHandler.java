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

import org.apache.log4j.Logger;

public class FakeClientContextHandler extends ClientContextHandler {
    private static Logger LOGGER = Logger.getLogger(FakeClientContextHandler.class);
    public static Boolean LOCKED = false;

    public FakeClientContextHandler(Client client) {
        super(client);
    }

    protected void initializeAndExecuteCommands(List<Command> commands) {
        if (commands != null) {
            for (Command command : commands) {

                LOGGER.debug("init & exec command " + command.getClass().getName());

                synchronized(LOCKED){
                    LOCKED = true;
                }

                initializeAndExecuteCommand(new LockingCommand(command));

                while(true){
                    synchronized(LOCKED){
                        if(LOCKED){
                            LOGGER.debug("locked is true");
                            Util.pause(100);
                        }else{
                            break;
                        }
                    }
                }


            }
        }
    }

    private class LockingCommand extends Command {
        private Command innerCommand;

        public LockingCommand(Command innerCommand) {
            this.innerCommand = innerCommand;
        }

        @Override
        public void run() {

            synchronized (LOCKED) {
                innerCommand.run();
                LOGGER.debug("unlocking");
                LOCKED = false;
            }
        }
    }
}
