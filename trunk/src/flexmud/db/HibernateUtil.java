package flexmud.db;

import org.hibernate.*;
import org.hibernate.cfg.*;


/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil {

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
}
