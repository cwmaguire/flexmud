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

import flexmud.cfg.Constants;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextSwitcher;
import flexmud.sec.Account;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class);
    protected UUID connID;
    protected SocketChannel socketChannel = null;
    private ClientState connState;
    protected ClientListener clientListener;
    protected CommandBuffer cmdBuffer;
    protected ContextSwitcher contextSwitcher;

    private Account account;

    public Client(ClientListener clientListener, SocketChannel socketChannel) {
        this.clientListener = clientListener;
        this.socketChannel = socketChannel;

        cmdBuffer = new CommandBuffer();
        connID = clientListener.getNewConnectionID();
        contextSwitcher = new ContextSwitcher(this);

        contextSwitcher.init();
    }

    public UUID getConnectionID() {
        return this.connID;
    }

    public ClientListener getClientListener() {
        return this.clientListener;
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public CommandBuffer getCmdBuffer() {
        return cmdBuffer;
    }

    public void disconnect() {
        this.clientListener.disconnect(this);
        try {
            socketChannel.close();
        } catch (IOException e) {
            LOGGER.error("ConnectionManager.disconnect(SocketChannel): Failed to close socket connection.", e);
        }
    }

    public void handleInputFromSocketChannel() {

        try {
            this.cmdBuffer.readFromSocketChannel(this.socketChannel);
            this.cmdBuffer.parseCommands();
        } catch (ClosedChannelException e) {
            this.disconnect();
        }

        LOGGER.error(this.cmdBuffer.toString());

        // If a valid command exists, then route it and generate a job.
        if (this.cmdBuffer.hasNextCommand() == false) {
            return;
        }

        // Determine Action appropriate for the current ConnectionState
        //ConnectionStateJob j = new ConnectionStateJob(this);
        //j.selfSubmit();
    }

    public void setContext(Context context){
        contextSwitcher.setContext(context);
    }

    public void sendCRLFs(int numberOfCRLFs) {
        StringBuilder crlfs = new StringBuilder();
        for (int i = 0; i < numberOfCRLFs; ++i) {
            crlfs.append(Constants.CRLF);
        }
        this.sendText(crlfs.toString());
    }

    public void sendCRLF() {
        this.sendText(Constants.CRLF);
    }

    public void sendTextLn(String textToSend) {
        this.sendText(textToSend + Constants.CRLF);
    }

    public void sendText(String text) {
        this.clientListener.send(this.socketChannel, text);
    }

    public void sendPrompt() {
        this.sendCRLF();
        this.sendText("fm:>");
    }

}
