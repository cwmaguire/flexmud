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

public class ClientCommunicatorConnectionTest {
    private int port;
    private ClientCommunicator clientCommunicator;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Before
    public void setup() {
        port = Util.getTestPort();
        clientCommunicator = Util.getNewClientCommunicator(port);
        clientCommunicator.start();
    }

    @After
    public void tearDown() {
        if (clientCommunicator != null) {
            clientCommunicator.stop();
        }
        clientCommunicator = null;

        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testConnectToClientCommunicator() {

        FakeRemoteClient fakeRemoteClient = new FakeRemoteClient();
        try {
            fakeRemoteClient.connect(port);
        } catch (Exception e) {
            Assert.fail("Failed to connect to client communicator");
        }

        Assert.assertTrue("FakeRemoteClient socket is not connected.", fakeRemoteClient.socket.isConnected());
    }

}
