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

import flexmud.cfg.Constants;
import flexmud.engine.cmd.Command;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.ContextCommandAlias;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import org.apache.log4j.Logger;

import java.util.Set;

public class HelpCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(HelpCommand.class);

    @Override
    public void run() {
        Client client = getClient();
        Context context = client.getContext();
        Set<ContextCommand> contextCommands = context.getContextCommands();
        StringBuilder commandsAndDescriptions = new StringBuilder();

        commandsAndDescriptions.append(Constants.CRLF).append("Commands: ").append(Constants.CRLF);

        for (ContextCommand cmd : contextCommands) {
            if (cmd.getName() != null) {
                commandsAndDescriptions.append(cmd.getName())
                        .append("\t")
                        .append(getAliases(cmd))
                        .append("\t\t")
                        .append(cmd.getDescription())
                        .append(Constants.CRLF);
            }
        }

        LOGGER.info("Sending help string [" + commandsAndDescriptions.toString() + "] to client " + client.getConnectionID());
        client.sendTextLn(commandsAndDescriptions.toString());

        Command promptCommand = client.getClientContextHandler().getPromptCommand();
        promptCommand.setClient(client);
        Executor.exec(promptCommand);
    }

    private String getAliases(ContextCommand cmd) {
        StringBuilder aliases = new StringBuilder();

        for (ContextCommandAlias alias : cmd.getAliases()) {
            if (aliases.length() > 0) {
                aliases.append(", ");
            }
            aliases.append(alias.getAlias());
        }

        return aliases.toString();
    }

}