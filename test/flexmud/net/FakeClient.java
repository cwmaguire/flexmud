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

import flexmud.engine.context.ClientContextHandler;
import flexmud.engine.context.FakeClientContextHandler;

import java.nio.channels.SocketChannel;

public class FakeClient extends Client{
    private boolean isConnected = true;

    public FakeClient(ClientCommunicator clientCommunicator, SocketChannel socketChannel){
        super(clientCommunicator, socketChannel);
    }

    public ClientContextHandler getNewClientContextHandler(){
        return new FakeClientContextHandler(this);
    }

    protected void init(){
    }

    public void disconnect(){
        isConnected = false;
    }

    public boolean isConnected(){
        return isConnected;
    }
}
