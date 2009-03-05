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

package flexmud.db;

import flexmud.engine.cmd.AliasCommandClassNameMap;
import flexmud.engine.context.Context;
import flexmud.engine.context.ContextGroup;
import flexmud.log.LoggingUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestPersistAliasCommandClassNameMap {
    private static final String COMMAND_CLASS_NAME = "CommandClass";

    @Before
    public void setup(){
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging();

    }

    @After
    public void teardown(){
    }

    @Test
    public void testSave(){
        Context context = new Context("a");
        ContextGroup contextGroup = new ContextGroup();
        context.setChildGroup(contextGroup);
        HibernateUtil.save(context);

        AliasCommandClassNameMap aliasCommandMap = new AliasCommandClassNameMap();
        aliasCommandMap.setCommandClassName(COMMAND_CLASS_NAME);
        aliasCommandMap.setAliases(new HashSet<String>(Arrays.asList("a", "b", "c")));
        aliasCommandMap.setContext(context);
        HibernateUtil.save(aliasCommandMap);
        Assert.assertNotSame("Alias command ID was not updated automatically after save", 0, aliasCommandMap.getId());
    }

    @Test
    public void testFetch(){
        // ToDo CM: fix the join so that we don't get three context_commands because of the
        // ToDo CM: three aliases (i.e. do a distinct)
        List<AliasCommandClassNameMap> aliasCommandMaps;
        DetachedCriteria criteria = DetachedCriteria.forClass(AliasCommandClassNameMap.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        aliasCommandMaps = (List<AliasCommandClassNameMap>) HibernateUtil.fetch(criteria);

        Assert.assertNotNull("List of AliasCommandMaps should not be null", aliasCommandMaps);
        Assert.assertEquals("Database should only contain one AliasCommandMap", 1, aliasCommandMaps.size());
    }

    @Test
    public void testDelete(){
        List<AliasCommandClassNameMap> aliasCommandMaps;

        DetachedCriteria criteria = DetachedCriteria.forClass(AliasCommandClassNameMap.class);
        aliasCommandMaps = (List<AliasCommandClassNameMap>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of alias command maps should not be null", aliasCommandMaps);

        HibernateUtil.delete(aliasCommandMaps.get(0));

        aliasCommandMaps = (List<AliasCommandClassNameMap>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of alias command maps should not be null", aliasCommandMaps);
        Assert.assertEquals("Database should contain no alias command maps", 0, aliasCommandMaps.size());
    }
}