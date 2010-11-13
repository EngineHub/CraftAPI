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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.*;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import com.sk89q.craftapi.*;
import com.sk89q.craftapi.auth.*;

/**
 * Entry point for the plugin for hey0's mod.
 *
 * @author sk89q
 */
public class CraftAPI extends Plugin {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft.CraftAPI");
    /**
     * Stores a list of running servers.
     */
    private ArrayList<ServerInterface> servers = new ArrayList<ServerInterface>();

    /**
     * Initializes the plugin.
     */
    @Override
    public void initialize() {
    }

    /**
     * Enables the plugin.
     */
    @Override
    public void enable() {
        logger.log(Level.INFO, "CraftAPI is installed");

        try {
            XMLConfiguration config = new XMLConfiguration("craftapi.xml");
            HierarchicalConfiguration authConfig =
                config.configurationAt("authentication");

            // XML-RPC server
            if (config.getBoolean("xml-rpc.enabled", false)) {
                try {
                    // Get a custom authentication provider for the "XML-RPC"
                    // service
                    AuthenticationProvider auth =
                        new ConfigurationAuthentication(authConfig, "XML-RPC");
                    int port = config.getInt("xml-rpc.port", 20012);

                    // Build the map of APIs that will be made available
                    Map<String,Class> handlers = new HashMap<String,Class>();
                    handlers.put("server", XMLRPCServerAPI.class);
                    handlers.put("player", XMLRPCPlayerAPI.class);
                    handlers.put("minecraft", XMLRPCMinecraftAPI.class);

                    // Start!
                    startServer(new XMLRPCInterface(port, handlers, auth));
                } catch (NoClassDefFoundError e) {
                    logger.log(Level.SEVERE, "Missing libraries for XML-RPC support: "
                            + e.getMessage());
                }
            }

            // Streaming API server
            if (config.getBoolean("streaming-api.enabled", false)) {
                try {
                    // Get a custom authentication provider
                    AuthenticationProvider auth =
                        new ConfigurationAuthentication(authConfig, "StreamingAPI");
                    int port = config.getInt("streaming-api.port", 20013);
                    int maxConnections = config.getInt("streaming-api.max-connections", 10);
                    String bindAddressStr = config.getString("streaming-api.bind-address");
                    boolean useSSL = config.getBoolean("streaming-api.use-ssl", false);

                    // Get bind address
                    InetAddress bindAddress = null;
                    if (bindAddressStr != null) {
                        bindAddress = InetAddress.getByName(bindAddressStr);
                    }

                    // Start!
                    startServer(new StreamingAPIInterface(port, maxConnections,
                            bindAddress, useSSL, auth));
                } catch (UnknownHostException e) {
                    logger.log(Level.SEVERE, "Unknown bind address for the streaming API server: "
                            + e.getMessage());
                } catch (NoClassDefFoundError e) {
                    logger.log(Level.SEVERE, "Missing libraries for streaming API support: "
                            + e.getMessage());
                }
            }
        } catch (ConfigurationException e) {
            logger.log(Level.SEVERE, "Failed to load CraftAPI configuration: "
                    + e.getMessage());
        }
    }

    /**
     * Disables the plugin.
     */
    @Override
    public void disable() {
        logger.log(Level.INFO, "CraftAPI is closing servers");

        stopAllServers();
    }

    /**
     * Start a server.
     *
     * @param server
     */
    public void startServer(ServerInterface server) {
        servers.add(server);
        server.start();
    }

    /**
     * Stop all servers.
     */
    public void stopAllServers() {
        for (ServerInterface server : servers) {
            server.shutdown();
        }
        servers.clear();
    }
}
