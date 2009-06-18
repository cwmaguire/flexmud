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
package flexmud.util;

import flexmud.engine.context.*;

import java.util.HashSet;
import java.util.Arrays;

public class ContextUtil {
    public static ContextCommand createContextCommand(Class clazz){
        ContextCommand cntxtCmd = new ContextCommand();
        cntxtCmd.setCommandClassName(clazz.getName());
        cntxtCmd.setSequence(0);
        return cntxtCmd;
    }

    public static ContextCommand createContextCommand(Class clazz, ContextCommandFlag flag) {
        ContextCommand cntxtCmd = createContextCommand(clazz);
        cntxtCmd.setContextCommandFlag(flag);
        return cntxtCmd;
    }

    public static Context createContextHierarchy() {
        FakeContext parentCntxt = new FakeContext(Long.MAX_VALUE);
        FakeContext childCntxt = new FakeContext(Long.MAX_VALUE - 1);
        ContextGroup cntxtGroup = new ContextGroup();
        cntxtGroup.setChildContexts(new HashSet<Context>(Arrays.asList(childCntxt)));
        parentCntxt.setChildGroup(cntxtGroup);
        return parentCntxt;
    }
}
