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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class FakeClientCommunicator extends ClientCommunicator {
    private static Logger LOGGER = Logger.getLogger(FakeClientCommunicator.class);
    private boolean shouldInterceptRead;
    private boolean shouldInterceptWrite;
    private String lastSentText;
    private List<String> sentText;


    public FakeClientCommunicator(){
        super();
        sentText = new ArrayList<String>();
    }

    public FakeClientCommunicator(int port) throws IOException{
        super(port);
        sentText = new ArrayList<String>();
    }

    public void setShouldInterceptRead(boolean shouldInterceptRead) {
        this.shouldInterceptRead = shouldInterceptRead;
    }

    public void setShouldInterceptWrite(boolean shouldInterceptWrite) {
        this.shouldInterceptWrite = shouldInterceptWrite;
    }

    public String getLastSentText() {
        return lastSentText;
    }

    public List<String> getSentText(){
        return sentText;
    }

    protected void read(SelectionKey key){
        if(shouldInterceptRead){
            try{
                readFromSocketChannel((SocketChannel) key.channel());
            }catch(IOException e){
                LOGGER.error("Error reading from socket channel", e);
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

    @Override
    public void send(SocketChannel sockChan, String text) {
        if (shouldInterceptWrite) {
            lastSentText = text;
            sentText.add(text);
        } else {
            super.send(sockChan, text);
        }

    }
}
