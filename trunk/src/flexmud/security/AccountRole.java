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

import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.Context;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name="account_role")
public class AccountRole {

    private long id;
    private String name;
    private Set<Account> accounts = new HashSet<Account>();

    private Set<ContextCommand> restrictedContextCommands = new HashSet<ContextCommand>();
    private Set<Context> restrictedContexts = new HashSet<Context>();
  
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

    @OneToMany(mappedBy = Account.ACCOUNT_ROLE_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @ManyToMany( targetEntity=Context.class, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    public Set<Context> getRestrictedContexts() {
        return restrictedContexts;
    }

    public void setRestrictedContexts(Set<Context> restrictedContexts) {
        this.restrictedContexts = restrictedContexts;
    }

    @ManyToMany( targetEntity=ContextCommand.class, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    public Set<ContextCommand> getRestrictedContextCommands() {
        return restrictedContextCommands;
    }

    public void setRestrictedContextCommands(Set<ContextCommand> restrictedContextCommands) {
        this.restrictedContextCommands = restrictedContextCommands;
    }

    public boolean hasPermission(Context context){
        return !restrictedContexts.contains(context);
    }

    public boolean hasPermission(ContextCommand contextCommand){
        return !restrictedContextCommands.contains(contextCommand);
    }

}
