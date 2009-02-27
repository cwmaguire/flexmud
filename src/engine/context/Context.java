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
package engine.context;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context")
public class Context {

    public long id;
    public String name;
    private Context parentContext;
    private Set<Context> childContexts;
    private Set<ContextCommand> commands;

    Context(){
        childContexts = new HashSet<Context>();
        commands = new HashSet<ContextCommand>();
    }

    Context(String name){
        this();
        this.name = name;
    }

    @Id
    @Column(name = "context_id")
    public long getId() {
        return id;
    }

    private void setId(long id){
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "context_id", unique = false, nullable = false, insertable = true, updatable = true)
    public Context getParentContext() {
        return parentContext;
    }

    public void setParentContext(Context parentContext) {
        this.parentContext = parentContext;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentContext")
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public Set<Context> getChildContexts() {
        return childContexts;
    }

    public void setChildContexts(Set<Context> childContexts) {
        this.childContexts = childContexts;
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "context")
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public Set<ContextCommand> getCommands() {
        return commands;
    }

    public void setCommands(Set<ContextCommand> commands) {
        this.commands = commands;
    }
}
