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

import flexmud.cfg.Constants;
import flexmud.cfg.Preferences;
import flexmud.db.HibernateUtil;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.ContextCommandFlag;
import flexmud.engine.context.ContextGroup;
import flexmud.engine.exec.Executor;
import flexmud.engine.cmd.login.ValidateLoginCommand;
import flexmud.log.LoggingUtil;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.security.Account;
import flexmud.util.Util;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class TestLoginValidationCommand {
    private final String LOGIN = UUID.randomUUID().toString();
    private final String PASSWORD = UUID.randomUUID().toString();

    private Context loginCntxt;
    private Account account;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    /*
       null login goes back to login
       null password goes back to login
       no matching account prints message, goes back to login
       matching account sets account, goes to main menu

     */

    @Before
    public void setup(){
        loginCntxt = new Context();
        loginCntxt.setName("login");
        HibernateUtil.save(loginCntxt);

        account = new Account();
        account.setLogin(LOGIN);
        account.setPassword(PASSWORD);
        HibernateUtil.save(account);

    }

    @After
    public void tearDown(){
        HibernateUtil.delete(loginCntxt);
        HibernateUtil.delete(account);
    }

    @Test
    public void testNullLoginSwitchesToLoginContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);
        client.setLogin(null);
        client.setPassword(UUID.randomUUID().toString());

        ValidateLoginCommand validateLoginCmd = new ValidateLoginCommand();
        validateLoginCmd.setClient(client);
        Executor.exec(validateLoginCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("ValidateLoginCommand did not switch to login context when no login is null ", loginCntxt.getId(), client.getContext().getId());

    }

    @Test
    public void testNullPasswordSwitchesToLoginContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);
        client.setLogin(UUID.randomUUID().toString());
        client.setPassword(null);

        ValidateLoginCommand validateLoginCmd = new ValidateLoginCommand();
        validateLoginCmd.setClient(client);
        Executor.exec(validateLoginCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("ValidateLoginCommand did not switch to login context when no password is null ", loginCntxt.getId(), client.getContext().getId());

    }

    @Test
    public void testNoMatchingAccountSwitchesToLoginContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);
        client.setLogin(UUID.randomUUID().toString());
        client.setPassword(UUID.randomUUID().toString());

        ValidateLoginCommand validateLoginCmd = new ValidateLoginCommand();
        validateLoginCmd.setClient(client);
        Executor.exec(validateLoginCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("ValidateLoginCommand did not switch to login context when no account matches ", loginCntxt.getId(), client.getContext().getId());

    }

    @Test
    public void testNoMatchingAccountsSendsMessageToClient(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);
        client.setLogin(UUID.randomUUID().toString());
        client.setPassword(UUID.randomUUID().toString());

        //FakeClientContextHandler clientContextHandler = new FakeClientContextHandler(client);

        ValidateLoginCommand validateLoginCmd = new ValidateLoginCommand();
        validateLoginCmd.setClient(client);
        Executor.exec(validateLoginCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertTrue("Client did not receive \"login failed\" message", clientCommunicator.getSentText().contains(Preferences.getPreference(Preferences.LOGIN_FAILED_MESSAGE) + Constants.CRLF));
    }

    @Test
    public void testMatchingCredentialsSwitchesToMainMenuContext(){
        FakeClientCommunicator clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        FakeClient client = new FakeClient(clientCommunicator, null);
        client.setLogin(LOGIN);
        client.setPassword(PASSWORD);

        //FakeClientContextHandler clientContextHandler = new FakeClientContextHandler(client);

        Context loginValidationCntxt = createContextHierarchy();

        client.setContext(loginValidationCntxt);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("ValidateLoginCommand did not switch to child context", loginValidationCntxt.getChildGroup().getChildContexts().iterator().next(), client.getContext());
    }

    private Context createContextHierarchy() {
        Context context = new Context();
        ContextCommand contextCmd = new ContextCommand();
        ContextGroup cntxtGroup = new ContextGroup();

        cntxtGroup.setChildContexts(new HashSet<Context>(Arrays.asList(new Context())));
        context.setChildGroup(cntxtGroup);

        contextCmd.setContext(context);
        contextCmd.setContextCommandFlag(ContextCommandFlag.ENTRY);
        contextCmd.setCommandClassName("flexmud.engine.cmd.login.ValidateLoginCommand");

        context.setContextCommands(new HashSet<ContextCommand>(Arrays.asList(contextCmd)));
        context.init();

        return context;
    }
}