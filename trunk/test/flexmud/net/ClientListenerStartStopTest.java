/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

flexmud is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

flexmud is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with flexmud.  If not, see <http://www.gnu.org/licenses/>.
 */
package flexmud.net;

import flexmud.cfg.Preferences;
import flexmud.log.LoggingUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import flexmud.util.Util;

public class ClientListenerStartStopTest {
    private static ClientListener clientListener;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j config file"));
    }

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testClientListenerStarts(){
        clientListener = Util.getNewClientListener();
        Assert.assertNotNull("ClientListener was not created", clientListener);

        clientListener.start();
        Assert.assertTrue("ClientListener is not running", clientListener.isRunning());
        Assert.assertTrue("ClientListener cannot accept connections", clientListener.canAcceptConnection());
        Assert.assertTrue("ClientListener will not run commands", clientListener.shouldRunCommands());
    }

    @Test
    public void testClientListenerStops(){

        if (clientListener != null) {
            clientListener.stop();
        }

        Assert.assertNotNull("ClientListener should not be null", clientListener);
        Assert.assertFalse("ClientListener should not be running", clientListener.isRunning());
        Assert.assertFalse("ClientListener should not run commands", clientListener.shouldRunCommands());

        clientListener = null;
    }
}