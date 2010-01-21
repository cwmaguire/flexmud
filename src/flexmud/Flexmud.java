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

package flexmud;

import flexmud.cfg.Preferences;
import flexmud.engine.exec.Executor;
import flexmud.log.LoggingUtil;
import flexmud.net.ClientCommunicator;
import org.apache.log4j.Logger;

public class Flexmud {
    private static final Logger LOGGER = Logger.getLogger(Flexmud.class);
    private static final int PORT = 9000;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    public static void main(String[] args) {
        try {
            Executor.exec(new ClientCommunicator(PORT));
            //new ClientCommunicator(PORT).run();
        } catch (Exception e) {
            LOGGER.error("Error launching client communicator", e);
        }
    }
}
