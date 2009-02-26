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

import config.Constants;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Client {
    private UUID connID;
    private final SocketChannel socketChannel = null;
    private ClientState connState;
    private ClientListener clientListener;
    private CommandBuffer cmdBuffer;

    //private PlayerCharacter pc;
    //private Account account;

    public Client() {
        /*
        this.socketChannel = inSc;
        this.connections = connections;
        this.connState = ConnectionState.DISCONNECTED;
        this.connID = connections.getNewConnectionID();
        this.cmdBuffer = new CommandBuffer();
        this.pc = null;
        this.account = null;
        */
    }

    public boolean disconnect() {
        this.clientListener.disconnect(this);
        try {
            socketChannel.close();
        } catch (IOException e) {
            System.err.println("ConnectionManager.disconnect(SocketChannel): Failed to close socket connection.");
            e.printStackTrace();
        }
        return socketChannel.isConnected();
    }

    public void handleInputFromClient() {

        try {
            this.cmdBuffer.readFromSocketChannel(this.socketChannel);
            this.cmdBuffer.parseCommands();
        } catch (ClosedChannelException e) {
            this.disconnect();
        }

        System.err.println(this.cmdBuffer.toString());

        // If a valid command exists, then route it and generate a job.
        if (this.cmdBuffer.hasNextCommand() == false) {
            return;
        }

        // Determine Action appropriate for the current ConnectionState
        //ConnectionStateJob j = new ConnectionStateJob(this);
        //j.selfSubmit();
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
        // Attach the SocketChannel and send the text on its way!
        this.clientListener.send(this.socketChannel, text);
    }

    public void sendPrompt() {
        this.sendCRLF();
        this.sendText("jMUD:");
    }

    public ClientState getConnState() {
        return this.connState;
    }

    public void changeConnState(ClientState newState) {
        ClientState oldState = this.connState;

        if (oldState == newState) {
            // There is no change in state

        } else {

            //oldState.runExitJob(this, newState);
            this.connState = newState;
            //newState.runEnterJob(this, oldState);
        }
    }

    public UUID getConnectionID() {
        return this.connID;
    }

    public ClientListener getConnectionManager() {
        return this.clientListener;
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public CommandBuffer getCmdBuffer() {
        return cmdBuffer;
    }

}
