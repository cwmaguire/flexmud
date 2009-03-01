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

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context_group")
public class ContextGroup {
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

    @OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL)
    @org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    public Set<Context> getChildContexts() {
        return childContexts;
    }

    public void setChildContexts(Set<Context> childContexts) {
        this.childContexts = childContexts;
    }
}
