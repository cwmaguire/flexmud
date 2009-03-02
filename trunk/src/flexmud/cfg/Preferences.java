/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

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
package flexmud.cfg;

import java.util.HashMap;
import java.util.Map;

public class Preferences {
    private static Map<String, String> preferences = new HashMap<String, String>();

    static {
        preferences.put("log4j config file", "log4j.lcf");
        preferences.put("welcome message", "welcome to flexmud\n.\n.\n.");
    }

    public static String getPreference(String preferenceName) {
        return preferences.get(preferenceName);
    }
}
