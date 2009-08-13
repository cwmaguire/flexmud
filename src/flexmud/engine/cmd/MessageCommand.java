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

import flexmud.db.HibernateUtil;
import flexmud.engine.context.Message;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class MessageCommand extends Command{
    private static final Logger LOGGER = Logger.getLogger(MessageCommand.class);

    @Override
    public void run() {
        List<String> cmdArguments = getCommandArguments();
        if (cmdArguments.isEmpty()) {
            return;
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
        criteria.add(Restrictions.eq(Message.ID_PROPERTY, Long.parseLong(cmdArguments.get(0))));

        List<Message> messages = HibernateUtil.fetch(criteria);

        if (messages != null && !messages.isEmpty()) {
            LOGGER.info("Sending message" + messages.get(0).getName() + " to client " + getClient().getConnectionID());
            getClient().sendTextLn(messages.get(0).getMessage());
        }
    }

}
