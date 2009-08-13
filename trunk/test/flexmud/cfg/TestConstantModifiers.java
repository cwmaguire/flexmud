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

import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestConstantModifiers {
    @Test
    public void testAllConstantModifiers(){
        int fieldModifiers;

        for(Field field : Constants.class.getDeclaredFields()){
            fieldModifiers = field.getModifiers();
            Assert.assertTrue("Constant " + field.getName() + " should be final: ", Modifier.isFinal(fieldModifiers));
            Assert.assertTrue("Constant " + field.getName() + " should be public: ", Modifier.isPublic(fieldModifiers));
            Assert.assertTrue("Constant " + field.getName() + " should be static: ", Modifier.isStatic(fieldModifiers));
        }
    }
}
