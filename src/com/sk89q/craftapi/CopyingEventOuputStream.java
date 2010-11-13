// $Id$
/*
 * CraftAPI
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.craftapi;

import java.lang.reflect.*;
import java.io.*;
import com.sk89q.craftapi.event.*;

/**
 * Used to replace a PrintStream. Sends messages to an event dispatcher.
 * 
 * @author Albert
 */
public class CopyingEventOuputStream extends ByteArrayOutputStream {
    /**
     * Line separator.
     */
    private String lineSeparator;
    /**
     * Old output stream.
     */
    private OutputStream oldOut;
    /**
     * Event dispatcher.
     */
    private EventDispatcher eventDispatcher;

    /**
     * Construct the object.
     * 
     * @param existingStream
     */
    public CopyingEventOuputStream(PrintStream existingStream,
            EventDispatcher eventDispatcher) throws Throwable {
        super();
        Field outField = FilterOutputStream.class.getDeclaredField("out");
        outField.setAccessible(true);
        oldOut = (OutputStream)outField.get(existingStream);
        lineSeparator = System.getProperty("line.separator");
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Flush.
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String message = this.toString();
            oldOut.write(buf);
            oldOut.flush();
            super.reset();

            if (message.length() == 0 || message.equals(lineSeparator)) {
                return;
            }
            
            eventDispatcher.dispatch(new StdOutEvent(message.trim()));
        }
    }
} 