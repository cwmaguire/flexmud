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

import flexmud.db.HibernateUtil;
import flexmud.engine.cmd.Command;
import flexmud.engine.cmd.CommandChainCommand;
import flexmud.engine.cmd.ContextOrGenericPromptCommand;
import flexmud.engine.cmd.menu.MenuCommand;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import flexmud.security.Account;
import flexmud.security.AccountRole;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.concurrent.Future;

public class ClientContextHandler {
    private static final Logger LOGGER = Logger.getLogger(ClientContextHandler.class);

    private Context context;
    private Client client;
    private Map<Long, Integer> contextEntryCounts = new HashMap<Long, Integer>();

    public ClientContextHandler(Client client) {
        this.client = client;
    }

    public void init() {
        loadAndSetFirstContext();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context newContext) {
        setContext(newContext, true);
    }

    public synchronized void setContext(Context newContext, boolean isPromptRequired) {

        if (doesExistsCheckFail(newContext)) return;

        if(newContext.isSecure() && doesSecurityCheckFail(newContext)) return;

        if (doesMaxEntriesCheckFail(newContext)) return;

        incrementEntryCount(newContext);

        context = newContext;

        /* Future CM: it looks like CachedThreadPool executes jobs in order from a queue,
                but we may still need to put in a delay to prevent later commands
                from sneaking ahead of earlier commands
                We could also put in some sort of dependency check where if a command
                is processed out of order it gets put back in the queue
        */
        initializeAndExecuteCommand(createFlaggedContextCommandChain(isPromptRequired));
    }

    protected Command createFlaggedContextCommandChain(boolean isPromptRequired) {
        List<Command> commands = new ArrayList<Command>();
        Command menuCommand;

        commands.addAll(getEntryCommands());

        menuCommand = createMenuCommand();
        if (menuCommand != null) {
            commands.add(createMenuCommand());
        }

        if (isPromptRequired) {
            commands.add(getPromptCommand());
        }

        return new CommandChainCommand(commands);
    }

    private List<Command> getEntryCommands() {
        return getFlaggedCommandsWithParamsOrNull(ContextCommandFlag.ENTRY);
    }

    public Command getPromptCommand() {
        List<Command> flaggedPromptCommands = getFlaggedCommandsWithParamsOrNull(ContextCommandFlag.PROMPT);

        if ((flaggedPromptCommands != null && !flaggedPromptCommands.isEmpty())) {
            return flaggedPromptCommands.get(0);
        }

        return new ContextOrGenericPromptCommand();
    }

    private Command getDefaultCommand() {
        List<Command> flaggedDefaultCommands = getFlaggedCommandsWithParamsOrNull(ContextCommandFlag.DEFAULT);

        if ((flaggedDefaultCommands != null && !flaggedDefaultCommands.isEmpty())) {
            return flaggedDefaultCommands.get(0);
        }

        return null;
    }

    public Command createMenuCommand() {
        MenuCommand menuCommand;
        List<ContextCommand> cntxtMenuItems = getAccessibleContextCommands(context.getFlaggedContextCommands(ContextCommandFlag.MENU_ITEM));

        if ((cntxtMenuItems != null && !cntxtMenuItems.isEmpty())) {
            menuCommand = new MenuCommand();
            menuCommand.setMenuContextCommands(cntxtMenuItems);
            return menuCommand;
        }

        return null;
    }

    public List<ContextCommand> getAccessibleContextCommands(List<? extends ContextCommand> commands){
        List<ContextCommand> accessibleCommands = null;
        Account account = client.getAccount();
        AccountRole role = account == null ? null : account.getAccountRole();

        if(role != null && commands != null){
            accessibleCommands = new ArrayList<ContextCommand>();
            for(ContextCommand command : commands){
                if(role.hasPermission(command)){
                    accessibleCommands.add(command);
                }
            }
        }

        return accessibleCommands;
    }

    private boolean doesMaxEntriesCheckFail(Context newContext) {
        if (isMaxEntriesExceeded(newContext)) {
            LOGGER.info("Max context entries exceeded for client " + client.getConnectionID() + ", disconnecting");
            client.sendTextLn(context.getMaxEntriesExceededMessage());
            client.sendTextLn("disconnecting");
            client.disconnect();
            return true;
        }
        return false;
    }

    private boolean doesSecurityCheckFail(Context newContext) {
        Account account = client.getAccount();
        AccountRole role = account == null ? null : account.getAccountRole();

        if (role == null || !role.hasPermission(newContext)) {
            LOGGER.info("Client " + client.getConnectionID() + " does not have permissions on context " + newContext.getName());
            client.sendTextLn("Access denied");
            // ToDO CM: need to reprompt at this point.
            //          or, we could simply re-enter them in their current context
            return true;
        }
        return false;
    }

    private boolean doesExistsCheckFail(Context newContext) {
        if (newContext == null && context == null) {
            LOGGER.error("Could not locate first context");
            client.sendText("Houston, we have a problem: we don't know where to send you. Disconnecting, sorry.");
            client.disconnect();
            return true;
        } else if (newContext == null) {
            LOGGER.error("Tried to send to null context, keeping client in old context.");
            client.sendText("The area you are trying to get to doesn't seem to exist.");
            // ToDO CM: need to reprompt at this point.
            //          or, we could simply re-enter them in their current context
            return true;
        }
        return false;
    }

