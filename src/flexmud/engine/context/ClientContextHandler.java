/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

MUD Cartographer is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MUD Cartographer is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MUD Cartographer.  If not, see <http://www.gnu.org/licenses/>.
 */
package flexmud.engine.context;

import flexmud.db.HibernateUtil;
import flexmud.engine.cmd.Command;
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

    public ClientContextHandler(Client client){
        this.client = client;
    }

    public void init(){
        setContext(fetchFirstContext());
    }

    public Context getContext(){
        return context;
    }

    public void setContext(Context newContext){

        if (newContext == null && context == null) {
            client.sendText("Houston, we have a problem: we don't know where to send you. Disconnecting, sorry.");
            LOGGER.error("Could not locate first context");
            client.disconnect();
            return;
        }else if(newContext == null){
            client.sendText("The area you are trying to get to doesn't seem to exist.");
            // ToDO CM: need to reprompt at this point.
            LOGGER.error("Tried to send to null context, keeping client in old context.");
            return;
        }

        if(isMaxEntriesExceeded(newContext)){
            client.sendTextLn(context.getMaxEntriesExceededMessage());
            client.sendTextLn("disconnecting");
            client.disconnect();
            return;
        }

        incrementEntryCount(newContext);

        context = newContext;

        /* ToDo CM: it looks like CachedThreadPool executes jobs in order from a queue,
           ToDo     but we may still need to put in a delay to prevent later commands
           ToDo     from sneaking ahead of earlier commands
           ToDo     We could also put in some sort of dependency check where if a command
           ToDo     is processed out of order it gets put back in the queue
        */
        initializeAndExecuteCommand(context.getEntryCommand());
        initializeAndExecuteCommand(context.getPromptCommand());
    }

    private void initializeAndExecuteCommand(Command command) {
        command.setClient(client);
        Executor.exec(command);
    }

    private boolean isMaxEntriesExceeded(Context newContext) {
        Integer contextCount;
        contextCount = contextEntryCounts.get(newContext);

        return contextCount != null && context.getMaxEntries() >= 0 && contextCount == context.getMaxEntries();

    }

    private void incrementEntryCount(Context context){
        Integer entryCount = contextEntryCounts.get(context);
        contextEntryCounts.put(context, entryCount == null ? 1 : entryCount + 1);
    }

    public Context fetchFirstContext() {
        List<Context> contexts;
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Context.class);
        detachedCriteria.add(Restrictions.isNull(Context.PARENT_GROUP_PROPERTY));
        contexts = (List<Context>) HibernateUtil.fetch(detachedCriteria);
        if (contexts != null && !contexts.isEmpty()) {
            return contexts.get(0);
        } else {
            return null;
        }
    }

    public Context getFirstChildContext() {
        ContextGroup childContextGroup = context.getChildGroup();
        List<Context> childContexts = new ArrayList<Context>(childContextGroup.getChildContexts());
        if (!childContexts.isEmpty()) {
            return childContexts.get(0);
        }

        return null;
    }

    public void runCommand(String commandString){
        StringTokenizer stringTokenizer;
        String[] tokens;
        String commandAlias;
        String arguments;
        Command command;

        if(commandString == null || commandString.trim().isEmpty()){
            initializeAndExecuteCommand(context.getPromptCommand());
            return;
        }

        stringTokenizer = new StringTokenizer(commandString);

        command = context.getCommandForAlias(getNextWord(stringTokenizer));
        command.setCommandArguments(getRemainingWords(stringTokenizer));
    }

    private String getNextWord(StringTokenizer stringTokenizer){
        if(stringTokenizer.hasMoreElements()){
            return stringTokenizer.nextToken();
        }else{
            return "";
        }
    }

    private List<String> getRemainingWords(StringTokenizer stringTokenizer){
        List<String> words = new ArrayList<String>();
        while (stringTokenizer.hasMoreElements()) {
            words.add(stringTokenizer.nextToken());
        }

        return words;
    }
}
