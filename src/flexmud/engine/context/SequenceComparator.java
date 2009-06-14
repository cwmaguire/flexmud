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

import java.util.Comparator;

public class SequenceComparator implements Comparator<Sequenceable> {
    @Override
    public int compare(Sequenceable sequenceable1, Sequenceable sequenceable2) {
        int sequence1 = sequenceable1.getSequence();
        int sequence2 = sequenceable2.getSequence();

        // sort in reverse order except zero is always last
        if (sequence1 == 0) {
            return sequence2;
        } else if (sequence2 == 0) {
            return 0;
        } else {
            return sequence1 - sequence2;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContextCommand && super.equals(obj);
    }
}
