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

import flexmud.engine.context.ContextCommandAlias;
import flexmud.cfg.Preferences;

public class ContextMenuAliasDecorator {

    // this will handle accelerators before bullets otherwise the
    // accelerator will trip over the bullet.
    // Question: how would the accelerator trip over the bullet?
    // Multiple aliases that are accelerators and bullets will get ugly;
    // if you decide to clean this up please update the unit test.
    public static String decorate(String string, ContextCommandAlias alias){
        String aliasString = alias.getAlias();
        String decoratedString = string;
        if(aliasString != null && !aliasString.isEmpty()){
            if(alias.isAccelerator()){
                decoratedString = getAcceleratedString(string, aliasString);
            }

            if(alias.isBullet()){
                decoratedString = getBulletedString(decoratedString, aliasString);
            }
        }

        return decoratedString;
    }

    private static String getAcceleratedString(String string, String aliasString) {
        return string.replaceFirst(aliasString, getAccelerator(aliasString));
    }

    private static String getAccelerator(String string){
        String leftAcceleratorBracket = Preferences.getPreference(Preferences.ACCELERATOR_LEFT_BRACKET);
        String rightAcceleratorBracket = Preferences.getPreference(Preferences.ACCELERATOR_RIGHT_BRACKET);
        return leftAcceleratorBracket + string + rightAcceleratorBracket;
    }

    private static String getBulletedString(String string, String bullet){
        return bullet + Preferences.getPreference(Preferences.BULLET_SEPERATOR) + " " + string;
    }
}
