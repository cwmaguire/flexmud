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

import flexmud.PersistedComparable;
import flexmud.engine.cmd.Command;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "context_command")
public class ContextCommand extends PersistedComparable implements Sequenceable {
    private static final Logger LOGGER = Logger.getLogger(ContextCommand.class);

    public static final String ID_PROPERTY = "id";
    public static final String CONTEXT_PROPERTY = "context";
    public static final String ALIASES_PROPERTY = "aliases";
    public static final String PARAMETERS_PROPERTY = "parameters";
    public static final String COMMAND_CLASS_NAME_PROPERTY = "commandClassName";

    protected long id;
    private Set<ContextCommandAlias> aliases = new HashSet<ContextCommandAlias>();
    private Set<ContextCommandParameter> parameters = new HashSet<ContextCommandParameter>();
    private String commandClassName;
    private Context context;
    private String description;
    private ContextCommandFlag contextCommandFlag;
    private Integer sequence = 0;
    private String name;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = ContextCommandAlias.CONTEXT_COMMAND_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ContextCommandAlias> getAliases() {
        return aliases;
    }

    public void setAliases(Set<ContextCommandAlias> aliases) {
        this.aliases = aliases;
    }

    @OneToMany(mappedBy = ContextCommandParameter.CONTEXT_COMMAND_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ContextCommandParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ContextCommandParameter> parameters) {
        this.parameters = parameters;
    }

    @Column(name = "command_class", nullable = false)
    public String getCommandClassName() {
        return commandClassName;
    }

    public void setCommandClassName(String commandClassName) {
        this.commandClassName = commandClassName;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "flag")
    @Enumerated(value = EnumType.STRING)
    public ContextCommandFlag getContextCommandFlag() {
        return contextCommandFlag;
    }

    public void setContextCommandFlag(ContextCommandFlag contextCommandFlag) {
        this.contextCommandFlag = contextCommandFlag;
    }

    @ManyToOne
    @JoinColumn(name = "context_id", nullable = false)
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Column(name = "sequence", nullable = true)
    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = (sequence == null ? 0 : sequence);
    }

    public Command createCommandInstance() {
        Class commandClass = getContextCommandClass();
        if (commandClass != null) {
            try {
                return (Command) commandClass.newInstance();
            } catch (Exception e) {
                LOGGER.error("Could not instantiate Command of class " + commandClass.getName(), e);
            }
        }
        return null;
    }

    @Transient
    private Class getContextCommandClass() {
        Class classFromName = null;
        if (commandClassName != null) {
            try {
                classFromName = Class.forName(commandClassName);
            } catch (Exception e) {
                LOGGER.error("Could not load command " + commandClassName + " for context command " + id, e);
                return null;
            }
        }
        return classFromName;
    }
}
