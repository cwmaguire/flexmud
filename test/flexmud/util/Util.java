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

import org.apache.log4j.Logger;

public class Util {
    private static Logger LOGGER = Logger.getLogger(ClientCommunicator.class);
    public static int CURRENT_TEST_PORT = 54321;
    public static int CLIENT_SHUTDOWN_WAIT_TIME = 1000;
    public static int ENGINE_WAIT_TIME = 500;

    public static ClientCommunicator getNewClientCommunicator() throws IOException{
        return getNewClientCommunicator(getTestPort());
    }

    public static ClientCommunicator getNewClientCommunicator(int port) throws IOException{
        return new ClientCommunicator(port);
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

    public static String getRandomSubstring(String string, int length){
        int start;

        if(length >= string.length()){
            return string;
        }else if(length <= 0){
            return "";
        }

        start = getRandomBounded(string.length() - length);

        return string.substring(start, start + length);
    }

    public static int getRandomBounded(int maxLength){
        return getRandomBounded(0, maxLength);
    }

    public static int getRandomBounded(int minLength, int maxLength) {
        int random = (int) Math.ceil(Math.random() * (maxLength - minLength)) + minLength;
        if(random == 0){
            random = minLength == 0 ? 1 : minLength;
        }
        return random;
    }
}
