/*
Copyright 2008 Chris Maguire (cwmaguire@gmail.com)

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

package flexmud.db;

import flexmud.engine.context.Context;
import flexmud.engine.context.ContextGroup;
import flexmud.log.LoggingUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestPersisteContext {
    private static final String CONTEXT_NAME = "ctxt1";
    private Session session;
    private Transaction transaction;

    @Before
    public void setup(){
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging();
        
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
    }

    @After
    public void teardown(){
        transaction.commit();
        session.close();
    }

    @Test
    public void testCreateContextWithGroup(){
        Context context = new Context(CONTEXT_NAME);
        ContextGroup contextGroup = new ContextGroup();
        context.setChildGroup(contextGroup);
        session.save(context);
        Assert.assertFalse("Context ID was not updated automatically after save", context.getId() == 0);
    }

    @Test
    public void testSelectContext(){
        List<Context> contexts;
        Criteria criteria = session.createCriteria(Context.class);
        contexts = (List<Context>) criteria.list();
        Assert.assertEquals("Database should only contain one context", 1, contexts.size());
    }

    @Test
    public void testSelectContextRetrieveGroup(){
        ContextGroup contextGroup;
        List<Context> contexts;
        Criteria criteria = session.createCriteria(Context.class);
        contexts = (List<Context>) criteria.list();
        contextGroup = contexts.get(0).getChildGroup();
        Assert.assertNotNull("Context child group should not be null", contextGroup);
    }

    @Test
    public void testSelectGroupRetrieveContext(){
        Context context;
        List<ContextGroup> contextGroups;
        Criteria criteria = session.createCriteria(ContextGroup.class);
        contextGroups = (List<ContextGroup>) criteria.list();
        context = contextGroups.get(0).getParentContext();
        Assert.assertNotNull("Context group parent context should not be null", context);
    }

    @Test
    public void testDeleteContextAndGroup(){
        List<Context> contexts;
        List<ContextGroup> contextGroups;

        Criteria criteria = session.createCriteria(Context.class);
        contexts = (List<Context>) criteria.list();

        session.delete(contexts.get(0));

        contexts = (List<Context>) criteria.list();
        Assert.assertEquals("Database should contain no contexts", 0, contexts.size());

        criteria = session.createCriteria(ContextGroup.class);
        contextGroups = criteria.list();
        Assert.assertEquals("Database should contain no context groups", 0, contextGroups.size());
    }
}
