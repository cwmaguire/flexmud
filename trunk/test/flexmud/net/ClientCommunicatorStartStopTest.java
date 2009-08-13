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
import flexmud.util.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClientCommunicatorStartStopTest {
    private static ClientCommunicator clientCommunicator;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testClientCommunicatorStarts(){
        clientCommunicator = Util.getNewClientCommunicator();
        Assert.assertNotNull("Client communicator was not created", clientCommunicator);

        clientCommunicator.start();
        Assert.assertTrue("Client communicator is not running", clientCommunicator.isRunning());
        Assert.assertTrue("Client communicator cannot accept connections", clientCommunicator.canAcceptConnection());
        Assert.assertTrue("Client communicator will not run commands", clientCommunicator.shouldRunCommands());
    }

    @Test
    public void testClientCommunicatorStops(){

        if (clientCommunicator != null) {
            clientCommunicator.stop();
            Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
        }

        Assert.assertNotNull("Client communicator should not be null", clientCommunicator);
        Assert.assertFalse("Client communicator should not be running", clientCommunicator.isRunning());
        Assert.assertFalse("Client communicator should not run commands", clientCommunicator.shouldRunCommands());

        clientCommunicator = null;
    }
}