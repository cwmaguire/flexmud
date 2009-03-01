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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import flexmud.log.LoggingUtil;

public class TestPersisteContext {
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
    public void testCreateContext(){
        Context context = new Context("context 1");
        session.save(context);
    }
}
