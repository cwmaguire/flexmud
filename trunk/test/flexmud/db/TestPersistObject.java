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

import flexmud.log.LoggingUtil;
import flexmud.cfg.Preferences;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestPersistObject {
    private static final String OBJECT_NAME = "Object 1";

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Test
    public void testSaveObject(){
        flexmud.engine.obj.Object object = new flexmud.engine.obj.Object();
        object.setName(OBJECT_NAME);
        HibernateUtil.save(object);
        Assert.assertFalse("Object ID was not updated automatically after save", object.getId() == 0);
    }

    @Test
    public void testFetchObject(){
        List<Object> objects;
        DetachedCriteria criteria = DetachedCriteria.forClass(Object.class);
        objects = (List<Object>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Objects should not be null", objects);
        Assert.assertEquals("Database should only contain one object", 1, objects.size());
    }

    @Test
    public void testDeleteObject(){
        List<Object> objects;

        DetachedCriteria criteria = DetachedCriteria.forClass(Object.class);
        objects = (List<Object>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Objects should not be null", objects);

        HibernateUtil.delete(objects.get(0));

        objects = (List<Object>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Objects should not be null", objects);
        Assert.assertEquals("Database should contain no contexts", 0, objects.size());
    }
}

