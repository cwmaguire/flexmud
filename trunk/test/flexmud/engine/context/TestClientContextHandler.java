/* Copyright */
package flexmud.engine.context;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import flexmud.db.HibernateUtil;
import flexmud.engine.cmd.TestCmd;
import flexmud.util.Util;
import junit.framework.Assert;

import java.util.*;

public class TestClientContextHandler {
    private FakeClientCommunicator clientCommunicator = null;
    private List<Object> objectsToDelete;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Before
    public void setup(){
        objectsToDelete = new ArrayList<Object>();
        clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        ContextCommand entryContextCommand = new ContextCommand();
        entryContextCommand.setCommandClassName(TestCmd.class.getName());
        entryContextCommand.setContextCommandFlag(ContextCommandFlag.ENTRY);

        ContextCommand promptContextCommand = new ContextCommand();
        promptContextCommand.setCommandClassName(TestCmd.class.getName());
        promptContextCommand.setContextCommandFlag(ContextCommandFlag.PROMPT);

        Context context1 = new Context("ctxt1");
        context1.setMaxEntries(1);
        ContextGroup contextGroup = new ContextGroup();
        context1.setChildGroup(contextGroup);
        context1.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryContextCommand, promptContextCommand)));
        entryContextCommand.setContext(context1);
        promptContextCommand.setContext(context1);
        HibernateUtil.save(context1);
        objectsToDelete.add(context1);

        Context context2 = new Context("ctxt2");
        context2.setParentGroup(contextGroup);
        HibernateUtil.save(context2);
        objectsToDelete.add(context2);
    }

    @After
    public void tearDown(){
        Collections.reverse(objectsToDelete);
        for(Object obj : objectsToDelete){
            HibernateUtil.delete(obj);
        }
    }

    @Test
    public void testLoadFirstContextAndFirstChild(){
        ClientContextHandler clientContextHandler;
        Context firstContext;
        Context firstChildContext;

        clientContextHandler = new ClientContextHandler(null);
        clientContextHandler.loadAndSetFirstContext();

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Context entry and prompt commands did not both run", 2, TestCmd.getRunCount());

        firstContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first context or first context does not exist", firstContext);
        Assert.assertNull("First context should not have a parent context group", firstContext.getParentGroup());

        clientContextHandler.loadFirstChildContext();
        firstChildContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first child context or first child context does not exist", firstChildContext);
    }

    @Test
    public void testExceedingMaxContextEntriesDisconnectsClient(){
        FakeClient client = new FakeClient(clientCommunicator, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);
        Context context;

        clientContextHandler.loadAndSetFirstContext();
        context = clientContextHandler.getContext();
        for(int i = 0; i < context.getMaxEntries(); i++){
            clientContextHandler.setContext(context);
        }

        Assert.assertFalse(client.isConnected());
    }

    @Test
    public void testNullContextDisconnectsClient(){
        FakeClient client = new FakeClient(clientCommunicator, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);

        clientContextHandler.setContext(null);

        Assert.assertFalse(client.isConnected());
    }

    @Test
    public void testEntryCommandIsRunOnContextEntry(){
        FakeClient client = new FakeClient(clientCommunicator, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);
        Context contextWithEntryCommand = new Context();
        ContextCommand entryContextCommand = new ContextCommand();
        entryContextCommand.setCommandClassName(TestCmd.class.getName());
        entryContextCommand.setContextCommandFlag(ContextCommandFlag.ENTRY);
        contextWithEntryCommand.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryContextCommand)));
        contextWithEntryCommand.init();

        TestCmd.resetRunCount();

        clientContextHandler.setContext(contextWithEntryCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Entry command did not run on context entry", 0, TestCmd.getRunCount());
    }

    @Test
    public void testPromptCommandIsRunOnContextEntry() {
        FakeClient client = new FakeClient(clientCommunicator, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);
        Context contextWithEntryCommand = new Context();
        ContextCommand promptContextCommand = new ContextCommand();
        promptContextCommand.setCommandClassName(TestCmd.class.getName());
        promptContextCommand.setContextCommandFlag(ContextCommandFlag.PROMPT);
        contextWithEntryCommand.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(promptContextCommand)));
        contextWithEntryCommand.init();

        TestCmd.resetRunCount();

        clientContextHandler.setContext(contextWithEntryCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Prompt command did not run on context entry", 0, TestCmd.getRunCount());
    }
}
