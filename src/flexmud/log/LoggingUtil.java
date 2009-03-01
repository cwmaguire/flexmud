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
package flexmud.log;

import flexmud.cfg.Preferences;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.Properties;

public class LoggingUtil {
    public static void configureLogging(String loggingConfigurationFile) {
        configureLogging(new File(loggingConfigurationFile));
    }

    public static void configureLogging(File loggingConfigurationFile) {
        if (loggingConfigurationFile.canRead()) {
            PropertyConfigurator.configure(loggingConfigurationFile.toString());
        } else {
            configureLogging();
        }
    }

    public static void configureLogging() {
        File log4jConfigurationFile = new File(Preferences.getPreference("log4j config file"));
        if (log4jConfigurationFile.canRead()) {
            PropertyConfigurator.configure(Preferences.getPreference("log4j config file"));
        } else {
            PropertyConfigurator.configure(createDefaultLoggingProperties());
        }
    }

    private static Properties createDefaultLoggingProperties() {
        Properties log4jProperties = new Properties();
        log4jProperties.put("log4j.rootLogger", "DEBUG, rollingFileAppender");
        log4jProperties.put("log4j.appender.rollingFileAppender", "org.apache.log4j.RollingFileAppender");
        log4jProperties.put("log4j.appender.rollingFileAppender.File", "flexmud.flexmud.log");
        log4jProperties.put("log4j.appender.rollingFileAppender.MaxFileSize", "100KB");
        log4jProperties.put("log4j.appender.rollingFileAppender.MaxBackupIndex", "1");
        log4jProperties.put("log4j.appender.rollingFileAppender.layout", "org.apache.log4j.PatternLayout");
        log4jProperties.put("log4j.appender.rollingFileAppender.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
        return log4jProperties;
    }

    public static void resetConfiguration() {
        LogManager.resetConfiguration();
    }

}
