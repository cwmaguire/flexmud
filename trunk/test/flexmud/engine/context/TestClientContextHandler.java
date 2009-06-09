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
import flexmud.engine.cmd.EchoCmd;
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

        Context context1 = new Context("ctxt1");
        context1.setMaxEntries(1);
        ContextGroup contextGroup = new ContextGroup();
        context1.setChildGroup(contextGroup);
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
        entryContextCommand.setCommandClassName(EchoCmd.class.getName());
        entryContextCommand.setContextCommandFlag(ContextCommandFlag.ENTRY);
        contextWithEntryCommand.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryContextCommand)));
        contextWithEntryCommand.init();

        clientContextHandler.setContext(contextWithEntryCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertTrue("Entry command did not run on context entry", EchoCmd.hasRun());

    }
}
