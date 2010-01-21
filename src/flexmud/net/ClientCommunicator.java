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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class ClientCommunicator implements Runnable {
    private static Logger LOGGER = Logger.getLogger(ClientCommunicator.class);
    private static final Object CLIENT_READ_LOCK = new Object();

    private boolean isRunning = true;
    private boolean shouldRunCommands = true;
    private int maxAllowedConns = 1024;
    private Thread clientListenerThread;
    private Selector selector;
    private final List<SocketChannel> socketChannelsReadyToWrite = new LinkedList<SocketChannel>();
    private final Map<SocketChannel, List<ByteBuffer>> socketChannelByteBuffers = new HashMap<SocketChannel, List<ByteBuffer>>();
    protected final Map<SocketChannel, Client> socketChannelClients = new HashMap<SocketChannel, Client>();

    public ClientCommunicator() {
    }

    public ClientCommunicator(int port) throws IOException {
        this(InetAddress.getLocalHost(), port);
    }

    public ClientCommunicator(InetAddress hostAddress, int port) throws IOException {
        this.selector = SelectorProvider.provider().openSelector();

        ServerSocketChannel serverChannel = createNonBlockingServerChannel();

        bindServerChannelToAddress(hostAddress, port, serverChannel);

        LOGGER.info("ClientCommunicator is configured to listen at: " + hostAddress.getCanonicalHostName() + " on port: " + port);

        registerToAcceptConnections(serverChannel);
    }

    private void registerToAcceptConnections(ServerSocketChannel serverChannel) throws ClosedChannelException {
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    private void bindServerChannelToAddress(InetAddress hostAddress, int port, ServerSocketChannel serverChannel) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostAddress, port);
        serverChannel.socket().bind(inetSocketAddress);
    }

    private ServerSocketChannel createNonBlockingServerChannel() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        return serverChannel;
    }

    private void acceptNewConnection(final SelectionKey key) throws IOException {

        ServerSocketChannel serverSocketChannel = getNewConnectionServerSocketChannel(key);

        SocketChannel socketChannel = serverSocketChannel.accept();

        this.createClientMappedToSocketChannel(socketChannel);

        LOGGER.info("Total Connections now: " + this.socketChannelClients.size());
    }

    private ServerSocketChannel getNewConnectionServerSocketChannel(SelectionKey key) {
        return (ServerSocketChannel) key.channel();
    }

    protected Client createClientMappedToSocketChannel(SocketChannel socketChannel) throws IOException {
        configureSocketChannelForNonBlockingRead(socketChannel);

        Client client = new Client(this, socketChannel);

        LOGGER.info("New Connection. ID: " + client.getConnectionID());

        this.socketChannelClients.put(socketChannel, client);

        return client;
    }

    protected void configureSocketChannelForNonBlockingRead(SocketChannel sc) throws IOException {
        sc.configureBlocking(false);
        sc.register(this.selector, SelectionKey.OP_READ);
    }

    private void disconnect(final SelectionKey key) {
        LOGGER.info("Client.disconnect(SelectionKey): key=" + key.toString());
        this.disconnect((SocketChannel) key.channel());

        key.cancel();
    }

    private void disconnect(final SocketChannel sockChan) {
        LOGGER.info("Client.disconnect(SocketChannel): sockChan=" + sockChan.toString());
        disconnect(this.socketChannelClients.get(sockChan));
    }

    public void disconnect(final Client client) {
        LOGGER.info("Client.disconnect(Connection): connection=" + client.toString());
        this.socketChannelClients.remove(client.getSocketChannel());

        // TODO What to do with the orphaned Connection? Probably perform Player object look up and persist the data....
        // CM: I concur; we might put in some penalties too, so this should be
        // abstracted out and made extendible with rules
        // DHL: I am thinking a PersistAndRemoveConnectionJob would be good here.

        try {
            client.getSocketChannel().close();
        } catch (IOException e) {
            LOGGER.error("Client.disconnect(Connection): Failed to close socket connection.", e);
        }

        LOGGER.info("Client.disconnect(Connection): Total Clients: " + this.socketChannelClients.size());
    }

    public UUID getNewConnectionID() {
        if (!this.canAcceptConnection()) {
            return null;
        } else {
            return UUID.randomUUID();
        }
    }

    public boolean canAcceptConnection() {
        return (this.socketChannelClients.size() < this.maxAllowedConns);
    }

    protected void read(final SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = this.socketChannelClients.get(socketChannel);

        if (client == null) {
            try {
                client = this.createClientMappedToSocketChannel(socketChannel);
                if (client == null) {
                    throw new IOException();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create new Client in Clients.read()", e);
                return;
            }
        }

        synchronized (CLIENT_READ_LOCK) {
            client.handleInputFromSocketChannel();
        }
    }

    public void send(final SocketChannel sockChan, String text) {
        this.send(sockChan, text.getBytes());
    }

    public final void send(SocketChannel socketChannel, byte[] data) {
        synchronized (this.socketChannelsReadyToWrite) {
            socketChannelsReadyToWrite.add(socketChannel);
        }
        bufferData(socketChannel, data);
        this.selector.wakeup();
    }

    private void bufferData(SocketChannel socketChannel, byte[] data) {
        synchronized (this.socketChannelByteBuffers) {
            List<ByteBuffer> buffers = createOrGetSocketChannelBuffers(socketChannel);
            buffers.add(ByteBuffer.wrap(data));
        }
    }

    // ByteBuffers keep track of how much data has been read; this is convenient when the
    // entire buffer can't be written out to the client
    private List<ByteBuffer> createOrGetSocketChannelBuffers(SocketChannel socketChannel) {
        List<ByteBuffer> buffers = this.socketChannelByteBuffers.get(socketChannel);

        if (buffers == null) {
            buffers = new ArrayList<ByteBuffer>();
            this.socketChannelByteBuffers.put(socketChannel, buffers);
        }
        return buffers;
    }

    private void write(final SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.socketChannelByteBuffers) {
            List<ByteBuffer> buffers = this.socketChannelByteBuffers.get(socketChannel);

            writeBufferedData(socketChannel, buffers);

            if (buffers.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void writeBufferedData(SocketChannel socketChannel, List<ByteBuffer> buffers) throws IOException {
        while (!buffers.isEmpty()) {
            ByteBuffer buf = buffers.get(0);
            socketChannel.write(buf);
            if (isSocketChannelFull(buf)) {
                break;
            }
            buffers.remove(0);
        }
    }

    private boolean isSocketChannelFull(ByteBuffer buf) {
        return buf.remaining() > 0;
    }

    public final void run() {
        this.isRunning = true;
        LOGGER.info("Running.");

        while (this.shouldRunCommands) {
            this.registerInterestOpsWithWritableSocketChannels();
            this.blockForSelectOnRegisteredChannels();
            this.handleNewSelectorKeyEvents();
        }

        LOGGER.info("Shutting down...");

        this.shutdown();

        LOGGER.info("Shutdown.");
    }

    private void shutdown() {
        this.isRunning = false;
        try {
            for (SocketChannel socketChannel : this.socketChannelClients.keySet()) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    LOGGER.error("ClientCommunicator.shutdown() -> SocketChannel.close() failed.", e);
                }
            }
            this.selector.close();
            this.socketChannelClients.clear();
            this.socketChannelByteBuffers.clear();
        } catch (IOException e) {
            LOGGER.info("ClientCommunicator.shutdown() failed: ", e);
        }
    }

    /**
     * This method sets up the thread to run, loads in the ConnectionManager,
     * sets the ThreadRun Command to true and starts the thread.
     */
    public final void start() {
        LOGGER.info("ClientCommunicator: Received Startup Command.");
        shouldRunCommands = true;
        isRunning = true;
        clientListenerThread = new Thread(this, "ClientCommunicator-Thread");
        clientListenerThread.start();
    }

    /**
     * Sets the thread's run command to false and wakes the selector.
     */
    public final void stop() {
        LOGGER.info("Received Shutdown Command.");
        shouldRunCommands = false;
        selector.wakeup();
    }

    private void registerInterestOpsWithWritableSocketChannels() {
        synchronized (socketChannelsReadyToWrite) {
            SelectionKey selectorKey;

            LOGGER.debug("processing pending events");

            for (SocketChannel socketChannel : socketChannelsReadyToWrite) {
                selectorKey = socketChannel.keyFor(selector);

                if (selectorKey == null) {
                    LOGGER.info("Null selector key for socket channel " + socketChannel.toString() + " and selector " + selector.toString());
                    if (!socketChannel.isConnected()) {
                        this.disconnect(socketChannel);
                    }
                    continue;
                }

                if (!selectorKey.isValid()) {
                    continue;
                }

                selectorKey.interestOps(SelectionKey.OP_WRITE);
            }
            this.socketChannelsReadyToWrite.clear();
        }
    }

    private void blockForSelectOnRegisteredChannels() {
        isRunning = false;
        try {
            selector.select();
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
        }
        this.isRunning = true;
    }

    private void handleNewSelectorKeyEvents() {
        SelectionKey key;

        LOGGER.debug("Handling new events");

        Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
            key = selectedKeys.next();
            selectedKeys.remove();

            try {
                if (!key.isValid()) {
                    // disconnect(key);
                } else if (key.isAcceptable()) {
                    acceptNewConnection(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);

                } else {
                    LOGGER.error("Unhandled keystate: " + key.toString());
                }
            } catch (CancelledKeyException cke) {
                LOGGER.error(cke);
                this.disconnect(key);
            } catch (IOException e) {
                LOGGER.error("Exception: " + key.toString(), e);
            }
        }
    }

    public final boolean shouldRunCommands() {
        return shouldRunCommands;
    }

    public final boolean isRunning() {
        return isRunning;
    }

    public final Thread getThread() {
        return clientListenerThread;
    }
}
