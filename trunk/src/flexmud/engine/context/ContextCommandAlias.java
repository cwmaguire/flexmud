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
@Table(name = "context_command_alias")
public class ContextCommandAlias {
    public static final String ID_PROPERTY = "id";
    public static final String CONTEXT_COMMAND_PROPERTY = "contextCommand";
    public static final String ALIAS_PROPERTY = "alias";
    public static final String IS_BULLET_PROPTERY = "isBullet";
    public static final String IS_ACCELERATOR_PROPERTY = "isAccelerator";

    private int id;
    private ContextCommand contextCommand;
    private String alias;
    private boolean isBullet;
    private boolean isAccelerator;

    @Id
    @GeneratedValue
    @Column(name = "context_command_alias_id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "alias", nullable = false)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Column(name = "is_bullet", nullable = false)
    public boolean isBullet() {
        return isBullet;
    }

    public void setBullet(boolean bullet) {
        isBullet = bullet;
    }

    @Column(name = "is_accelerator", nullable = false)
    public boolean isAccelerator() {
        return isAccelerator;
    }

    public void setAccelerator(boolean accelerator) {
        isAccelerator = accelerator;
    }

    @ManyToOne
    @JoinColumn(name = "context_command_id", nullable = false)
    public ContextCommand getContextCommand() {
        return contextCommand;
    }

    public void setContextCommand(ContextCommand contextCommand) {
        this.contextCommand = contextCommand;
    }
}
