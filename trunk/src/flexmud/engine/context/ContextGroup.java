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

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context_group")
public class ContextGroup {
    public static final String ID_PROPERTY = "id";
    public static final String CONTEXT_PROPERTY = "context";
    public static final String CHILD_CONTEXTS_PROPERTY = "childContexts";

    private long id;
    private Context context;
    private Set<Context> childContexts = new HashSet<Context>();

    @Id @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    // get any context whose child_group_id matches this group (i.e. the parent)
    @OneToOne(mappedBy = Context.CHILD_GROUP_PROPERTY)
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // get any context that has a parent_group_id matching this group (i.e. the children)
    @OneToMany(mappedBy = Context.PARENT_GROUP_PROPERTY, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<Context> getChildContexts() {
        return childContexts;
    }

    public void setChildContexts(Set<Context> childContexts) {
        this.childContexts = childContexts;
    }
}
