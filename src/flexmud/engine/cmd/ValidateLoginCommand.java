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

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import flexmud.security.Account;
import flexmud.engine.context.Context;
import flexmud.db.HibernateUtil;
import flexmud.cfg.Preferences;

public class ValidateLoginCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ValidateLoginCommand.class);

    @Override
    public void run() {
        String login = client.getLogin();
        String password = client.getPassword();


        LOGGER.info("Client " + client.getConnectionID() + " logging in with login \"" + (login == null ? "[null]" : login) +
                "\" and password \"" + (password == null ? "[null]" : password) + "\"");

        if (login == null || password == null) {
            LOGGER.info("Login or Password is null for client " + client.getConnectionID() + ", sending to login");
            client.sendTextLn(Preferences.getPreference(Preferences.LOGIN_FAILED_MESSAGE));
            client.setContext(getLoginContext());
        }

        Account account = getMatchingAccount(login, password);

        if (account != null) {
            client.setAccount(account);

            LOGGER.info("Client " + client.getConnectionID() + " account set to " + account.getId() + "; sending to main menu");

            client.getContextHandler().loadAndSetFirstChildContext();
        }else{
            LOGGER.info("No account matches login credentials provided for client " + client.getConnectionID() + ", sending to login");
            client.sendTextLn(Preferences.getPreference(Preferences.LOGIN_FAILED_MESSAGE));
            client.setContext(getLoginContext());
        }
    }

    private Account getMatchingAccount(String login, String password) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
        criteria.add(Restrictions.eq(Account.LOGIN_PROPERTY, login));
        criteria.add(Restrictions.eq(Account.PASSWORD_PROPERTY, password));

        List<Account> accounts = HibernateUtil.fetch(criteria);

        if (accounts != null && !accounts.isEmpty()) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    private Context getLoginContext() {
        DetachedCriteria criteria = DetachedCriteria.forClass(Context.class);
        criteria.add(Restrictions.eq(Context.NAME_PROPERTY, "login"));

        List<Context> contexts = HibernateUtil.fetch(criteria);

        if (contexts != null && !contexts.isEmpty()) {
            return contexts.get(0);
        } else {
            return null;
        }
    }
}