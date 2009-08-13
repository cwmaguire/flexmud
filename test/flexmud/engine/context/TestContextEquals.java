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

package flexmud.engine.context;

import flexmud.db.HibernateUtil;
import flexmud.util.ContextUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestContextEquals {
    private Context realCntxt;

    @Before
    public void setup(){
        realCntxt = new Context();
        HibernateUtil.save(realCntxt);
    }

    @After
    public void tearDown(){
        HibernateUtil.delete(realCntxt);
    }

    @Test
    public void testEquals() {
        FakeContext fakeCntxt = new FakeContext(realCntxt.getId());
        FakeContext fakeCntxt2 = new FakeContext(realCntxt.getId());
        Context fakeCntxtParent = ContextUtil.createContextHierarchy();
        Assert.assertFalse("Context and child Context should not be equal", fakeCntxtParent.equals(fakeCntxtParent.getChildGroup().getChildContexts().iterator().next()));
        Assert.assertEquals("A FakeContext and a Context with the same ID should be equal", fakeCntxt, realCntxt);
        Assert.assertEquals("Two FakeContexts with the same ID should be equal", fakeCntxt, fakeCntxt2);
    }
}