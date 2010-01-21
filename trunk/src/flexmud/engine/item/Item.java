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
package flexmud.engine.item;

import javax.persistence.*;

@Entity
@Table(name = "object")
public class Item {
    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";

    private long id;
    private String name;

    public Item() {

    }

    @Id
    @GeneratedValue
    @Column(name = "object_id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*

   Everything is an object

   Every single object can potentially move, attack, be attacked, fire events, etc.

   So do we need to have separate classes for "Character" objects and "Mob" objects and "Item" objects?

   Maybe instead of inheritance, we can glob together each item's capabilities from components:
       - Attack strategy
       - Skills
       - Stats
       - etc.

    */
}
