/* Copyright */
package flexmud.engine.context;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import flexmud.net.ClientListener;
import flexmud.net.Client;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientListener;
import flexmud.util.Util;
import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import junit.framework.Assert;

public class TestClientContextHandler {
    private FakeClientListener clientListener = null;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j test config file"));
    }

    @Before
    public void setup(){
        clientListener = new FakeClientListener();
    }

    @Test
    public void testLoadFirstContextAndFirstChild(){
        ClientContextHandler clientContextHandler;
        Context firstContext;
        Context firstChildContext;

        clientContextHandler = new ClientContextHandler(null);
        clientContextHandler.loadFirstContext();

        firstContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first context or first context does not exist", firstContext);
        Assert.assertNull("First context should not have a parent context group", firstContext.getParentGroup());

        clientContextHandler.loadFirstChildContext();
        firstChildContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first child context or first child context does not exist", firstChildContext);
    }

    @Test
    public void testExceedingMaxContextEntriesDisconnectsClient(){
        FakeClient client = new FakeClient(clientListener, null);
        ClientContextHandler clientContextHandler = new ClientContextHandler(client);
        Context firstChildContext;

        clientContextHandler.loadFirstContext();
        clientContextHandler.loadFirstChildContext();
        firstChildContext = clientContextHandler.getContext();
        clientContextHandler.setContext(firstChildContext);

        Assert.assertFalse(client.isConnected());

    }
}
