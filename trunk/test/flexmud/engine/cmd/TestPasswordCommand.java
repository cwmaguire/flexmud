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

package flexmud.engine.cmd;

import org.junit.Test;

import java.util.UUID;
import java.util.HashSet;
import java.util.Arrays;

import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.engine.context.*;
import flexmud.engine.exec.Executor;
import flexmud.util.Util;
import flexmud.util.ContextUtil;
import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import junit.framework.Assert;

public class TestPasswordCommand {

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Test
    public void testPasswordContextSwitchesToLoginValidationContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);

        FakeClientContextHandler clientContextHandler = new FakeClientContextHandler(client);

        Context passwordContext = createContextHierarchy();

        client.setContext(passwordContext);

        PasswordCommand passwordCmd = new PasswordCommand();
        passwordCmd.setClient(client);
        passwordCmd.setCommandArguments(Arrays.asList(""));

        clientContextHandler.setContext(passwordContext);
        Executor.exec(passwordCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Password command did not switch to child context", passwordContext.getChildGroup().getChildContexts().iterator().next(), client.getContext());
    }

    private Context createContextHierarchy() {
        Context passwordContext = new Context();
        ContextGroup cntxtGroup = new ContextGroup();
        cntxtGroup.setChildContexts(new HashSet<Context>(Arrays.asList(new Context())));
        passwordContext.setChildGroup(cntxtGroup);
        return passwordContext;
    }
}