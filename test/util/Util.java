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
package util;

import net.ClientListener;
import net.FakeClientListener;

import java.io.IOException;

public class Util {
    public static int TEST_PORT = 54321;
    public static int CLIENT_SHUTDOWN_WAIT_TIME = 1000;

    public static ClientListener getNewClientListener(){
        try {
            return new ClientListener(TEST_PORT);
        } catch (IOException e) {
            return null;
        }
    }

    public static FakeClientListener getNewFakeClientListener(){
        return getNewFakeClientListener(false);
    }

    public static FakeClientListener getNewFakeClientListener(boolean shouldInterceptRead) {
        try {
            return new FakeClientListener(shouldInterceptRead);
        } catch (IOException e) {
            return null;
        }
    }

    public static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
