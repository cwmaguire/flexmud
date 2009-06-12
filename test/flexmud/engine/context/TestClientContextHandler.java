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
import flexmud.engine.cmd.TestCmd2;
import flexmud.engine.cmd.TestCmd3;
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

        ContextCommand entryCntxtCmd1 = createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);
        ContextCommand promptContextCommand = createContextCommand(TestCmd.class, ContextCommandFlag.PROMPT);

        Context context1 = new Context("ctxt1");
        context1.setMaxEntries(1);
        ContextGroup contextGroup = new ContextGroup();
        context1.setChildGroup(contextGroup);
        context1.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd1, promptContextCommand)));
        entryCntxtCmd1.setContext(context1);
        promptContextCommand.setContext(context1);
        HibernateUtil.save(context1);
        objectsToDelete.add(context1);

        Context context2 = new Context("ctxt2");
        context2.setParentGroup(contextGroup);
        HibernateUtil.save(context2);
        objectsToDelete.add(context2);
    }

    private ContextCommand createContextCommand(Class clazz, ContextCommandFlag flag) {
        ContextCommand entryCntxtCmd1 = new ContextCommand();
        entryCntxtCmd1.setCommandClassName(clazz.getName());
        entryCntxtCmd1.setContextCommandFlag(flag);
        return entryCntxtCmd1;
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

        TestCmd.resetRunCount();

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
        Context contextWithEntryCmd = new Context();
        ContextCommand entryCntxtCmd = createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);

        contextWithEntryCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd)));
        contextWithEntryCmd.init();

        TestCmd.resetRunCount();

        clientContextHandler.setContext(contextWithEntryCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Entry command did not run on context entry", 1, TestCmd.getRunCount());
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

        Assert.assertEquals("Prompt command did not run on context entry", 1, TestCmd.getRunCount());
    }

    @Test
    public void testMultipleCommandsWithSameFlagAreRunInSequenceOrder(){
        FakeClient client = new FakeClient(clientCommunicator, null);
        ClientContextHandler clientContextHandler = new FakeClientContextHandler(client);
        Context contextWithEntryCmd = new Context();

        ContextCommand entryCntxtCmd0 = createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);
        // setting this explicity for clarity
        entryCntxtCmd0.setSequence(0);
        entryCntxtCmd0.setContext(contextWithEntryCmd);

        ContextCommand entryCntxtCmd1 = createContextCommand(TestCmd2.class, ContextCommandFlag.ENTRY);
        entryCntxtCmd1.setSequence(1);
        entryCntxtCmd1.setContext(contextWithEntryCmd);

        ContextCommand entryCntxtCmd2 = createContextCommand(TestCmd3.class, ContextCommandFlag.ENTRY);
        entryCntxtCmd2.setSequence(2);
        entryCntxtCmd2.setContext(contextWithEntryCmd);

        contextWithEntryCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd0, entryCntxtCmd1, entryCntxtCmd2)));
        contextWithEntryCmd.init();

        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();
        TestCmd3.resetRunCount();

        clientContextHandler.setContext(contextWithEntryCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Zeroth entry command did not run on context entry", 1, TestCmd.getRunCount());
        Assert.assertEquals("First entry command did not run on context entry", 1, TestCmd2.getRunCount());
        Assert.assertEquals("Second entry command did not run on context entry", 1, TestCmd3.getRunCount());
        Assert.assertTrue("First entry command was not run before second entry command: 1 = " + TestCmd2.getLastRunMillis() + ", 2 = " + TestCmd3.getLastRunMillis(), TestCmd2.getLastRunMillis() < TestCmd3.getLastRunMillis());
        Assert.assertTrue("Second entry command was not run before zeroth entry command: 2 = " + TestCmd2.getLastRunMillis() + ", 0 = " + TestCmd3.getLastRunMillis(), TestCmd3.getLastRunMillis() < TestCmd.getLastRunMillis());

    }
}
