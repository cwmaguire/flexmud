/* Copyright */
package flexmud.engine.context;

import org.junit.Test;
import flexmud.net.ClientListener;
import flexmud.util.Util;
import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import junit.framework.Assert;

public class TestClientContextHandler {
    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j test config file"));
    }

    @Test
    public void testLoadFirstContextAndFirstChild(){
        ClientListener clientListener = null;
        ClientContextHandler clientContextHandler;
        Context firstContext;
        Context firstChildContext;

        try{
            clientListener = new ClientListener(Util.TEST_PORT);
        }catch(Exception e){
            Assert.fail("Could not create client listener on port " + Util.TEST_PORT + "\n" + e.getMessage());
        }

        clientContextHandler = new ClientContextHandler(null);
        clientContextHandler.loadFirstContext();

        firstContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first context or first context does not exist", firstContext);
        Assert.assertNull("First context should not have a parent context group", firstContext.getParentGroup());

        clientContextHandler.loadFirstChildContext();
        firstChildContext = clientContextHandler.getContext();

        Assert.assertNotNull("Unable to fetch first child context or first child context does not exist", firstChildContext);
    }
}
