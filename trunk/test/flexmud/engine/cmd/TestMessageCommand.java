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
import org.junit.Before;
import org.junit.After;
import flexmud.engine.exec.Executor;
import flexmud.engine.context.Message;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.util.Util;
import flexmud.cfg.Preferences;
import flexmud.cfg.Constants;
import flexmud.log.LoggingUtil;
import flexmud.db.HibernateUtil;
import junit.framework.Assert;

import java.util.UUID;
import java.util.Arrays;

public class TestMessageCommand {

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    private Message message;

    @Before
    public void setup(){
        message = new Message();
        message.setName("test message");
        message.setMessage(UUID.randomUUID().toString());
        HibernateUtil.save(message);
    }

    @After
    public void tearDown(){
        HibernateUtil.delete(message);
    }

    @Test
    public void testMessageCommand(){
        FakeClientCommunicator fakeClientCommunicator = new FakeClientCommunicator();
        fakeClientCommunicator.setShouldInterceptWrite(true);

        FakeClient fakeClient = new FakeClient(fakeClientCommunicator, null);

        MessageCommand messageCommand = new MessageCommand();
        messageCommand.setClient(fakeClient);
        messageCommand.setCommandArguments(Arrays.asList(String.valueOf(message.getId())));
        Executor.exec(messageCommand);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertTrue("Message ID was not set, ergo was not saved", message.getId() != 0);
        Assert.assertEquals("Message did not output correctly",  message.getMessage() + Constants.CRLF, fakeClientCommunicator.getLastSentText());
    }
}
