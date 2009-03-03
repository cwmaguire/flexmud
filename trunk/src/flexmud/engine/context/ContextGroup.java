/*
Copyright 2008 Chris Maguire (cwmaguire@gmail.com)

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

package flexmud.engine.context;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context_group")
public class ContextGroup {
    public static final String ID_PROPERTY = "id";
    public static final String PARENT_CONTEXT_PROPERTY = "parentContext";
    public static final String CHILD_CONTEXTS_PROPERTY = "childContexts";

    private long id;
    private Context parentContext;
    private Set<Context> childContexts = new HashSet<Context>();

    @Id @GeneratedValue
    @Column(name = "context_group_id")
    public long getId() {
        return id;
    }

    private void setId(long id){
        this.id = id;
    }

    @OneToOne(mappedBy = "childGroup")
    public Context getParentContext() {
        return parentContext;
    }

    public void setParentContext(Context parentContext) {
        this.parentContext = parentContext;
    }

    @OneToMany(mappedBy = Context.PARENT_GROUP_PROPERTY, cascade = CascadeType.ALL)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    public Set<Context> getChildContexts() {
        return childContexts;
    }

    public void setChildContexts(Set<Context> childContexts) {
        this.childContexts = childContexts;
    }
}
