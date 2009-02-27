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
package engine.obj;

import javax.persistence.*;

@Entity
@Table(name = "object")
public class Object {
    private long id;

    public Object(){

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
