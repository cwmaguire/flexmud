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
import org.hibernate.criterion.DetachedCriteria;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestPersistContext {
    private static final String CONTEXT_NAME = "ctxt1";

    @Before
    public void setup(){
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging();
    }

    @After
    public void teardown(){
    }

    @Test
    public void testCreateContextWithGroup(){
        Context context = new Context(CONTEXT_NAME);
        ContextGroup contextGroup = new ContextGroup();
        context.setChildGroup(contextGroup);
        HibernateUtil.save(context);
        Assert.assertFalse("Context ID was not updated automatically after save", context.getId() == 0);
    }

    @Test
    public void testSelectContext(){
        List<Context> contexts;
        DetachedCriteria criteria = DetachedCriteria.forClass(Context.class);
        contexts = (List<Context>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Contexts should not be null", contexts);
        Assert.assertEquals("Database should only contain one context:", 1, contexts.size());
    }

    @Test
    public void testSelectContextRetrieveGroup(){
        ContextGroup contextGroup;
        List<Context> contexts;
        DetachedCriteria criteria = DetachedCriteria.forClass(Context.class);
        contexts = (List<Context>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Contexts should not be null", contexts);
        contextGroup = contexts.get(0).getChildGroup();
        Assert.assertNotNull("Context child group should not be null", contextGroup);
    }

    @Test
    public void testSelectGroupRetrieveContext(){
        Context context;
        List<ContextGroup> contextGroups;
        DetachedCriteria criteria = DetachedCriteria.forClass(ContextGroup.class);
        contextGroups = (List<ContextGroup>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Contexts should not be null", contextGroups);
        context = contextGroups.get(0).getContext();
        Assert.assertNotNull("Context group parent context should not be null", context);
    }

    @Test
    public void testDeleteContextAndGroup(){
        List<Context> contexts;
        List<ContextGroup> contextGroups;

        DetachedCriteria criteria = DetachedCriteria.forClass(Context.class);
        contexts = (List<Context>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Contexts should not be null", contexts);

        HibernateUtil.delete(contexts.get(0));

        contexts = (List<Context>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Contexts should not be null", contexts);
        Assert.assertEquals("Database should contain no contexts", 0, contexts.size());

        criteria = DetachedCriteria.forClass(ContextGroup.class);
        contextGroups = (List<ContextGroup>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of ContextGroups should not be null", contextGroups);
        Assert.assertEquals("Database should contain no context groups", 0, contextGroups.size());
    }
}
