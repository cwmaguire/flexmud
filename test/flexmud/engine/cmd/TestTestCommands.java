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

package flexmud.engine.cmd;

import junit.framework.Assert;
import org.junit.Test;

public class TestTestCommands {

    @Test
    public void testTestCommands(){
        TestCmd.resetRunCount();
        TestCmd2.resetRunCount();
        TestCmd3.resetRunCount();

        new TestCmd().run();
        new TestCmd2().run();
        new TestCmd3().run();

        Assert.assertEquals("TestCmd did not record running once", TestCmd.getRunCount(), 1);
        Assert.assertEquals("TestCmd2 did not record running once", TestCmd2.getRunCount(), 1);
        Assert.assertEquals("TestCmd3 did not record running once", TestCmd3.getRunCount(), 1);

        Assert.assertTrue("TestCmd did not record run time", TestCmd.getLastRunMillis() != 0);
        Assert.assertTrue("TestCmd2 did not record run time", TestCmd2.getLastRunMillis() != 0);
        Assert.assertTrue("TestCmd3 did not record run time", TestCmd3.getLastRunMillis() != 0);
    }
}
