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

public class Preferences {
    public static final String LOG4J_CONFIG_FILE = "log4j config file";
    public static final String LOG4J_TEST_CONFIG_FILE = "log4j test config file";
    public static final String WELCOME_MESSAGE = "welcome message";
    public static final String GENERIC_PROMPT = "generic prompt";
    
    private static Map<String, String> preferences = new HashMap<String, String>();

    static {
        preferences.put(LOG4J_CONFIG_FILE, "log4j.lcf");
        preferences.put(LOG4J_TEST_CONFIG_FILE, "log4j-test.lcf");
        preferences.put(WELCOME_MESSAGE, "welcome to flexmud\n.\n.\n.");
        preferences.put(GENERIC_PROMPT, "flexmud>");
    }

    public static String getPreference(String preferenceName) {
        return preferences.get(preferenceName);
    }
}
