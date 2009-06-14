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
public class ContextCommandParameter implements Sequenceable{
    public static final String ID_PROPERTY = "id";
    public static final String VALUE_PROPERTY = "value";
    public static final String SEQUENCE_PROPERTY = "sequence";
    public static final String CONTEXT_COMMAND_PROPERTY = "contextCommand";

    private int id;
    private ContextCommand contextCommand;
    private String value;
    private Integer sequence;

    @Id
    @GeneratedValue
    @Column(name = "id")
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

    @Column(name = "param_value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "sequence", nullable = false)
    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
