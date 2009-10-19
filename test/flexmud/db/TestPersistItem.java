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

import flexmud.cfg.Preferences;
import flexmud.log.LoggingUtil;
import flexmud.engine.item.Item;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestPersistItem {
    private static final String ITEM_NAME = "Item 1";

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Test
    public void testSaveObject(){
        Item item = new Item();
        item.setName(ITEM_NAME);
        HibernateUtil.save(item);
        Assert.assertFalse("Object ID was not updated automatically after save", item.getId() == 0);
    }

    @Test
    public void testFetchObject(){
        List<Item> items;
        DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
        items = (List<Item>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Items should not be null", items);
        Assert.assertEquals("Database should only contain one item", 1, items.size());
    }

    @Test
    public void testDeleteObject(){
        List<Item> items;

        DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
        items = (List<Item>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Items should not be null", items);

        HibernateUtil.delete(items.get(0));

        items = (List<Item>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Objects should not be null; ", items);
        Assert.assertEquals("Database should contain no objects; ", 0, items.size());
    }
}

