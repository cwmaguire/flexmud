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
package flexmud.cfg;

import java.util.HashMap;
import java.util.Map;

public enum Preferences {
    LOG4J_CONFIG_FILE,
    LOG4J_TEST_CONFIG_FILE,
    WELCOME_MESSAGE,
    GENERIC_PROMPT,
    LOGIN_FAILED_MESSAGE,
    BULLET_SEPERATOR;

    private static final Map<Enum, String> preferences = new HashMap<Enum, String>();

    static {
        preferences.put(LOG4J_CONFIG_FILE, "log4j.lcf");
        preferences.put(LOG4J_TEST_CONFIG_FILE, "log4j-test.lcf");
        preferences.put(GENERIC_PROMPT, "flexmud>");
        preferences.put(LOGIN_FAILED_MESSAGE, "login failed.");
        preferences.put(BULLET_SEPERATOR, ")");
    }

    public static String getPreference(Enum preferenceName) {
        return preferences.get(preferenceName);
    }
}
