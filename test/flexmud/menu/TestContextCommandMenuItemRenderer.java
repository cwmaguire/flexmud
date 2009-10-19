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

package flexmud.menu;

import flexmud.cfg.Preferences;
import flexmud.engine.context.ContextCommand;
import flexmud.engine.context.ContextCommandAlias;
import flexmud.log.LoggingUtil;
import flexmud.util.Util;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class TestContextCommandMenuItemRenderer {
    private static Logger LOGGER = Logger.getLogger(TestContextMenuAliasDecorator.class);
    private static String leftAcceleratorBracket;
    private static String rightAcceleratorBracket;
    private static String bulletSeparator;

    private String randomUUID;
    private String randomSingleCharString;
    private ContextCommandAlias acceleratorAlias1;
    private ContextCommandAlias bulletAlias1;
    private ContextCommand ctxCommand;

    static {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference(Preferences.LOG4J_TEST_CONFIG_FILE));

        leftAcceleratorBracket = Preferences.getPreference(Preferences.ACCELERATOR_LEFT_BRACKET);
        rightAcceleratorBracket = Preferences.getPreference(Preferences.ACCELERATOR_RIGHT_BRACKET);
        bulletSeparator = Preferences.getPreference(Preferences.BULLET_SEPERATOR);
    }

    @Before
    public void setup(){
        randomUUID = UUID.randomUUID().toString();
        randomSingleCharString = Util.getRandomSubstring(randomUUID, 1);
        acceleratorAlias1 = new ContextCommandAlias();
        bulletAlias1 = new ContextCommandAlias();
        ctxCommand = new ContextCommand();
        ctxCommand.setDescription(randomUUID);
    }

    public void testZeroAliases(){
        String menuItem = ContextCommandMenuItemRenderer.render(ctxCommand);

        Assert.assertTrue("Command with no aliases should be rended as description; ", menuItem.equals(ctxCommand.getDescription()));

        Assert.assertEquals("Right accelerator bracket was not found two positions to the right of the first occurance of the alias string",
                menuItem.indexOf(rightAcceleratorBracket, 1),
                randomUUID.indexOf(acceleratorAlias1.getAlias()) + 3);

        Assert.assertTrue("Bullet seperator and space should not be in the menu item; ", menuItem.indexOf(bulletSeparator + " ") == -1);
    }

    public void testOneAlias(){
        String menuItem;

        acceleratorAlias1.setAccelerator(true);
        acceleratorAlias1.setAlias(randomSingleCharString);

        ctxCommand.setAliases(new HashSet<ContextCommandAlias>(Arrays.asList(acceleratorAlias1)));

        menuItem = ContextCommandMenuItemRenderer.render(ctxCommand);

        LOGGER.info("Random string: " + randomUUID + ", alias: " + randomSingleCharString + ", menu item: " + menuItem);

        Assert.assertEquals("Left accelerator bracket was not found at the position of the first occurance of the alias string; ",
                menuItem.indexOf(leftAcceleratorBracket, 1),
                randomUUID.indexOf(acceleratorAlias1.getAlias()));

        Assert.assertEquals("Right accelerator bracket was not found two positions to the right of the first occurance of the alias string",
                menuItem.indexOf(rightAcceleratorBracket, 1),
                randomUUID.indexOf(acceleratorAlias1.getAlias()) + 3);

        Assert.assertTrue("Bullet seperator and space should not be in the menu item; ",
                menuItem.indexOf(bulletSeparator + " ") == -1);
    }

    @Test
    public void testMultipleAliases(){
        String menuItem;

        acceleratorAlias1.setAccelerator(true);
        acceleratorAlias1.setAlias(randomSingleCharString);

        acceleratorAlias1.setBullet(true);
        bulletAlias1.setAlias(randomSingleCharString);

        ctxCommand.setAliases(new HashSet<ContextCommandAlias>(Arrays.asList(acceleratorAlias1, bulletAlias1)));

        menuItem = ContextCommandMenuItemRenderer.render(ctxCommand);

        LOGGER.info("Random string: " + randomUUID + ", alias: " + randomSingleCharString + ", menu item: " + menuItem);

        Assert.assertEquals("Left accelerator bracket was not found at the position of the first occurance of the alias string; ",
                menuItem.indexOf(leftAcceleratorBracket, 1),
                randomUUID.indexOf(acceleratorAlias1.getAlias()) + 3);

        Assert.assertEquals("Right accelerator bracket was not found two positions to the right of the first occurance of the alias string",
                menuItem.indexOf(rightAcceleratorBracket, 1),
                randomUUID.indexOf(acceleratorAlias1.getAlias()) + 5);

        Assert.assertEquals("Bullet was not found at the position of the first occurance of the alias string; ",
                menuItem.indexOf(randomSingleCharString), 0);

        Assert.assertEquals("Bullet seperator and space were not found immediately after the bullet; ",
                menuItem.indexOf(bulletSeparator + " "),
                randomSingleCharString.length());
    }
}