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
package log;

import config.pref.Preferences;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoggingTest {
    private static Logger LOGGER = Logger.getLogger(LoggingTest.class);
    private static final String TEST_LOG_MESSAGE = "Test Log Message";
    private static final String TEST_LOG_FILE_NAME = "testLog4j.log";

    private void clearLogFile() throws IOException {
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(TEST_LOG_FILE_NAME));
            bufferedWriter.write(new char[]{});
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    // ToDo CM: Anyone know how to fix this? It's not actually deleting the file.
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4041126
    private void deleteLogFile() {
        File testLogFile = new File(TEST_LOG_FILE_NAME);

        System.out.println("Current directory: " + System.getProperty("user.dir"));
        System.out.println("Test log file name: " + testLogFile.getName());
        System.out.println("Test log file path: " + testLogFile.getAbsolutePath());
        System.out.println("Test log file exists: " + testLogFile.exists());
        System.out.println("Test log file writable: " + testLogFile.canWrite());

        if (!testLogFile.canWrite()) {
            throw new RuntimeException( "Test log file could not be deleted, write protected: " + TEST_LOG_FILE_NAME);
        }

        if (!testLogFile.delete()) {
            throw new RuntimeException("Test log file not deleted");
        }

    }

    private List<String> readLogFileByLines() throws IOException {

        List<String> linesFromFile = new ArrayList<String>();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(TEST_LOG_FILE_NAME));

            String lineFromFile;
            while ((lineFromFile = bufferedReader.readLine()) != null) {
                linesFromFile.add(lineFromFile);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return linesFromFile;
    }

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        // files in Windows XP just won't seem to delete:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4041126
        // deleteLogFile();

        try {
            clearLogFile();
        } catch (IOException e) {
            System.out.println("Could not clear test log file: " + e.getMessage());
        }
    }

    @Test
    public void testDefaultConfigurationLogging() {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging();

        List<String> logFileLines = new ArrayList<String>();
        LOGGER.info(TEST_LOG_MESSAGE);

        try {
            logFileLines = readLogFileByLines();
        } catch (IOException e) {
            Assert.fail("Failed to read log file: " + e.getMessage());
        }

        Assert.assertNotNull("Failed to read log file", logFileLines);
        Assert.assertEquals( "Incorrect number of log file lines read; expected 1, read " + logFileLines.size(), 1, logFileLines.size());
        Assert.assertTrue( "Expected text not found; expected " + TEST_LOG_MESSAGE, logFileLines.get(0).contains(TEST_LOG_MESSAGE));
    }

    @Test
    public void testFileConfigurationLogging() {
        LoggingUtil.resetConfiguration();
        LoggingUtil.configureLogging(Preferences.getPreference("log4j config file"));

        List<String> logFileLines = new ArrayList<String>();
        LOGGER.info(TEST_LOG_MESSAGE);

        try {
            logFileLines = readLogFileByLines();
        } catch (IOException e) {
            Assert.fail("Failed to read log file: " + e.getMessage());
        }

        Assert.assertNotNull("Failed to read log file", logFileLines);
        Assert.assertEquals( "Incorrect number of log file lines read; expected 1, read " + logFileLines.size(), 1, logFileLines.size());
        Assert.assertTrue( "Expected text not found; expected " + TEST_LOG_MESSAGE, logFileLines.get(0).contains(TEST_LOG_MESSAGE));
    }
}
