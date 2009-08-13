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

package flexmud.menu;

import org.junit.Test;
import org.junit.Assert;
import flexmud.engine.context.ContextCommandAlias;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class TestContextMenuRenderer {

    // ToDo: I think this can be removed because I can't see why accelerators have to be handled before bullets

    /*@Test
    public void testBulletComesAfterAccelerator(){
        ContextCommandAlias alias1 = new ContextCommandAlias();
        ContextCommandAlias alias2 = new ContextCommandAlias();

        alias2.setBullet(true);

        List<ContextCommandAlias> aliases = Arrays.asList(alias2, alias1);
        Collections.sort(aliases);

        Assert.assertEquals("Bullet alias did not come after accelerator alias; ", aliases.get(0), alias1);
    }

    @Test
    public void testAcceleratorComesBeforeNeither() {
        Assert.fail("You need to fill in the rest of the context men renderer sorting tests");
    }

    @Test
    public void testAcceleratorComesBeforeBoth() {
    }

    @Test
    public void testAcceleratorBulletComesBeforeNeither() {
    }

    @Test
    public void testAcceleratorBulletComesBeforeBullet() {
    }

    @Test
    public void testNeitherComesAfterAcceleratorBullet() {
    }

    @Test
    public void testNeitherComesAfterAccelerator() {
    }

    @Test
    public void testBulletComesAfterNeither() {
    }

    @Test
    public void testBulletComesAfterAlias() {
    }

    @Test
    public void testBulletComesAfterBoth() {
    }

    @Test
    public void testBothComesAfterAlias() {
    }*/
}
