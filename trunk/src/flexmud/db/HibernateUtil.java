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

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.DetachedCriteria;

import java.util.List;


/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil {
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new AnnotationConfiguration().configure("/flexmud/db/hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        // Alternatively, we could look up in JNDI here
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

    public static List fetch(DetachedCriteria detachedCriteria){
        Session session;
        Transaction transaction;

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        Criteria criteria = detachedCriteria.getExecutableCriteria(session);

        try{
            return criteria.list();
        }catch(Exception e){
            LOGGER.error("Error retrieving data for criteria", e);
            return null;
        }finally{
            transaction.commit();
            session.close();
        }
    }

    public static void save(Object obj){
        Session session;
        Transaction transaction;

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        try{
            session.save(obj);
            transaction.commit();
        }catch(Exception e){
            LOGGER.error("Error saving object", e);
            transaction.rollback();
        }finally{
            session.close();
        }
    }

    public static void delete(Object obj){
        Session session;
        Transaction transaction;

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        try{
            session.delete(obj);
        }catch(Exception e){
            LOGGER.error("Error saving object", e);
        }finally{
            transaction.commit();
            session.close();
        }
    }
}
