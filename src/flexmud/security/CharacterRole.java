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
import flexmud.engine.character.Character;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name="character_role")
public class CharacterRole {

    private long id;
    private String name;
    private Set<Character> characters = new HashSet<Character>();

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

    @OneToMany(mappedBy = Character.CHARACTER_ROLE_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    public Set<flexmud.engine.character.Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    @ManyToMany( targetEntity=Context.class, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="CHARACTER_ROLE_CONTEXT",
        joinColumns=@JoinColumn(name="ACCOUNT_ROLE_ID"),
        inverseJoinColumns=@JoinColumn(name="CONTEXT_ID")
    )
    public Set<Context> getContexts() {
        return contexts;
    }

    public void setContexts(Set<Context> contexts) {
        this.contexts = contexts;
    }

    @ManyToMany( targetEntity=ContextCommand.class, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="CHARACTER_ROLE_COMMAND",
        joinColumns=@JoinColumn(name="ACCOUNT_ROLE_ID"),
        inverseJoinColumns=@JoinColumn(name="CONTEXT_COMMAND_ID")
    )
    public Set<ContextCommand> getCommands() {
        return commands;
    }

    public void setCommands(Set<ContextCommand> commands) {
        this.commands = commands;
    }

    public boolean hasPermission(Context context){
        return !contexts.contains(context);
    }

    public boolean hasPermission(ContextCommand contextCommand){
        return !commands.contains(contextCommand);
    }

}