/**************************************************************************************************
 * Copyright 2009 Chris Maguire (cwmaguire@gmail.com)                                             *
 *                                                                                                *
 * Flexmud is free software: you can redistribute it and/or modify                                *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 *                                                                                                *
 * Flexmud is distributed in the hope that it will be useful,                                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 *                                                                                                *
 * You should have received a copy of the GNU General Public License                              *
 * along with flexmud.  If not, see <http://www.gnu.org/licenses/>.                               *
 **************************************************************************************************/
package flexmud.net;

import flexmud.cfg.Constants;
import flexmud.engine.context.ClientContextHandler;
import flexmud.engine.context.Context;
import flexmud.security.Account;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class);
    protected UUID connID;
    protected SocketChannel socketChannel = null;
    protected ClientCommunicator clientCommunicator;
    protected LineBuffer inputBuffer;
    protected ClientContextHandler clientContextHandler;
    protected Account account;
    protected String login;
    protected String password;

    public Client(ClientCommunicator clientCommunicator, SocketChannel socketChannel) {
        this.clientCommunicator = clientCommunicator;
        this.socketChannel = socketChannel;
        clientContextHandler = getNewClientContextHandler();

        init();
    }

    public ClientContextHandler getNewClientContextHandler() {
        return new ClientContextHandler(this);
    }

    protected void init() {
        inputBuffer = new LineBuffer();
        connID = clientCommunicator.getNewConnectionID();

        clientContextHandler.init();
    }

    public UUID getConnectionID() {
        return this.connID;
    }

    public ClientCommunicator getClientListener() {
        return this.clientCommunicator;
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public LineBuffer getInputBuffer() {
        return inputBuffer;
    }

    public void disconnect() {
        this.clientCommunicator.disconnect(this);
        try {
            socketChannel.close();
        } catch (IOException e) {
            LOGGER.error("ConnectionManager.disconnect(SocketChannel): Failed to close socket connection.", e);
        }
    }

    public void handleInputFromSocketChannel() {

        try {
            inputBuffer.readFromSocketChannel(this.socketChannel);
            inputBuffer.storeCompleteLines();
        } catch (ClosedChannelException e) {
            disconnect();
        }

        LOGGER.info("Command buffer: " + this.inputBuffer.toString());

        if (inputBuffer.hasCompleteCommand()) {
            LOGGER.debug("Running completed command");
            clientContextHandler.runCommand(inputBuffer.getNextCommand());
        }else{
            LOGGER.debug("No complete commands to run");
        }

    }

    public Context getContext(){
        return clientContextHandler.getContext();
    }

    public void setContext(Context context){
        clientContextHandler.setContext(context);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ClientContextHandler getClientContextHandler() {
        return clientContextHandler;
    }

    public void setClientContextHandler(ClientContextHandler clientContextHandler) {
        this.clientContextHandler = clientContextHandler;
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
        this.clientCommunicator.send(this.socketChannel, text);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
