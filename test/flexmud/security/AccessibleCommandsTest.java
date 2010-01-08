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

package flexmud.security;

import flexmud.cfg.Preferences;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.FakeContextCommand;
import flexmud.log.LoggingUtil;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AccessibleCommandsTest {
    private FakeClientCommunicator clientCommunicator = null;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Test
    public void testCommandAccessibility() {

        Account fakeAccount = new Account();
        fakeAccount.setAccountRole(new AccountRole());

        FakeClient fakeClient = new FakeClient(clientCommunicator, null);
        fakeClient.setAccount(fakeAccount);

        FakeContextCommand accessibleCommand = new FakeContextCommand();
        accessibleCommand.setIdForTesting(1);

        FakeContextCommand inaccessibleCommand = new FakeContextCommand();
        inaccessibleCommand.setIdForTesting(2);

        fakeAccount.getAccountRole().getCommands().add(accessibleCommand);

        List<ContextCommand> accessibleCommands = fakeClient.getClientContextHandler().getAccessibleContextCommands(Arrays.asList(accessibleCommand, inaccessibleCommand));

        Assert.assertTrue(accessibleCommands.contains(accessibleCommand));
        Assert.assertFalse(accessibleCommands.contains(inaccessibleCommand));

    }
}