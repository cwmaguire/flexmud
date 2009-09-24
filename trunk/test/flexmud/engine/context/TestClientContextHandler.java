/* Copyright */
package flexmud.engine.context;

import flexmud.cfg.Preferences;
import flexmud.db.HibernateUtil;
import flexmud.engine.cmd.*;
import flexmud.engine.exec.Executor;
import flexmud.log.LoggingUtil;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.util.ContextUtil;
import flexmud.util.Util;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TestClientContextHandler {
    private FakeClientCommunicator fakeClientCommunicator = null;
    private List<Object> objectsToDelete;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Before
    public void setup(){
        objectsToDelete = new ArrayList<Object>();
        fakeClientCommunicator = new FakeClientCommunicator();
        fakeClientCommunicator.setShouldInterceptWrite(true);

        ContextCommand entryCntxtCmd1 = ContextUtil.createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);
        ContextCommand promptContextCommand = ContextUtil.createContextCommand(TestCmd.class, ContextCommandFlag.PROMPT);

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

    @After
    public void tearDown(){
        Collections.reverse(objectsToDelete);
        for(Object obj : objectsToDelete){
            HibernateUtil.delete(obj);
        }
    }

    @Test
    public void testLoadFirstContextAndFirstChild(){
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        ClientContextHandler clientContextHandler = client.getClientContextHandler();
        Context firstContext;
        Context firstChildContext;

        TestCmd.resetRunCount();

        clientContextHandler.loadAndSetFirstContext();

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Context entry and prompt commands did not both run", 2, TestCmd.getRunCount());

        firstContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first context or first context does not exist", firstContext);
        Assert.assertNull("First context should not have a parent context group", firstContext.getParentGroup());

        clientContextHandler.loadAndSetFirstChildContext();
        firstChildContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first child context or first child context does not exist", firstChildContext);
    }

    @Test
    public void testExceedingMaxContextEntriesDisconnectsClient(){
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
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
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);

        clientContextHandler.setContext(null);

        Assert.assertFalse(client.isConnected());
    }

    @Test
    public void testEntryCommandIsRunOnContextEntry(){
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        FakeClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        ContextCommand entryCntxtCmd = ContextUtil.createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);

        Context contextWithEntryCmd = new Context();
        contextWithEntryCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd)));
        contextWithEntryCmd.init();

        TestCmd.resetRunCount();

        fakeClientCntxtHndlr.setContext(contextWithEntryCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Entry command did not run on context entry", 1, TestCmd.getRunCount());
    }

    @Test
    public void testCommandHasParameters() {
        String testParam1 = UUID.randomUUID().toString();
        String testParam2 = UUID.randomUUID().toString();
        List<String> paramList = Arrays.asList(testParam1, testParam2);

        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        FakeClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        ContextCommand entryCntxtCmd = ContextUtil.createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);
        entryCntxtCmd.setParameters(createContextCommandParameters(entryCntxtCmd, paramList));

        Context contextWithEntryCmd = new Context();
        contextWithEntryCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd)));
        contextWithEntryCmd.init();

        TestCmd.resetRunCount();

        fakeClientCntxtHndlr.setContext(contextWithEntryCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertTrue("Command did not contain specified parameters", fakeClientCntxtHndlr.getLastCommandArguments().containsAll(paramList));
    }

    private Set<ContextCommandParameter> createContextCommandParameters(ContextCommand cntxtCmd, List<String> parameters){
        Set<ContextCommandParameter> cntxtCmdParams = new HashSet<ContextCommandParameter>();
        for(String param : parameters){
            cntxtCmdParams.add(createContextCommandParameter(cntxtCmd, param));
        }
        return cntxtCmdParams;
    }

    private ContextCommandParameter createContextCommandParameter(ContextCommand cntxtCmd, String parameter){
        ContextCommandParameter cntxtCmdParam = new ContextCommandParameter();
        cntxtCmdParam.setContextCommand(cntxtCmd);
        cntxtCmdParam.setValue(parameter);
        return cntxtCmdParam;
    }

    @Test
    public void testPromptCommandIsRunOnContextEntry() {
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        ClientContextHandler fakeClientCntxtHndlr = new ClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);
        Context contextWithEntryCommand = new Context();
        ContextCommand promptContextCommand = new ContextCommand();
        promptContextCommand.setCommandClassName(TestCmd.class.getName());
        promptContextCommand.setContextCommandFlag(ContextCommandFlag.PROMPT);
        contextWithEntryCommand.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(promptContextCommand)));
        contextWithEntryCommand.init();

        TestCmd.resetRunCount();

        fakeClientCntxtHndlr.setContext(contextWithEntryCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Prompt command did not run on context entry", 1, TestCmd.getRunCount());
    }

    @Test
    public void testMultipleCommandsWithSameFlagAreRunInSequenceOrder(){
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        ClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        Context contextWithEntryCmd = new Context();

        ContextCommand entryCntxtCmd0 = ContextUtil.createContextCommand(TestCmd.class, ContextCommandFlag.ENTRY);
        // setting this explicity for clarity
        entryCntxtCmd0.setSequence(0);
        entryCntxtCmd0.setContext(contextWithEntryCmd);

        ContextCommand entryCntxtCmd1 = ContextUtil.createContextCommand(TestCmd2.class, ContextCommandFlag.ENTRY);
        entryCntxtCmd1.setSequence(1);
        entryCntxtCmd1.setContext(contextWithEntryCmd);

        ContextCommand entryCntxtCmd2 = ContextUtil.createContextCommand(TestCmd3.class, ContextCommandFlag.ENTRY);
        entryCntxtCmd2.setSequence(2);
        entryCntxtCmd2.setContext(contextWithEntryCmd);

        contextWithEntryCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(entryCntxtCmd0, entryCntxtCmd1, entryCntxtCmd2)));
        contextWithEntryCmd.init();

        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();
        TestCmd3.resetRunCount();

        fakeClientCntxtHndlr.setContext(contextWithEntryCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Zeroth entry command did not run on context entry", 1, TestCmd.getRunCount());
        Assert.assertEquals("First entry command did not run on context entry", 1, TestCmd2.getRunCount());
        Assert.assertEquals("Second entry command did not run on context entry", 1, TestCmd3.getRunCount());
        Assert.assertTrue("First entry command was not run before second entry command: 1 = " + TestCmd2.getLastRunMillis() + ", 2 = " + TestCmd3.getLastRunMillis(), TestCmd2.getLastRunMillis() < TestCmd3.getLastRunMillis());
        Assert.assertTrue("Second entry command was not run before zeroth entry command: 2 = " + TestCmd2.getLastRunMillis() + ", 0 = " + TestCmd3.getLastRunMillis(), TestCmd3.getLastRunMillis() < TestCmd.getLastRunMillis());
    }

    @Test
    public void testSpecifiedCommandIsRun(){
        String uniqueCntxtCmdAlias = UUID.randomUUID().toString();
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        FakeClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        Context cntxtWithSpecifiedCmd = new Context();
        ContextCommand specifiedCntxtCmd = ContextUtil.createContextCommand(TestCmd.class);
        ContextCommand defaultCntxtCmd = ContextUtil.createContextCommand(TestCmd2.class, ContextCommandFlag.DEFAULT);
        ContextCommandAlias specifiedCntxtCmdAlis = new ContextCommandAlias();

        specifiedCntxtCmdAlis.setAlias(uniqueCntxtCmdAlias);
        specifiedCntxtCmd.setAliases(new HashSet<ContextCommandAlias>(Arrays.asList(specifiedCntxtCmdAlis)));

        cntxtWithSpecifiedCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(specifiedCntxtCmd, defaultCntxtCmd)));
        cntxtWithSpecifiedCmd.init();

        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();

        fakeClientCntxtHndlr.setContext(cntxtWithSpecifiedCmd);
        fakeClientCntxtHndlr.runCommand(uniqueCntxtCmdAlias);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Specified command did not run", 1, TestCmd.getRunCount());
        Assert.assertEquals("Default command should not have run", 0, TestCmd2.getRunCount());
    }

    @Test
    public void testDefaultCommandIsRun(){
        String uniqueCntxtCmdAlias = UUID.randomUUID().toString();
        FakeClient client = new FakeClient(fakeClientCommunicator, null);
        FakeClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        Context cntxtWithSpecifiedCmd = new Context();
        ContextCommand specifiedCntxtCmd = ContextUtil.createContextCommand(TestCmd.class);
        ContextCommand defaultCntxtCmd = ContextUtil.createContextCommand(TestCmd2.class, ContextCommandFlag.DEFAULT);
        ContextCommandAlias specifiedCntxtCmdAlis = new ContextCommandAlias();

        specifiedCntxtCmdAlis.setAlias(uniqueCntxtCmdAlias);
        specifiedCntxtCmd.setAliases(new HashSet<ContextCommandAlias>(Arrays.asList(specifiedCntxtCmdAlis)));

        cntxtWithSpecifiedCmd.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(specifiedCntxtCmd, defaultCntxtCmd)));
        cntxtWithSpecifiedCmd.init();

        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();

        fakeClientCntxtHndlr.setContext(cntxtWithSpecifiedCmd);
        fakeClientCntxtHndlr.runCommand(uniqueCntxtCmdAlias + "_GARBAGE");

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Specified command does not exist and should not have run", 0, TestCmd.getRunCount());
        Assert.assertEquals("Default command should have run", 1, TestCmd2.getRunCount());
    }

    @Test
    public void testChainCommandDoesNotRunAfterContextSwitch(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        FakeClient client = new FakeClient(clientCommunicator, null);

        FakeClientContextHandler fakeClientCntxtHndlr = new FakeClientContextHandler(client);
        client.setClientContextHandler(fakeClientCntxtHndlr);

        Context loginCntxt = ContextUtil.createContextHierarchy();

        client.setContext(loginCntxt);

        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();

        CommandChainCommand cmdChainCmd = new CommandChainCommand(Arrays.asList(new TestCmd(), new TestSwitchToChildContextCommand(), new TestCmd2()));
        cmdChainCmd.setClient(client);

        fakeClientCntxtHndlr.setContext(loginCntxt);
        Executor.exec(cmdChainCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("TestCmd should have run once since it's before the context switch command in the chain ", 1, TestCmd.getRunCount());
        Assert.assertEquals("TestSwitchToChildContextCommand did not switch to child context", loginCntxt.getChildGroup().getChildContexts().iterator().next(), client.getContext());
        Assert.assertEquals("TestCmd2 should NOT have run since it's after the context switch command in the chain ", 0, TestCmd2.getRunCount());
    }
}
