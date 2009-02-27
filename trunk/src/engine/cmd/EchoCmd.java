/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

flexmud is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

flexmud is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with flexmud.  If not, see <http://www.gnu.org/licenses/>.
 */
package engine.cmd;

import engine.Command;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class EchoCmd implements Command {
    private static Logger LOGGER = Logger.getLogger(EchoCmd.class);

    public List<String> getAliases(){
        return Arrays.asList("echo");
    }

    @Override
    public void run() {
        LOGGER.info("EchoCmd class ran");
    }
}
