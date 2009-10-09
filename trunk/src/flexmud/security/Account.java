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

import flexmud.engine.character.AccountCharacter;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "account")
public class Account {
    public static final String ID_PROPERTY = "id";
    public static final String LOGIN_PROPERTY = "login";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String ACCOUNT_CHARACTERS_PROPERTY = "accountCharacters";

    private long id;
    private String login;
    private String password;
    private Set<AccountCharacter> accountCharacters = new HashSet<AccountCharacter>();

    @Id
    @GeneratedValue()
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = AccountCharacter.ACCOUNT_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<AccountCharacter> getAccountCharacters() {
        return accountCharacters;
    }

    public void setAccountCharacters(Set<AccountCharacter> accountCharacters) {
        this.accountCharacters = accountCharacters;
    }


}