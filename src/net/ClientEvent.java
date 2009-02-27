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

import java.nio.channels.SocketChannel;

public class ClientEvent {

    public SocketChannel socket;
    public EventType eventType;
    public int operations;

    public ClientEvent(SocketChannel socketChannel, EventType eventType, int operations) {
        this.socket = socketChannel;
        this.eventType = eventType;
        this.operations = operations;
    }

    public static enum EventType {
        REGISTER,
        CHANGEOPS
    }
}