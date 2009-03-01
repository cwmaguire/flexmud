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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestPersistObject {
    private static final String OBJECT_NAME = "Object 1";
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
    public void testObject(){
        flexmud.engine.obj.Object object = new flexmud.engine.obj.Object();
        object.setName(OBJECT_NAME);
        session.save(object);
        Assert.assertFalse("Object ID was not updated automatically after save", object.getId() == 0);
    }

    @Test
    public void testSelectContext(){
        List<Object> objects;
        Criteria criteria = session.createCriteria(Object.class);
        objects = (List<Object>) criteria.list();
        Assert.assertEquals("Database should only contain one object", 1, objects.size());
    }

    @Test
    public void testDeleteObject(){
        List<Object> objects;

        Criteria criteria = session.createCriteria(Object.class);
        objects = (List<Object>) criteria.list();

        session.delete(objects.get(0));

        objects = (List<Object>) criteria.list();
        Assert.assertEquals("Database should contain no contexts", 0, objects.size());
    }
}

