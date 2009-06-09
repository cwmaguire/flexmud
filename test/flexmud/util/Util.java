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
package flexmud.util;

import flexmud.net.ClientCommunicator;
import flexmud.net.FakeClientCommunicator;

import java.io.IOException;

public class Util {
    public static int CURRENT_TEST_PORT = 54321;
    public static int CLIENT_SHUTDOWN_WAIT_TIME = 1000;
    public static int ENGINE_WAIT_TIME = 1000;

    public static ClientCommunicator getNewClientCommunicator(){
        return getNewClientCommunicator(getTestPort());
    }

    public static ClientCommunicator getNewClientCommunicator(int port) {
        try {
            return new ClientCommunicator(port);
        } catch (IOException e) {
            return null;
        }
    }

    public static FakeClientCommunicator getNewFakeClientCommunicator(){
        return getNewFakeClientCommunicator(false);
    }

    public static FakeClientCommunicator getNewFakeClientCommunicator(boolean shouldInterceptRead) {
        return getNewFakeClientCommunicator(Util.getTestPort(), shouldInterceptRead);
    }

    public static FakeClientCommunicator getNewFakeClientCommunicator(int port, boolean shouldInterceptRead) {
        FakeClientCommunicator fakeClientCommunicator;
        try {
            fakeClientCommunicator = new FakeClientCommunicator(port);
        } catch (IOException e) {
            return null;
        }
        fakeClientCommunicator.setShouldInterceptRead(shouldInterceptRead);
        return fakeClientCommunicator;
    }

    public static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static int getTestPort(){
        return CURRENT_TEST_PORT++;
    }

}
