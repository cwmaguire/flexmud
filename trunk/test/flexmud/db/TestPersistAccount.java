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

package flexmud.db;

import flexmud.cfg.Preferences;
import flexmud.log.LoggingUtil;
import flexmud.security.Account;
import flexmud.security.AccountRole;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.UUID;

public class TestPersistAccount {
    private static final String username = UUID.randomUUID().toString();
    private static final String password = UUID.randomUUID().toString();

    private Account account;
    private AccountRole accountRole;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_CONFIG_FILE));
    }

    @Before
    public void setup(){
        accountRole = new AccountRole();
        accountRole.setName("TEST_ROLE");
        HibernateUtil.save(accountRole);
    }

    @After
    public void teardown(){
        HibernateUtil.delete(accountRole);
    }

    @Test
    public void testPersistAccount(){
        testSaveAccount();
        testSelectAccount();
        testDeleteAccount();
    }

    private void testSaveAccount() {
        account = new Account();
        account.setLogin(username);
        account.setPassword(password);
        account.setAccountRole(accountRole);
        HibernateUtil.save(account);
        Assert.assertFalse("Acount ID was not updated automatically after save", account.getId() == 0);
    }

    public void testSelectAccount(){
        List<Account> accounts;
        DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
        criteria.add(Restrictions.eq(Account.ID_PROPERTY, account.getId()));
        accounts = (List<Account>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Accounts should not be null", accounts);
        Assert.assertEquals("Database should contain one account:", 1, accounts.size());
        Assert.assertEquals("Account contains wrong username", username, accounts.get(0).getLogin());
        Assert.assertEquals("Account contains wrong password", password, accounts.get(0).getPassword());
    }

    public void testDeleteAccount(){
        List<Account> accounts;

        HibernateUtil.delete(account);

        DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
        criteria.add(Restrictions.eq(Account.ID_PROPERTY, account.getId()));
        accounts = (List<Account>) HibernateUtil.fetch(criteria);
        Assert.assertNotNull("List of Accounts should not be null", accounts);
        Assert.assertEquals("Database should contain no Accounts", 0, accounts.size());
    }
}