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
import org.apache.log4j.Logger;
import flexmud.net.FakeRemoteClient;
import flexmud.util.Util;

import java.io.IOException;
import java.util.UUID;

public class RemoteClientReadWriteTest {
    private static final Logger LOGGER = Logger.getLogger(RemoteClientReadWriteTest.class);
    private ClientCommunicator clientCommunicator;
    private FakeRemoteClient fakeRemoteClient;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Before
    public void setup() {
        int port = Util.getTestPort();
        clientCommunicator = Util.getNewFakeClientCommunicator(port, true);
        clientCommunicator.start();

        fakeRemoteClient = new FakeRemoteClient();
        try {
            fakeRemoteClient.connect(port);
        } catch (Exception e) {
            Assert.fail("Failed to connect client");
        }
    }

    @After
    public void tearDown(){
        Assert.assertNotNull("FakeRemoteClient not found", fakeRemoteClient);

        try{
            fakeRemoteClient.disconnect();
        }catch(Exception e){
            Assert.fail("Failed to disconnect client");
        }

        if (clientCommunicator != null) {
            clientCommunicator.stop();
        }
        clientCommunicator = null;

        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testConnectToClientListener() throws IOException {
        String testString = UUID.randomUUID().toString();
        byte[] buffer = testString.getBytes();
        char[] charsRead = new char[buffer.length];

        fakeRemoteClient.outStream.writeBytes(testString);
        fakeRemoteClient.inStream.read(buffer);

        LOGGER.info("Reading received bytes: ");
        for (int i = 0; i < buffer.length; ++i) {
            System.out.print((char) buffer[i]);
            charsRead[i] = (char) buffer[i];
        }
        LOGGER.info("\nFinished reading received bytes");

        Assert.assertEquals("Data received was not the same as data sent", testString, String.valueOf(charsRead));
    }
}
