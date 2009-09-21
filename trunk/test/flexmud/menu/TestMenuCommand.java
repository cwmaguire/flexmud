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
package flexmud.menu;

import org.junit.Test;
import org.junit.Before;
import flexmud.engine.cmd.MenuCommand;
import flexmud.engine.cmd.ContextOrGenericPromptCommand;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.exec.Executor;
import flexmud.net.FakeClientCommunicator;
import flexmud.net.FakeClient;
import flexmud.util.Util;
import flexmud.cfg.Constants;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

public class TestMenuCommand {
    //ToDo: get some context commands and print them out

    private FakeClient fakeClient;
    private FakeClientCommunicator fakeClientCommunicator;

    @Before
    public void setup(){

        fakeClientCommunicator = new FakeClientCommunicator();
        fakeClientCommunicator.setShouldInterceptWrite(true);
        fakeClient = new FakeClient(fakeClientCommunicator, null);
        //fakeClient.setContext(context);

    }

    @Test
    public void testMenuWithZeroMenuItemCommands(){

        MenuCommand emptyMenuCommand = new MenuCommand();
        emptyMenuCommand.setClient(fakeClient);
        Executor.exec(emptyMenuCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("No text should be sent to client for an empty menu; ", "", fakeClientCommunicator.getLastSentText());
    }

    @Test
    public void testMenuWithSingleMenuItemCommand(){

        MenuCommand singleItemMenuCommand = new MenuCommand();
        ContextCommand contextCommand = createPlainContextCommand();

        singleItemMenuCommand.setClient(fakeClient);
        singleItemMenuCommand.setMenuContextCommands(Arrays.asList(contextCommand));

        Executor.exec(singleItemMenuCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Context command description should be sent to client; ", contextCommand.getDescription() + Constants.CRLF, fakeClientCommunicator.getLastSentText());
    }

    @Test
    public void testMenuWithMultipleMenuItemCommands(){

        MenuCommand multiItemMenuCommand = new MenuCommand();
        ContextCommand contextCommand1 = createPlainContextCommand();
        ContextCommand contextCommand2 = createPlainContextCommand();
        String menuText = contextCommand1.getDescription() + Constants.CRLF + contextCommand2.getDescription() + Constants.CRLF;

        multiItemMenuCommand.setClient(fakeClient);
        multiItemMenuCommand.setMenuContextCommands(Arrays.asList(contextCommand1, contextCommand2));

        Executor.exec(multiItemMenuCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Context command description should be sent to client; ", menuText, fakeClientCommunicator.getLastSentText());
    }

    private ContextCommand createPlainContextCommand(){
        ContextCommand ctxCommand = new ContextCommand();
        ctxCommand.setDescription(UUID.randomUUID().toString());
        return ctxCommand;
    }
}
