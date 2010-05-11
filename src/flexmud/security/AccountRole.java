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
package flexmud.security;

import flexmud.engine.context.Context;
import flexmud.engine.context.ContextCommand;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account_role")
public class AccountRole {

    private long id;
    private String name;
    private Set<Account> accounts = new HashSet<Account>();

    private Set<ContextCommand> commands = new HashSet<ContextCommand>();
    private Set<Context> contexts = new HashSet<Context>();

    @Id
    @GeneratedValue()
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = Account.ACCOUNT_ROLE_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @ManyToMany(targetEntity = Context.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "ACCOUNT_ROLE_CONTEXT",
            joinColumns = @JoinColumn(name = "ACCOUNT_ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "CONTEXT_ID")
    )
    public Set<Context> getContexts() {
        return contexts;
    }

    public void setContexts(Set<Context> contexts) {
        this.contexts = contexts;
    }

    @ManyToMany(targetEntity = ContextCommand.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "ACCOUNT_ROLE_COMMAND",
            joinColumns = @JoinColumn(name = "ACCOUNT_ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "CONTEXT_COMMAND_ID")
    )
    public Set<ContextCommand> getCommands() {
        return commands;
    }

    public void setCommands(Set<ContextCommand> commands) {
        this.commands = commands;
    }

    public boolean hasPermission(Context context) {
        return new ArrayList<Context>(contexts).contains(context);
    }

    public boolean hasPermission(ContextCommand contextCommand) {
        return new ArrayList<ContextCommand>(commands).contains(contextCommand);
    }

}
