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
import flexmud.util.FakeRemoteClient;
import flexmud.util.Util;

public class ClientListenerConnectionTest {
    private int port;
    private ClientListener clientListener;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j config file"));
    }

    @Before
    public void setup() {
        port = Util.getTestPort();
        clientListener = Util.getNewClientListener(port);
        clientListener.start();
    }

    @After
    public void tearDown() {
        if (clientListener != null) {
            clientListener.stop();
        }
        clientListener = null;

        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testConnectToClientListener() {

        FakeRemoteClient fakeRemoteClient = new FakeRemoteClient();
        try {
            fakeRemoteClient.connect(port);
        } catch (Exception e) {
            Assert.fail("Failed to connect to client listener");
        }

        Assert.assertTrue("FakeRemoteClient socket is not connected.", fakeRemoteClient.socket.isConnected());
    }

}
