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

import java.io.*;
import java.net.*;
import java.util.List;
import com.sk89q.craftapi.*;
import com.sk89q.craftapi.streaming.*;
import com.sk89q.craftapi.event.*;

/**
 *
 * @author sk89q
 */
public class StreamingAPIServerClient extends StreamingServerClient
        implements EventListener {
    private final static int UNKNOWN_STREAM = 100;
    
    /**
     * Event dispatcher.
     */
    private EventDispatcher eventDispatcher;
    
    /**
     * Construct the instance.
     *
     * @param server
     * @param socket
     * @param eventDispatcher
     */
    public StreamingAPIServerClient(StreamingServer server, Socket socket,
            EventDispatcher eventDispatcher)
            throws Throwable {
        super(server, socket);
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Handle an event.
     * 
     * @param event
     */
    public synchronized void handleEvent(Event event) {
        if (event instanceof ChatEvent) {
            ChatEvent evt = (ChatEvent)event;
            send("MSG", evt.getPlayer().getName(), evt.getMessage());
        } else if (event instanceof StdOutEvent) {
            StdOutEvent evt = (StdOutEvent)event;
            send("SOUT", evt.getMessage());
        } else if (event instanceof PlayerConnectionEvent) {
            PlayerConnectionEvent evt = (PlayerConnectionEvent)event;
            if (evt.connected()) {
                send("PCONN", evt.getPlayer().getName());
            } else {
                send("PDISC", evt.getPlayer().getName());
            }
        }
    }

    /**
     * Handle authenticated packets.
     *
     * @param parts
     */
    public void handle(String[] parts) throws UnsupportedEncodingException {
        String command = decode(parts[0]);
        
        // SUB / UNSUB
        if (command.equals("SUB") || command.equals("UNSUB")) {
            boolean subscribe = command.equals("SUB");

            for (int i = 2; i < parts.length; i++) {
                String name = parts[i];

                Class<? extends Event> eventClass = getEventClass(name);

                if (eventClass != null) {
                    if (subscribe) {
                        eventDispatcher.register(eventClass, this);
                    } else {
                        eventDispatcher.unregister(eventClass, this);
                    }
                } else {
                    sendError(UNKNOWN_STREAM, name);
                }
            }

        // BCAST
        } else if (command.equals("BCAST")) {
            String message = decode(parts[2]);
            for (Player player : etc.getServer().getPlayerList()) {
                player.sendMessage(message);
            }

        // NAMES
        } else if (command.equals("NAMES")) {
            List<Player> players = etc.getServer().getPlayerList();
            
            String[] reply = new String[players.size() + 1];
            reply[0] = "NAMES";

            int i = 1;
            for (Player player : etc.getServer().getPlayerList()) {
                reply[i++] = player.getName();
            }

            send((Object[])reply);

        // EXEC
        } else if (command.equals("EXEC")) {
            etc.getServer().useConsoleCommand(decode(parts[2]));

        // PING
        } else if (command.equals("PING")) {
            send("PONG");
        
        // Unknown
        } else {
            sendError(ERROR_UNKNOWN_PACKET, command);
        }
    }

    /**
     * Called on close.
     */
    @Override
    protected void handleClose() {
        super.handleClose();
        eventDispatcher.unregisterAll(this);
    }

    /**
     * Gets an event class from name.
     * 
     * @param name
     * @return
     */
    protected Class<? extends Event> getEventClass(String name) {
        if (name.equals("CHAT")) {
            return ChatEvent.class;
        } else if (name.equals("PLYCONN")) {
            return PlayerConnectionEvent.class;
        } else if (name.equals("STDOUT")) {
            return StdOutEvent.class;
        } else {
            return null;
        }
    }
}
