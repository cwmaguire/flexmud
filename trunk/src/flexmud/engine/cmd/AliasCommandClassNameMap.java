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
package flexmud.engine.cmd;

import flexmud.engine.context.Context;
import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context_command")
public class AliasCommandClassNameMap {
    public static final String ID_PROPERTY = "id";
    public static final String CONTEXT_PROPERTY = "context";
    public static final String ALIASES_PROPERTY = "aliases";
    public static final String COMMAND_CLASS_NAME_PROPERTY = "commandClassName";

    private long id;
    private Set<String> aliases = new HashSet<String>();
    private String commandClassName;
    private Context context;

    @Id
    @GeneratedValue
    @Column(name = "context_command_id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @CollectionOfElements(fetch = FetchType.EAGER)
    @Column(name = "alias")
    @JoinTable(name = "context_command_alias", joinColumns= {@JoinColumn(name = "context_command_id")})
    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    @Column(name = "command_class", nullable = false)
    public String getCommandClassName() {
        return commandClassName;
    }

    public void setCommandClassName(String commandClassName) {
        this.commandClassName = commandClassName;
    }

    @ManyToOne
    @JoinColumn(name = "context_id", nullable = false)
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
