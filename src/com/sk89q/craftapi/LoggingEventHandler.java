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

import java.util.logging.*;
import com.sk89q.craftapi.event.*;

/**
 *
 * @author sk89q
 */
public class LoggingEventHandler extends Handler {
    /**
     * Event dispatcher.
     */
    private EventDispatcher eventDispatcher;

    /**
     * Construct the object.
     * 
     * @param eventDispatcher
     * @param formatter
     */
    public LoggingEventHandler(EventDispatcher eventDispatcher, Formatter formatter) {
        this.eventDispatcher = eventDispatcher;
        setFormatter(formatter);
    }

    /**
     * Publish the record.
     * 
     * @param record
     */
    public void publish(LogRecord record) {
        eventDispatcher.dispatch(new StdOutEvent(getFormatter().format(record)));
    }

    /**
     * Flush the stream.
     */
    public void flush() {

    }

    /**
     * Close the handler.
     */
    public void close() {
    }
}
