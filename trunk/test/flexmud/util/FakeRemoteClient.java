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

import java.net.Socket;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FakeRemoteClient {
    public Socket socket;
    public DataInputStream inStream;
    public DataOutputStream outStream;

    public void connect(int port) throws IOException{
        connect(InetAddress.getLocalHost(), port);
    }

    public void connect(InetAddress inetAddress, int port) throws IOException {
        socket = new Socket(inetAddress, port);
        inStream = new DataInputStream(this.socket.getInputStream());
        outStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void disconnect() throws IOException {
        socket.close();
        inStream = null;
        outStream = null;
    }
}
