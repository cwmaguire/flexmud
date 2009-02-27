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
package net;

import org.apache.log4j.Logger;
import util.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class FakeClientListener extends ClientListener {
    private static Logger LOGGER = Logger.getLogger(FakeClientListener.class);
    private boolean shouldInterceptRead;


    public FakeClientListener() throws IOException {
        this(false);
    }

    public FakeClientListener(boolean shouldInterceptRead) throws IOException{
        super(Util.TEST_PORT);
        this.shouldInterceptRead = shouldInterceptRead;
    }

    protected void read(SelectionKey key){
        if(shouldInterceptRead){
            try{
                readFromSocketChannel((SocketChannel) key.channel());
            }catch(IOException e){
            }
        }else{
            super.read(key);
        }
    }

    public void readFromSocketChannel(SocketChannel socketChannel) throws ClosedChannelException {
        try {
            ByteBuffer eightBitCharBuffer = ByteBuffer.allocate(128);

            int read = socketChannel.read(eightBitCharBuffer);
            if (read == -1) {
                throw new IOException();
            }

            while (read > 0) {
                eightBitCharBuffer.flip();

                socketChannel.write(eightBitCharBuffer);

                read = socketChannel.read(eightBitCharBuffer);
                if (read == -1) {
                    throw new IOException();
                }
            }

        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }
}
