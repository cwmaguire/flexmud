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

import javax.persistence.*;

@Entity
@Table(name = "context_command_parameter")
public class ContextCommandParameter {
    public static final String ID_PROPERTY = "id";
    public static final String CLASS_PROPERTY = "clazz";
    public static final String VALUE_PROPERTY = "value";

    private int id;
    private ContextCommand contextCommand;
    private String clazz;
    private String value;

    @Id
    @GeneratedValue
    @Column(name = "context_command_alias_id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "context_command_id", nullable = false)
    public ContextCommand getContextCommand() {
        return contextCommand;
    }

    public void setContextCommand(ContextCommand contextCommand) {
        this.contextCommand = contextCommand;
    }

    @Column(name = "clazz", nullable = false)
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Column(name = "value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
