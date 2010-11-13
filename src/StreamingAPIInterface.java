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

import com.sk89q.craftapi.streaming.StreamingServer;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sk89q.craftapi.*;
import com.sk89q.craftapi.streaming.*;
import com.sk89q.craftapi.auth.*;
import com.sk89q.craftapi.event.*;

/**
 * Streaming API server interface.
 * 
 * @author Albert
 */
public class StreamingAPIInterface implements ServerInterface {
    /**
     * Logger.
     */
    private static final Logger logger =
            Logger.getLogger("Minecraft.CraftAPI.Streaming");
    /**
     * Port.
     */
    private int port;
    /**
     * Max connections.
     */
    private int maxConnections;
    /**
     * Address to listen to.
     */
    private InetAddress listenInterface;
    /**
     * Whether to use SSL;
     */
    private boolean useSSL;
    /**
     * Authentication provider.
     */
    private AuthenticationProvider authProvider;
    /**
     * Streaming server.
     */
    private StreamingServer server;
    /**
     * Event dispatcher.
     */
    private EventDispatcher eventDispatcher;

    /**
     * Construct an instance.
     *
     * @param port
     * @param maxConnections
     */
    public StreamingAPIInterface(int port, int maxConnections,
            InetAddress listenInterface, boolean useSSL,
            AuthenticationProvider authProvider,
            EventDispatcher eventDispatcher) {
        this.port = port;
        this.maxConnections = maxConnections;
        this.listenInterface = listenInterface;
        this.useSSL = useSSL;
        this.authProvider = authProvider;
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Start the server.
     */
    public void start() {
        logger.log(Level.INFO, "Starting streaming API server");
        server = new StreamingServer(port, maxConnections,
                listenInterface, useSSL, authProvider,
                new StreamingAPIServerFactory(eventDispatcher));
        Thread thread = new Thread(server);
        thread.start();
    }

    /**
     * Shutdown the server.
     */
    public void shutdown() {
        logger.log(Level.INFO, "Shutting down streaming API server");
        if (server != null) {
            server.shutdown();
        }
    }
}