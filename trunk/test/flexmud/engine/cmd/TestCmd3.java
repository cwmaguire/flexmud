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

import org.apache.log4j.Logger;

public class TestCmd3 extends Command {
    private static Logger LOGGER = Logger.getLogger(TestCmd3.class);
    private static int runCount;
    private static long lastRunMillis;

    @Override
    public void run() {
        runCount++;
        lastRunMillis = System.currentTimeMillis();
        LOGGER.info("TestCmd3 class ran");
    }

    public static int getRunCount() {
        return runCount;
    }

    public static void resetRunCount() {
        runCount = 0;
    }

    public static long getLastRunMillis(){
        return lastRunMillis;
    }
}