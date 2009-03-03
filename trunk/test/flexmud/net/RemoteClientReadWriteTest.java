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

import java.io.IOException;
import java.util.UUID;

public class RemoteClientReadWriteTest {
    private static ClientListener clientListener;
    private static FakeRemoteClient fakeRemoteClient;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j config file"));
    }

    @Before
    public void setup() {
        clientListener = Util.getNewFakeClientListener(true);
        clientListener.start();

        fakeRemoteClient = new FakeRemoteClient();
        try {
            fakeRemoteClient.connect(Util.TEST_PORT);
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

        if (clientListener != null) {
            clientListener.stop();
        }
        clientListener = null;

        Util.pause(Util.CLIENT_SHUTDOWN_WAIT_TIME);
    }

    @Test
    public void testConnectToClientListener() throws IOException {
        String testString = UUID.randomUUID().toString();
        byte[] buffer = testString.getBytes();
        char[] charsRead = new char[buffer.length];

        fakeRemoteClient.outStream.writeBytes(testString);
        fakeRemoteClient.inStream.read(buffer);

        System.out.println("Reading received bytes: ");
        for (int i = 0; i < buffer.length; ++i) {
            System.out.print((char) buffer[i]);
            charsRead[i] = (char) buffer[i];
        }
        System.out.println("\nFinished reading received bytes");

        Assert.assertEquals("Data received was not the same as data sent", testString, String.valueOf(charsRead));
    }
}