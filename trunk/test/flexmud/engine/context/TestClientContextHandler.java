/* Copyright */
package flexmud.engine.context;

import org.junit.Test;
import flexmud.net.Client;
import flexmud.net.ClientListener;
import flexmud.util.Util;
import junit.framework.Assert;

public class TestClientContextHandler {

    @Test
    public void testLoadFirstContext(){
        ClientListener clientListener = null;
        ClientContextHandler clientContextHandler;
        Context firstContext;

        try{
            clientListener = new ClientListener(Util.TEST_PORT);
        }catch(Exception e){
            Assert.fail("Could not create client listener on port " + Util.TEST_PORT + "\n" + e.getMessage());
        }

        clientContextHandler = new ClientContextHandler(new Client(clientListener, null));

        firstContext = clientContextHandler.fetchFirstContext();

        Assert.assertNotNull("Unable to fetch first context or first context does not exist", firstContext);
        Assert.assertNull("First context should not have a parent context", firstContext.getParentContext());
    }
}
