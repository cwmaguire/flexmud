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

import flexmud.cfg.Preferences;
import flexmud.net.Client;

public class ContextOrGenericPromptCommand extends Command {
    @Override
    public void run() {
        String prompt;

        if (getClient() != null) {
            prompt = getContextOrGenericPrompt(getClient());
            getClient().sendText(prompt);
        }
    }

    private String getContextOrGenericPrompt(Client client) {
        String prompt = client.getContext().getPrompt();
        if (prompt == null) {
            prompt = Preferences.getPreference(Preferences.GENERIC_PROMPT);
        }
        return prompt;
    }
}
