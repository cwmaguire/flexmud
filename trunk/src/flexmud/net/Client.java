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
import flexmud.db.HibernateUtil;
import flexmud.engine.context.Context;
import flexmud.sec.Account;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.UUID;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class);
    protected UUID connID;
    protected SocketChannel socketChannel = null;
    private ClientState connState;
    protected ClientListener clientListener;
    protected CommandBuffer cmdBuffer;
    protected Context context;

    private Account account;

    public Client(ClientListener clientListener, SocketChannel socketChannel) {
        this.cmdBuffer = new CommandBuffer();
        this.clientListener = clientListener;
        this.connID = clientListener.getNewConnectionID();
        this.socketChannel = socketChannel;

        this.context = fetchMainContext();

    }

    private Context fetchMainContext(){
        List<Context> contexts;
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Context.class);
        detachedCriteria.add(Restrictions.isNull(Context.PARENT_GROUP_PROPERTY));
        contexts = (List<Context>) HibernateUtil.fetch(detachedCriteria);
        if(contexts != null && contexts.size() > 0){
            return contexts.get(0);
        }else{
            return null;
        }
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
