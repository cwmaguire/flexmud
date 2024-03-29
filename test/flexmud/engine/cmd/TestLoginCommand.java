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

import flexmud.cfg.Preferences;
import flexmud.engine.context.Context;
import flexmud.engine.exec.Executor;
import flexmud.engine.cmd.login.LoginCommand;
import flexmud.log.LoggingUtil;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.util.ContextUtil;
import flexmud.util.Util;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestLoginCommand {

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Test
    public void testLoginContextSwitchesToChildContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);

        Context fakeContext = ContextUtil.createContextHierarchy();

        client.setContext(fakeContext);

        LoginCommand loginCmd = new LoginCommand();
        loginCmd.setClient(client);
        loginCmd.setCommandArguments(Arrays.asList(""));

        Executor.exec(loginCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Login command did not switch to child context", fakeContext.getChildGroup().getChildContexts().iterator().next(), client.getContext());
    }
}
