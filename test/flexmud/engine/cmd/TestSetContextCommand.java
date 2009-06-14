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
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.ContextCommandFlag;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextGroup;
import flexmud.net.FakeClient;
import flexmud.net.FakeClientCommunicator;
import flexmud.util.Util;
import flexmud.cfg.Preferences;
import flexmud.cfg.Constants;
import flexmud.log.LoggingUtil;
import flexmud.db.HibernateUtil;
import junit.framework.Assert;

import java.util.*;

public class TestSetContextCommand {
    private FakeClientCommunicator clientCommunicator = null;
    private List<Object> objectsToDelete;
    private Context context2;
    private Context context1;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));
    }

    @Before
    public void setup() {
        objectsToDelete = new ArrayList<Object>();
        clientCommunicator = new FakeClientCommunicator();
        clientCommunicator.setShouldInterceptWrite(true);

        context1 = new Context("ctxt1");
        ContextGroup contextGroup = new ContextGroup();
        context1.setChildGroup(contextGroup);
        HibernateUtil.save(context1);
        objectsToDelete.add(context1);

        context2 = new Context("ctxt2");
        context2.setParentGroup(contextGroup);
        HibernateUtil.save(context2);
        objectsToDelete.add(context2);
    }

    @After
    public void tearDown() {
        Collections.reverse(objectsToDelete);
        for (Object obj : objectsToDelete) {
            HibernateUtil.delete(obj);
        }
    }

    @Test
    public void testSetContextCommand(){
        FakeClient fakeClient = new FakeClient(clientCommunicator, null);
        fakeClient.setContext(context1);

        SetContextCommand setCntxtCmd = new SetContextCommand();
        setCntxtCmd.setClient(fakeClient);
        setCntxtCmd.setCommandArguments(Arrays.asList(String.valueOf(context2.getId())));

        Executor.exec(setCntxtCmd);

        Util.pause(Util.ENGINE_WAIT_TIME);

        Assert.assertEquals("Client did not get set to the target context", context2.getId(), fakeClient.getContext().getId());
    }
}