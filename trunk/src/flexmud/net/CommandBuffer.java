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
package flexmud.net;

import flexmud.cfg.Constants;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class CommandBuffer {
    private static final Logger LOGGER = Logger.getLogger(CommandBuffer.class);
    private static final Object CHAR_BUFFER_LOCK = new Object();
    private static final Object COMPLETE_COMMANDS_LOCK = new Object();

    private ArrayList<Character> charBuffer;
    private ArrayList<String> completeCmds;

    /**
     * Default constructor.
     */
    public CommandBuffer() {
        this.completeCmds = new ArrayList<String>();
        this.charBuffer = new ArrayList<Character>();
    }

    public void write(byte[] data) throws IOException {
        this.write(data, data.length);
    }

    public void write(byte[] bytes, int length) throws IOException {

        if (bytes.length < 1) {
            return;
        }

        if (length > bytes.length) {
            length = bytes.length;
        }

        char c;

        for (int i = 0; i < length; ++i) {
            c = (char) bytes[i];

            synchronized (CHAR_BUFFER_LOCK) {
                this.charBuffer.add(c);
            }
        }

    }

    public void readFromSocketChannel(SocketChannel socketChannel) throws ClosedChannelException {
        try {
            ByteBuffer eightBitCharBuffer = ByteBuffer.allocate(128);

            int read = socketChannel.read(eightBitCharBuffer);
            if (read == -1) {
                throw new IOException();
            }

            while (read > 0) {
                eightBitCharBuffer.flip();

                this.write(eightBitCharBuffer.array(), eightBitCharBuffer.limit());
                eightBitCharBuffer.clear();

                read = socketChannel.read(eightBitCharBuffer);
                if (read == -1) {
                    throw new IOException();
                }
            }

        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void readFromInputStream(InputStream is) throws IOException {
        DataInputStream inputStream;
        char input;

        try {
            inputStream = new DataInputStream(is);
            while (inputStream.available() >= 2) {
                input = inputStream.readChar();

                LOGGER.info("CommandBuffer.write() new char: '" + input + "'");

                synchronized (CHAR_BUFFER_LOCK) {
                    this.charBuffer.add(input);
                }
            }
        } catch (EOFException eofe) {
            // we're done
        }
    }

    public void parseCommands() {
        int crIndex;
        String cmd;
        char[] chars;

        synchronized (CHAR_BUFFER_LOCK) {
            crIndex = findCarriageReturnPos();

            if (isCommandComplete(crIndex)) {
                return;
            }

            while (!isCommandComplete(crIndex)) {

                if (!isLastChar(crIndex) && nextCharIsLF(crIndex)) {
                    deleteNextChar(crIndex);
                }

                cmd = removeCRLF(getStringFromCharBuffer(crIndex));

                LOGGER.info("CommandBuffer.Parse() new command: \"" + cmd + "\"");

                synchronized (COMPLETE_COMMANDS_LOCK) {
                    this.completeCmds.add(cmd);
                }

                crIndex = findCarriageReturnPos();
            }
        }
    }

    private String getStringFromCharBuffer(int crIndex) {
        char[] chars;
        chars = new char[crIndex + 1];

        for (int i = 0; i <= crIndex; ++i) {
            chars[i] = this.charBuffer.remove(0);
        }
        return new String(chars);
    }

    private int findCarriageReturnPos() {
        int crIndex;
        crIndex = this.charBuffer.indexOf(Constants.CR);
        return crIndex;
    }

    private String removeCRLF(String cmd) {
        cmd = cmd.replace(String.valueOf(Constants.CR), "");
        cmd = cmd.replace(String.valueOf(Constants.LF), "");
        return cmd;
    }

    private void deleteNextChar(int crIndex) {
        this.charBuffer.remove(crIndex + 1);
    }

    private boolean nextCharIsLF(int crIndex) {
        return this.charBuffer.get(crIndex + 1) == Constants.LF;
    }

    private boolean isLastChar(int crIndex) {
        return crIndex == (this.charBuffer.size());
    }

    private boolean isCommandComplete(int crIndex) {
        return crIndex == -1;
    }

    public int cmdCount() {
        synchronized (COMPLETE_COMMANDS_LOCK) {
            return this.completeCmds.size();
        }
    }

    public boolean hasCompleteCommand() {
        return (this.cmdCount() > 0);
    }

    public String getNextCommand() {
        if (this.hasCompleteCommand()) {
            synchronized (COMPLETE_COMMANDS_LOCK) {
                return this.completeCmds.remove(0);
            }
        }
        return "";
    }

    public String toString() {
        String out = "CharacterBuffer is: " + this.charBuffer.size() + " characters long.\n Completed Command Queue:\n";
        for (String s : this.completeCmds) {
            out += "\t-" + s.replace("\r", "\\r").replace("\n", "\\n") + "\n";
        }

        return out;
    }
}
