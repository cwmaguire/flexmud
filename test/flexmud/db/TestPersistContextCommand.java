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

import flexmud.engine.context.*;
import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestPersistContextCommand {
    private static final String COMMAND_CLASS_NAME = "CommandClass";
    private Context context;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Test
    public void testSave(){
        this.context = new Context("Command Alias Context");
        Context context = this.context;
        ContextGroup contextGroup = new ContextGroup();
        context.setChildGroup(contextGroup);
        HibernateUtil.save(context);

        ContextCommand contextCommand = new ContextCommand();
        contextCommand.setContext(context);
        contextCommand.setCommandClassName(COMMAND_CLASS_NAME);
        contextCommand.setDescription("Command Alias Command");

        ContextCommandAlias alias1 = new ContextCommandAlias();
        alias1.setContextCommand(contextCommand);
        alias1.setAccelerator(true);
        alias1.setBullet(false);
        alias1.setAlias("T");

        ContextCommandAlias alias2 = new ContextCommandAlias();
        alias2.setContextCommand(contextCommand);
        alias2.setAccelerator(false);
        alias2.setBullet(true);
        alias2.setAlias("1");

        ContextCommandParameter param1 = new ContextCommandParameter();
        param1.setContextCommand(contextCommand);
        param1.setClazz("");
        param1.setValue("");

        contextCommand.setAliases(new HashSet<ContextCommandAlias>(Arrays.asList(alias1, alias2)));
        contextCommand.setParameters(new HashSet<ContextCommandParameter>(Arrays.asList(param1)));

        HibernateUtil.save(contextCommand);
        Assert.assertNotSame("Command ID was not updated automatically after save", 0, contextCommand.getId());
        Assert.assertNotSame("Alias ID was not updated automatically after save", 0, alias1.getId());
        Assert.assertNotSame("Parameter ID was not updated automatically after save", 0, param1.getId());
    }

    @Test
    public void testFetch(){
        List<ContextCommand> contextCommands;
        Set<ContextCommandAlias> aliases;
        Set<ContextCommandParameter> parameters;
        DetachedCriteria criteria = DetachedCriteria.forClass(ContextCommand.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        contextCommands = (List<ContextCommand>) HibernateUtil.fetch(criteria);

        Assert.assertNotNull("List of context commands should not be null", contextCommands);
        Assert.assertEquals("Database should only contain one context command", 1, contextCommands.size());

        aliases = contextCommands.get(0).getAliases();

        Assert.assertEquals("Context Command Aliases should have been eagerly loaded", 2, aliases.size());

        parameters = contextCommands.get(0).getParameters();

        Assert.assertEquals("Context Command Parameter should have been eagerly loaded", 1, parameters.size());
    }

    @Test
    public void testDelete(){
        List<ContextCommand> contextCommands;
        List<ContextCommandAlias> aliases;
        List<ContextCommandParameter> parameters;

        contextCommands = (List<ContextCommand>) HibernateUtil.fetch(createCriteria(ContextCommand.class));
        Assert.assertNotNull("List of context commands should not be null", contextCommands);

        HibernateUtil.delete(contextCommands.get(0).getContext());

        contextCommands = (List<ContextCommand>) HibernateUtil.fetch(createCriteria(ContextCommand.class));
        Assert.assertNotNull("List of context commands should not be null", contextCommands);
        Assert.assertEquals("Database should contain no context commands:", 0, contextCommands.size());

        aliases = (List<ContextCommandAlias>) HibernateUtil.fetch(createCriteria(ContextCommandAlias.class));
        Assert.assertNotNull("List of context command aliases should not be null", aliases);
        Assert.assertEquals("Database should contain no context command aliases:", 0, aliases.size());

        parameters = (List<ContextCommandParameter>) HibernateUtil.fetch(createCriteria(ContextCommandParameter.class));
        Assert.assertNotNull("List of context command parameters should not be null", aliases);
        Assert.assertEquals("Database should contain no context command parameters:", 0, parameters.size());
    }

    private DetachedCriteria createCriteria(Class clazz){
        DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria;
    }
}