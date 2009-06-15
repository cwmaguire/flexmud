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
import flexmud.engine.cmd.ContextOrGenericPromptCommand;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.concurrent.Future;

public class ClientContextHandler {
    private static final Logger LOGGER = Logger.getLogger(ClientContextHandler.class);

    private Context context;
    private Client client;
    private Map<Context, Integer> contextEntryCounts = new HashMap<Context, Integer>();

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

        if (doesContextCheckFail(newContext)) return;

        if (doesMaxEntriesCheckFail(newContext)) return;

        incrementEntryCount(newContext);

        context = newContext;

        /* ToDo CM: it looks like CachedThreadPool executes jobs in order from a queue,
           ToDo     but we may still need to put in a delay to prevent later commands
           ToDo     from sneaking ahead of earlier commands
           ToDo     We could also put in some sort of dependency check where if a command
           ToDo     is processed out of order it gets put back in the queue
        */
        initializeAndExecuteCommands(getFlaggedCommandsWithParamsOrNull(ContextCommandFlag.ENTRY));
        initializeAndExecuteCommand(getPromptCommand());
    }

    private Command getPromptCommand() {
        List<Command> flaggedPromptCommands = getFlaggedCommandsWithParamsOrNull(ContextCommandFlag.PROMPT);

        if ((flaggedPromptCommands != null && !flaggedPromptCommands.isEmpty()) ) {
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

    private boolean doesContextCheckFail(Context newContext) {
        if (newContext == null && context == null) {
            LOGGER.info("Could not locate first context");
            client.sendText("Houston, we have a problem: we don't know where to send you. Disconnecting, sorry.");
            client.disconnect();
            return true;
        } else if (newContext == null) {
            LOGGER.info("Tried to send to null context, keeping client in old context.");
            client.sendText("The area you are trying to get to doesn't seem to exist.");
            // ToDO CM: need to reprompt at this point.
            // ToDo CM: or, we could simply re-enter them in their current context
            return true;
        }
        return false;
    }

    private List<Command> getFlaggedCommandsWithParamsOrNull(ContextCommandFlag flag) {
        Command command;
        List<Command> commands = new ArrayList<Command>();
        List<ContextCommand> cntxtCmds = context.getFlaggedCommandClasses(flag);

        if (cntxtCmds != null) {
            for(ContextCommand cntxtCmd : cntxtCmds){
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

        // ToDo CM: we could establish a dependency chain right here.

        return commands;
    }

    private void addParameters(Command cmd, ContextCommand cntxtCmd){
        List<ContextCommandParameter> cntxtCmdParams = new ArrayList<ContextCommandParameter>(cntxtCmd.getParameters());
        List<String> parameters = new ArrayList<String>();

        Collections.sort(cntxtCmdParams, new SequenceComparator());

        for(ContextCommandParameter cntxtCmdParam : cntxtCmdParams){
            parameters.add(cntxtCmdParam.getValue());
        }

        cmd.setCommandArguments(parameters);
    }


    protected void initializeAndExecuteCommands(List<Command> commands) {
        if (commands != null) {
            for(Command command : commands){
                initializeAndExecuteCommand(command);
            }
        }
    }

    protected Future initializeAndExecuteCommand(Command command) {
        if (command != null) {
            LOGGER.info("Executing command " + command.getClass().getName());
            command.setClient(client);
            return Executor.exec(command);
        }

        return null;
    }

    private boolean isMaxEntriesExceeded(Context newContext) {
        Integer contextCount;
        contextCount = contextEntryCounts.get(newContext);

        return contextCount != null && context.getMaxEntries() > 0 && contextCount >= context.getMaxEntries();

    }

    private void incrementEntryCount(Context context) {
        Integer entryCount = contextEntryCounts.get(context);
        contextEntryCounts.put(context, entryCount == null ? 1 : entryCount + 1);
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

    public void loadFirstChildContext() {
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
        StringTokenizer stringTokenizer;
        Class commandClass;
        Command command = null;

        if (commandString == null || commandString.trim().isEmpty()) {
            initializeAndExecuteCommand(getPromptCommand());
            return;
        }

        stringTokenizer = new StringTokenizer(commandString);

        commandClass = context.getCommandClassForAlias(getNextWord(stringTokenizer));
        try {
            command = (Command) commandClass.newInstance();
            command.setCommandArguments(getRemainingWords(stringTokenizer));
        } catch (Exception e) {
            LOGGER.error("Could not instantiate Command for class " + (commandClass == null ? "[null]" : commandClass.getName()), e);
        }

        if(command == null){
            command = getDefaultCommand();
            // the default command gets _all_ the words, including the "command" (i.e. the first word)
            command.setCommandArguments(tokenize(commandString));
        }

        if(command == null){
            client.sendTextLn("An error occurred trying to run \'" + commandString + "\"");
            initializeAndExecuteCommand(getPromptCommand());
            return;
        }

        initializeAndExecuteCommand(command);
    }

    private String getNextWord(StringTokenizer stringTokenizer) {
        if (stringTokenizer.hasMoreElements()) {
            return stringTokenizer.nextToken();
        } else {
            return "";
        }
    }

    private List<String> getRemainingWords(StringTokenizer stringTokenizer) {
    }

    private List<String> tokenize(String commandString){
        StringTokenizer stringTokenizer = new StringTokenizer(commandString);
        List<String> words = new ArrayList<String>();

        while (stringTokenizer.hasMoreElements()) {
            words.add(stringTokenizer.nextToken());
        }

        return words;
    }
}
