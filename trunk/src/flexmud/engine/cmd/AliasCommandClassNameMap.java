/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

Flexmud is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Flexmud is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with flexmud.  If not, see <http://www.gnu.org/licenses/>.
 */
package flexmud.engine.cmd;

import flexmud.engine.context.Context;
import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alias_command")
public class AliasCommandClassNameMap {
    public static final String ID_PROPERTY = "id";
    public static final String CONTEXT_PROPERTY = "context";
    public static final String ALIASES_PROPERTY = "aliases";
    public static final String COMMAND_PROPERTY = "command";

    private long id;
    private Set<String> aliases = new HashSet<String>();
    private String commandClassName;
    private Context context;

    @Id
    @GeneratedValue
    @Column(name = "alias_command_id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @CollectionOfElements(fetch = FetchType.EAGER)
    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    @Column(name = "command")
    public String getCommandClassName() {
        return commandClassName;
    }

    public void setCommand(String commandClassName) {
        this.commandClassName = commandClassName;
    }

    @ManyToOne
    @JoinColumn(name = "context_id", nullable = true)
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
