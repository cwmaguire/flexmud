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
import flexmud.net.FakeClientCommunicator;
import flexmud.net.FakeClient;
import flexmud.engine.exec.Executor;
import flexmud.engine.context.Context;
import flexmud.util.Util;
import flexmud.cfg.Preferences;
import flexmud.cfg.Constants;
import flexmud.log.LoggingUtil;
import junit.framework.Assert;

import java.util.UUID;

public class TestPromptCommand {

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Test
    public void testPromptFromContext() {
        String testString = UUID.randomUUID().toString();

        Context context = new Context("Test Context");
        context.setPrompt(testString);

        FakeClientCommunicator fakeClientCommunicator = new FakeClientCommunicator();
        fakeClientCommunicator.setShouldInterceptWrite(true);

        FakeClient fakeClient = new FakeClient(fakeClientCommunicator, null);
        fakeClient.setContext(context);

        PromptCommand promptCommand = new PromptCommand();
        promptCommand.setClient(fakeClient);

        Executor.exec(promptCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Context prompt was not sent to client", testString, fakeClientCommunicator.getLastSentText());
    }

    @Test
    public void testGenericPrompt() {

        Context context = new Context("Test Context");

        FakeClientCommunicator fakeClientCommunicator = new FakeClientCommunicator();
        fakeClientCommunicator.setShouldInterceptWrite(true);

        FakeClient fakeClient = new FakeClient(fakeClientCommunicator, null);
        fakeClient.setContext(context);

        PromptCommand promptCommand = new PromptCommand();
        promptCommand.setClient(fakeClient);
        Executor.exec(promptCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Generic prompt was not sent to client",
                Preferences.getPreference(Preferences.GENERIC_PROMPT),
                fakeClientCommunicator.getLastSentText());
    }
}