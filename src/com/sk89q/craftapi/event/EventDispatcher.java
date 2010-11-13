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

package com.sk89q.craftapi.event;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple event dispatcher that identifies events by their class. The downside
 * to this approach is that it reduces the usefulness of sub-classing, but
 * the upside is that it doesn't result in a large number of anonymous
 * classes and classes for each event type.
 *
 * @author sk89q
 */
public class EventDispatcher {
    /**
     * List of listeners.
     */
    private Map<Class<? extends Event>,Set<EventListener>> registered =
            new HashMap<Class<? extends Event>,Set<EventListener>>();

    /**
     * Register an event listener.
     * 
     * @param eventClass
     * @param listener
     */
    public synchronized void register(Class<? extends Event> eventClass,
            EventListener listener) {
        Set<EventListener> listeners;

        if (registered.containsKey(eventClass)) {
            listeners = registered.get(eventClass);
        } else {
            listeners = new LinkedHashSet<EventListener>();
            registered.put(eventClass, listeners);
        }

        listeners.add(listener);
    }

    /**
     * Unregister an event listener.
     *
     * @param eventClass
     * @param listener
     */
    public synchronized void unregister(Class<? extends Event> eventClass,
            EventListener listener) {
        if (registered.containsKey(eventClass)) {
            Set<EventListener> listeners = registered.get(eventClass);
            listeners.remove(listener);
            if (listeners.size() == 0) {
                registered.remove(eventClass);
            }
        }
    }

    /**
     * Unregister an event listener for all events.
     *
     * @param listener
     */
    public synchronized void unregisterAll(EventListener listener) {
        for (Map.Entry<Class<? extends Event>,Set<EventListener>> entry
                : registered.entrySet()) {
            Set<EventListener> listeners = entry.getValue();
            listeners.remove(listener);
            if (listeners.size() == 0) {
                registered.remove(entry.getKey());
            }
        }
    }

    /**
     * Dispatch an event.
     * 
     * @param event
     */
    public synchronized void dispatch(Event event) {
        Class<? extends Event> eventClass = event.getClass();
        
        if (registered.containsKey(eventClass)) {
            Set<EventListener> listeners = registered.get(eventClass);
            for (EventListener listener : listeners) {
                try {
                    listener.handleEvent(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the number of listeners for a particular event.
     *
     * @param eventClass
     */
    public synchronized int getListenerCount(Class<? extends Event> eventClass) {
        if (registered.containsKey(eventClass)) {
            return registered.get(eventClass).size();
        }

        return 0;
    }

    /**
     * Unregister all listeners.
     */
    public synchronized void unregisterAll() {
        registered = new HashMap<Class<? extends Event>,Set<EventListener>>();
    }
}
