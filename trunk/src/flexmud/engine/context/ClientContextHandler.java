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
import flexmud.engine.cmd.PromptCommand;
import flexmud.engine.exec.Executor;
import flexmud.net.Client;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.*;

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
        initializeAndExecuteCommand(getFlaggedCommandOrNull(ContextCommandFlag.ENTRY));
        initializeAndExecuteCommand(getSpecificOrGenericPromptCommand());
    }

    private Command getSpecificOrGenericPromptCommand() {
        Command promptCommand = getFlaggedCommandOrNull(ContextCommandFlag.PROMPT);

        if (promptCommand == null && context.getPrompt() != null) {
            promptCommand = new PromptCommand();
        }
        return promptCommand;
    }

    private Command getFlaggedCommandOrNull(ContextCommandFlag flag) {
        Command command = null;
        Class commandClass = context.getFlaggedCommandClass(flag);
        if (commandClass != null) {
            try {
                command = (Command) commandClass.newInstance();
            } catch (Exception e) {
                LOGGER.error("Could not instantiate flagged command [" + commandClass.getName() + "] for context [" +
                        context.getName() + "] and flag [" + flag.name() + "]", e);
            }
        }
        return command;
    }

    private boolean doesMaxEntriesCheckFail(Context newContext) {
        if (isMaxEntriesExceeded(newContext)) {
            client.sendTextLn(context.getMaxEntriesExceededMessage());
            client.sendTextLn("disconnecting");
            client.disconnect();
            return true;
        }
        return false;
    }

    private boolean doesContextCheckFail(Context newContext) {
        if (newContext == null && context == null) {
            client.sendText("Houston, we have a problem: we don't know where to send you. Disconnecting, sorry.");
            client.disconnect();
            LOGGER.error("Could not locate first context");
            return true;
        } else if (newContext == null) {
            client.sendText("The area you are trying to get to doesn't seem to exist.");
            // ToDO CM: need to reprompt at this point.
            LOGGER.error("Tried to send to null context, keeping client in old context.");
            return true;
        }
        return false;
    }

    private void initializeAndExecuteCommand(Command command) {
        if (command != null) {
            command.setClient(client);
            Executor.exec(command);
        }
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
        List<Context> contexts;
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Context.class);
        detachedCriteria.add(Restrictions.isNull(Context.PARENT_GROUP_PROPERTY));
        contexts = (List<Context>) HibernateUtil.fetch(detachedCriteria);

        if (contexts != null && !contexts.isEmpty()) {
            setContext(contexts.get(0));
        } else {
            setContext(null);
        }
    }

    public void loadFirstChildContext() {
        ContextGroup childContextGroup = context.getChildGroup();
        List<Context> childContexts = new ArrayList<Context>(childContextGroup.getChildContexts());
        if (!childContexts.isEmpty()) {
            setContext(childContexts.get(0));
        } else {
            setContext(null);
        }
    }

    public void runCommand(String commandString) {
        StringTokenizer stringTokenizer;
        Class commandClass;
        Command command;

        if (commandString == null || commandString.trim().isEmpty()) {
            initializeAndExecuteCommand(getSpecificOrGenericPromptCommand());
            return;
        }

        stringTokenizer = new StringTokenizer(commandString);

        commandClass = context.getCommandClassForAlias(getNextWord(stringTokenizer));
        try {
            command = (Command) commandClass.newInstance();
        } catch (Exception e) {
            LOGGER.error("Could not instantiate Command for class " + commandClass.getName(), e);
            client.sendTextLn("An error occurred trying to run \'" + commandString + "\"");
            initializeAndExecuteCommand(getSpecificOrGenericPromptCommand());
            return;
        }
        command.setCommandArguments(getRemainingWords(stringTokenizer));
    }

    private String getNextWord(StringTokenizer stringTokenizer) {
        if (stringTokenizer.hasMoreElements()) {
            return stringTokenizer.nextToken();
        } else {
            return "";
        }
    }

    private List<String> getRemainingWords(StringTokenizer stringTokenizer) {
        List<String> words = new ArrayList<String>();
        while (stringTokenizer.hasMoreElements()) {
            words.add(stringTokenizer.nextToken());
        }

        return words;
    }
}
