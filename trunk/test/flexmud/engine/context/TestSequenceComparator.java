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
package flexmud.engine.context;

import org.junit.Test;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

public class TestSequenceComparator {

    @Test
    public void testSorting() {
        Sequenceable sequenceable1 = new Sequenceable() {
            @Override
            public Integer getSequence() {
                return 1;
            }
        };

        Sequenceable sequenceable2 = new Sequenceable() {
            @Override
            public Integer getSequence() {
                return 2;
            }
        };

        Sequenceable sequenceable0 = new Sequenceable() {
            @Override
            public Integer getSequence() {
                return 0;
            }
        };

        List<Sequenceable> sequenceables = Arrays.asList(sequenceable0, sequenceable2, sequenceable1);
        Collections.sort(sequenceables, new SequenceComparator());

        Assert.assertEquals("Sequenceable1 should be first", sequenceable1, sequenceables.get(0));
        Assert.assertEquals("Sequenceable2 should be second", sequenceable2, sequenceables.get(1));
        Assert.assertEquals("Sequenceable0 should be third", sequenceable0, sequenceables.get(2));
    }
}
