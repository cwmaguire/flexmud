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
package flexmud.engine.context;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context")
public class Context {

    private long id;
    private String name;
    private ContextGroup childGroup;
    private ContextGroup parentGroup;
    private Set<String> commands = new HashSet<String>();

    public Context(){
    }

    public Context(String name){
        this();
        this.name = name;
    }

    @Id
    @GeneratedValue
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

    @OneToOne(targetEntity = ContextGroup.class, cascade = CascadeType.ALL, optional = true)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name = "child_group_id")
    public ContextGroup getChildGroup(){
        return childGroup;
    }

    public void setChildGroup(ContextGroup childGroup) {
        this.childGroup = childGroup;
    }

    @ManyToOne
    @JoinColumn(name = "parent_group_id", nullable = true)
    public ContextGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(ContextGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    @CollectionOfElements( targetElement = String.class )
    @JoinTable(name = "context_command", joinColumns = @JoinColumn(name = "context_id"))
    @Column(name = "command", nullable = false)
    public Set<String> getCommands() {
        return commands;
    }

    public void setCommands(Set<String> commands) {
        this.commands = commands;
    }
}
