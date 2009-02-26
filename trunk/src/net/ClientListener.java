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

import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientListener implements Runnable{
    private static Logger LOGGER = Logger.getLogger(ClientListener.class);

    private boolean isRunning = true;
    private boolean shouldRunCommands = true;
    private int maxAllowedConns = 1024;
    private Thread clientListenerThread;
    private Selector selector;
    private final List<ClientEvent> pendingEvents = new LinkedList<ClientEvent>();
    private final Map<SocketChannel, List<ByteBuffer>> socketChannelByteBuffers = new HashMap<SocketChannel, List<ByteBuffer>>();
    protected final Map<SocketChannel, Client> socketChannelConnections = new HashMap<SocketChannel, Client>();

    public ClientListener(int port) throws IOException {
        this(InetAddress.getLocalHost(), port);
    }

    public ClientListener(InetAddress hostAddress, int port) throws IOException {
        this.selector = SelectorProvider.provider().openSelector();

        ServerSocketChannel serverChannel = createNonBlockingServerChannel();

        bindServerChannelToAddress(hostAddress, port, serverChannel);

        LOGGER.info("ConnectionManager is configured to listen at: " + hostAddress.getCanonicalHostName() + " on port: " + port);

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

        this.mapSocketChannelToNewClient(socketChannel);

        LOGGER.info("ConnectionManager: Total Connections now: " + this.socketChannelConnections.size());
    }

    private ServerSocketChannel getNewConnectionServerSocketChannel(SelectionKey key) {
        return (ServerSocketChannel) key.channel();
    }

    private Client mapSocketChannelToNewClient(SocketChannel socketChannel) throws IOException {
        configureSocketChannelForNonBlockingRead(socketChannel);

        Client client = new Client();

        LOGGER.info("ConnectionManager: New Connection. ID: " + client.getConnectionID());

        this.socketChannelConnections.put(socketChannel, client);

        return client;
    }

    private void configureSocketChannelForNonBlockingRead(SocketChannel sc) throws IOException {
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
        disconnect(this.socketChannelConnections.get(sockChan));
    }

    public void disconnect(final Client client) {
        LOGGER.info("Client.disconnect(Connection): connection=" + client.toString());
        this.socketChannelConnections.remove(client.getSocketChannel());

        // TODO What to do with the orphaned Connection? Probably perform Player object look up and persist the data....
        // CM: I concur; we might put in some penalties too, so this should be
        // abstracted out and made extendible with rules
        // DHL: I am thinking a PersistAndRemoveConnectionJob would be good here.

        try {
            client.getSocketChannel().close();
        } catch (IOException e) {
            LOGGER.error("Client.disconnect(Connection): Failed to close socket connection.", e);
        }

        LOGGER.info("Client.disconnect(Connection): Total Clients: " + this.socketChannelConnections.size());
    }

    public UUID getNewConnectionID() {
        if (!this.canAcceptConnection()) {
            return null;
        } else {
            return UUID.randomUUID();
        }
    }

    public boolean canAcceptConnection() {
        return (this.socketChannelConnections.size() < this.maxAllowedConns);
    }

    protected void read(final SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = this.socketChannelConnections.get(socketChannel);

        if (client == null) {
            try {
                client = this.mapSocketChannelToNewClient(socketChannel);
                if(client == null){
                    throw new IOException();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create new Client in Clients.read()", e);
                return;
            }
        }

        synchronized (key) {
            client.handleInputFromClient();
        }
    }

    public final void send(final SocketChannel sockChan, String text) {
        this.send(sockChan, text.getBytes());
    }

    public final void send(SocketChannel socketChannel, byte[] data) {
        synchronized (this.pendingEvents) {

            registerSocketChannelForReading(socketChannel);

            bufferData(socketChannel, data);
        }
        this.selector.wakeup();
    }

    private void bufferData(SocketChannel socketChannel, byte[] data) {
        synchronized (this.socketChannelByteBuffers) {
            List<ByteBuffer> buffers = createOrGetSocketChannelBuffers(socketChannel);
            buffers.add(ByteBuffer.wrap(data));
        }
    }

    private List<ByteBuffer> createOrGetSocketChannelBuffers(SocketChannel socketChannel) {
        List<ByteBuffer> buffers = this.socketChannelByteBuffers.get(socketChannel);

        if (buffers == null) {
            buffers = new ArrayList<ByteBuffer>();
            this.socketChannelByteBuffers.put(socketChannel, buffers);
        }
        return buffers;
    }

    private void registerSocketChannelForReading(SocketChannel socketChannel) {
        this.pendingEvents.add(new ClientEvent(socketChannel, ClientEvent.EventType.CHANGEOPS, SelectionKey.OP_WRITE));
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
        LOGGER.info("ClientListener: Running.");

        while (this.shouldRunCommands) {
            this.processPendingEvents();
            this.blockForSelectOnRegisteredChannels();
            this.handleNewEvents();
        }

        LOGGER.info("ClientListener: Shutting down...");

        this.shutdown();

        LOGGER.info("ClientListener: Shutdown.");
    }

    private void shutdown() {
        this.isRunning = false;
        try {
            for (SocketChannel socketChannel : this.socketChannelConnections.keySet()) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    LOGGER.error("ClientListener.shutdown() -> SocketChannel.close() failed.", e);
                }
            }
            this.selector.close();
            this.socketChannelConnections.clear();
            this.socketChannelByteBuffers.clear();
        } catch (IOException e) {
            LOGGER.info("ClientListener.shutdown() failed: ", e);
        }
    }

    /**
     * This method sets up the thread to run, loads in the ConnectionManager,
     * sets the ThreadRun Command to true and starts the thread.
     */
    public final void start() {
        LOGGER.info("ClientListener: Received Startup Command.");
        this.shouldRunCommands = true;
        this.isRunning = true;
        this.clientListenerThread = new Thread(this, "ClientListener-Thread");
        this.clientListenerThread.start();
    }

    /**
     * Sets the thread's run command to false and wakes the selector.
     */
    public final void stop() {
        LOGGER.info("ClientListener: Received Shutdown Command.");
        this.shouldRunCommands = false;
        this.selector.wakeup();
    }

    private void processPendingEvents() {
        synchronized (this.pendingEvents) {
            SelectionKey selKey;

            for (ClientEvent connEvent : this.pendingEvents) {
                switch (connEvent.eventType) {
                    case CHANGEOPS:
                        selKey = connEvent.socket.keyFor(this.selector);

                        if (selKey == null) {
                            System.out.println(connEvent.toString());
                            if (!connEvent.socket.isConnected()) {
                                this.disconnect(connEvent.socket);
                            }
                            continue;
                        }

                        if (!selKey.isValid()) {
                            continue;
                        }

                        selKey.interestOps(connEvent.operations);
                        break;

                    // case REGISTER:
                    // System.out.println("Register");
                    // try {
                    // connEvent.socket.register(this.selector, connEvent.operations);
                    // } catch (ClosedChannelException e) {
                    // System.err.println("Exception: " + e.getMessage());
                    // }
                    // break;
                }
            }
            this.pendingEvents.clear();
        }
    }

    private void blockForSelectOnRegisteredChannels() {
        this.isRunning = false;
        try {
            this.selector.select();
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
        }
        this.isRunning = true;
    }

    private void handleNewEvents() {
        SelectionKey key;

        Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
            key = selectedKeys.next();
            selectedKeys.remove();

            try {
                if (!key.isValid()) {
                    // disconnect(key);
                    continue;
                } else if (key.isAcceptable()) {
                    acceptNewConnection(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);

                } else {
                    System.err.println("Unhandled keystate: " + key.toString());
                }
            } catch (CancelledKeyException cke) {
                System.err.print(cke.getMessage());
                this.disconnect(key);
            } catch (IOException e) {
                System.err.println("Exception: " + key.toString());
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
