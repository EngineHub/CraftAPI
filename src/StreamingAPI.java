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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sk89q.craftapi.*;

public class StreamingAPI implements ServerInterface {
    /**
     * Logger.
     */
    private static final Logger logger =
            Logger.getLogger("Minecraft.CraftAPI.StreamingAPI");
    /**
     * Port.
     */
    private int port;
    /**
     * Max connections.
     */
    private int maxConnections;
    /**
     * Streaming server.
     */
    private StreamingServer server;

    /**
     * Construct an instance.
     *
     * @param port
     * @param maxConnections
     */
    public StreamingAPI(int port, int maxConnections) {
        this.port = port;
        this.maxConnections = maxConnections;
    }

    /**
     * Start the server.
     */
    public void start() {
        server = new StreamingServer(port, maxConnections);
        Thread thread = new Thread(server);
        thread.start();
    }

    /**
     * Shutdown the server.
     */
    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }
}