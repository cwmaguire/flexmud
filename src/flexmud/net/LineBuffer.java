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
package flexmud.net;

import flexmud.cfg.Constants;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class LineBuffer {
    private static final Logger LOGGER = Logger.getLogger(LineBuffer.class);
    private static final Object CHAR_BUFFER_LOCK = new Object();
    private static final Object COMPLETE_LINES_LOCK = new Object();

    private ArrayList<Character> charBuffer;
    private ArrayList<String> completeLines;

    /**
     * Default constructor.
     */
    public LineBuffer() {
        this.completeLines = new ArrayList<String>();
        this.charBuffer = new ArrayList<Character>();
    }

    public void write(byte[] data) throws IOException {
        this.writeToCharBuffer(data, data.length);
    }

    public void writeToCharBuffer(byte[] bytes, int length) throws IOException {

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

                writeToCharBuffer(eightBitCharBuffer.array(), eightBitCharBuffer.limit());
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

    public void storeCompleteLines() {
        int carriageReturnIndex;

        synchronized (CHAR_BUFFER_LOCK) {
            carriageReturnIndex = findNextCarriageReturnPos();

            if (hasNoCarriageReturns(carriageReturnIndex)) {
                return;
            }

            while (hasMoreCarriageReturns(carriageReturnIndex)) {

                deleteLeftoverLineFeed(carriageReturnIndex);

                synchronized (COMPLETE_LINES_LOCK) {
                    this.completeLines.add(removeCompleteLine(carriageReturnIndex));
                }
                carriageReturnIndex = findNextCarriageReturnPos();
            }
        }
    }

    private String removeCompleteLine(int carriageReturnIndex) {
        String line;


        line = removeCompleteLineFromCharBuffer(carriageReturnIndex);
        line = trimCRLF(line);

        LOGGER.info("LineBuffer.Parse() complete line: \"" + line + "\"");

        return line;
    }

    private void deleteLeftoverLineFeed(int carriageReturnIndex) {
        if (!isLastChar(carriageReturnIndex) && nextCharIsLineFeed(carriageReturnIndex)) {
            deleteNextChar(carriageReturnIndex);
        }
    }

    private String removeCompleteLineFromCharBuffer(int crIndex) {
        char[] chars = new char[crIndex + 1];

        for (int i = 0; i <= crIndex; ++i) {
            chars[i] = this.charBuffer.remove(0);
        }
        return new String(chars);
    }

    private int findNextCarriageReturnPos() {
        int crIndex;
        crIndex = this.charBuffer.indexOf(Constants.CR);
        return crIndex;
    }

    private String trimCRLF(String cmd) {
        cmd = cmd.replace(String.valueOf(Constants.CR), "");
        //cmd = cmd.replace(String.valueOf(Constants.LF), "");
        return cmd;
    }

    private void deleteNextChar(int crIndex) {
        this.charBuffer.remove(crIndex + 1);
    }

    private boolean nextCharIsLineFeed(int crIndex) {
        return this.charBuffer.get(crIndex + 1) == Constants.LF;
    }

    private boolean isLastChar(int crIndex) {
        return crIndex == (this.charBuffer.size());
    }

    private boolean hasMoreCarriageReturns(int crIndex) {
        return crIndex != -1;
    }

    private boolean hasNoCarriageReturns(int crIndex) {
        return crIndex == -1;
    }

    public int cmdCount() {
        synchronized (COMPLETE_LINES_LOCK) {
            return this.completeLines.size();
        }
    }

    public boolean hasCompleteCommand() {
        return (this.cmdCount() > 0);
    }

    public String getNextCommand() {
        if (this.hasCompleteCommand()) {
            synchronized (COMPLETE_LINES_LOCK) {
                return this.completeLines.remove(0);
            }
        }
        return "";
    }

    public String toString() {
        String out = "CharacterBuffer is: " + this.charBuffer.size() + " characters long.\n Completed Command Queue:\n";
        for (String s : this.completeLines) {
            out += "\t-" + s.replace("\r", "\\r").replace("\n", "\\n") + "\n";
        }

        return out;
    }
}