    protected List<Command> getFlaggedCommandsWithParamsOrNull(ContextCommandFlag flag) {
        Command command;
        List<Command> commands = new ArrayList<Command>();
        List<ContextCommand> cntxtCmds = context.getFlaggedContextCommands(flag);

        if (cntxtCmds != null) {
            for (ContextCommand cntxtCmd : cntxtCmds) {
                try {
                    command = (Command) Class.forName(cntxtCmd.getCommandClassName()).newInstance();
                } catch (Exception e) {
                    LOGGER.error("Could not instantiate flagged command [" + cntxtCmd.getCommandClassName() +
                            "] for context [" + context.getName() +
                            "] and flag [" + flag.name() + "]", e);
                    continue;
                }
                addParameters(command, cntxtCmd);
                commands.add(command);
            }
        }

        // Future CM: we could establish a dependency chain right here.

        return commands;
    }

    private void addParameters(Command cmd, ContextCommand cntxtCmd) {
        List<ContextCommandParameter> cntxtCmdParams = new ArrayList<ContextCommandParameter>(cntxtCmd.getParameters());
        List<String> parameters = new ArrayList<String>();

        Collections.sort(cntxtCmdParams, new SequenceComparator());

        for (ContextCommandParameter cntxtCmdParam : cntxtCmdParams) {
            parameters.add(cntxtCmdParam.getValue());
        }

        cmd.setCommandArguments(parameters);
    }

    protected void initializeAndExecuteCommands(List<Command> commands) {
        if (commands != null) {
            for (Command command : commands) {
                initializeAndExecuteCommand(command);
            }
        }
    }

    protected Future initializeAndExecuteCommand(Command command) {
        if (command != null) {
            LOGGER.debug("Executing command " + command.getClass().getName());
            command.setClient(client);
            return Executor.exec(command);
        }

        return null;
    }

    private boolean isMaxEntriesExceeded(Context newContext) {
        Integer contextCount;
        contextCount = contextEntryCounts.get(newContext.getId());

        return contextCount != null && context.getMaxEntries() > 0 && contextCount >= context.getMaxEntries();

    }

    private void incrementEntryCount(Context context) {
        Integer entryCount = contextEntryCounts.get(context.getId());
        contextEntryCounts.put(context.getId(), entryCount == null ? 1 : entryCount + 1);
    }

    public void loadAndSetFirstContext() {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Context.class);
        detachedCriteria.add(Restrictions.isNull(Context.PARENT_GROUP_PROPERTY));
        List<Context> contexts = (List<Context>) HibernateUtil.fetch(detachedCriteria);

        Context firstContext;
        if (contexts != null && !contexts.isEmpty()) {
            firstContext = contexts.get(0);
            firstContext.init();
            setContext(firstContext);
        } else {
            setContext(null);
        }
    }

    public void loadAndSetFirstChildContext() {
        ContextGroup childContextGroup = context.getChildGroup();
        List<Context> childContexts = new ArrayList<Context>(childContextGroup.getChildContexts());
        Context firstContext;
        if (!childContexts.isEmpty()) {
            firstContext = childContexts.get(0);
            firstContext.init();
            setContext(firstContext);
        } else {
            setContext(null);
        }
    }

    public void runCommand(String commandString) {
        Command command;

        if (commandString == null || commandString.trim().isEmpty()) {
            initializeAndExecuteCommand(getPromptCommand());
            return;
        }

        command = loadMatchingCommandOrDefault(commandString);

        if (command == null) {
            // Future CM: put something fun in here like random phrases "What?!" "Huh?" "Does not compute", etc.
            // "Hal reports that he's sorry, but he can't do that"
            client.sendTextLn("An error occurred trying to run \"" + commandString + "\"");
            initializeAndExecuteCommand(getPromptCommand());
            return;
        }

        initializeAndExecuteCommand(command);
    }

    private Command loadMatchingCommandOrDefault(String commandString) {
        ContextCommand contextCommand;
        Command command;
        List<String> commandTokens = tokenize(commandString);

        contextCommand = context.getContextCommandForAlias(commandTokens.get(0));

        if (contextCommand != null) {
            command = contextCommand.createCommandInstance();
            command.setCommandArguments(determineParameters(contextCommand.getParameters(), commandTokens));
        } else {
            command = getDefaultCommand();
            command.setCommandArguments(commandTokens);
        }

        return command;
    }

    private List<String> tokenize(String commandString) {
        StringTokenizer stringTokenizer = new StringTokenizer(commandString);
        List<String> words = new ArrayList<String>();

        while (stringTokenizer.hasMoreElements()) {
            words.add(stringTokenizer.nextToken());
        }

        return words;
    }

    private List<String> determineParameters(Set<ContextCommandParameter> preconfiguredParams, List<String> commandLineParams) {
        List<ContextCommandParameter> parameters = new ArrayList<ContextCommandParameter>(preconfiguredParams);
        if (preconfiguredParams != null && !preconfiguredParams.isEmpty()) {
            Collections.sort(parameters, new SequenceComparator());
            return getParameterValues(parameters);
        } else {
            return commandLineParams;
        }
    }

    private List<String> getParameterValues(List<ContextCommandParameter> preconfiguredParams) {
        List<String> parameterValues = new ArrayList<String>();
        if (preconfiguredParams != null) {
            for (ContextCommandParameter param : preconfiguredParams) {
                parameterValues.add(param.getValue());
            }
        }
        return parameterValues;
    }

}
