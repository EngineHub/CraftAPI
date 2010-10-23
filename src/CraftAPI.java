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

import com.sk89q.craftapi.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

/**
 * Entry point for the plugin for hey0's mod.
 *
 * @author sk89q
 */
public class CraftAPI extends Plugin {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft");
    /**
     * CraftAPI settings.
     */
    private PropertiesFile properties = new PropertiesFile("craftapi.properties");
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
        properties.load();

        logger.log(Level.INFO, "CraftAPI is installed");

        // XML-RPC server
        if (properties.getBoolean("xml-rpc-enable", true)) {
            try {
                Map<String,String> logins = readUserPassPairs("xml-rpc-logins.txt");
                int port = properties.getInt("xml-rpc-port", 20012);
                Map<String,Class> handlers = new HashMap<String,Class>();
                handlers.put("server", XMLRPCServerAPI.class);
                handlers.put("player", XMLRPCPlayerAPI.class);
                handlers.put("minecraft", XMLRPCMinecraftAPI.class);
                startServer(new XMLRPCInterface(port, handlers, logins));
            } catch (FileNotFoundException fe) {
                logger.log(Level.SEVERE, "xml-rpc-logins.txt cannot be found: "
                        + fe.getMessage());
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Failed to read xml-rpc-logins.txt: "
                        + ioe.getMessage());
            } catch (NoClassDefFoundError e) {
                logger.log(Level.SEVERE, "Missing libraries for XML-RPC support: "
                        + e.getMessage());
            }
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
     * Read a file containing username/password pairs.
     * 
     * @param file
     * @return
     * @throws IOException
     */
    private static Map<String,String> readUserPassPairs(String path)
            throws IOException {
        File file = new File(path);
        FileReader input = null;
        Map<String,String> logins = new HashMap<String,String>();
        
        try {
            input = new FileReader(file);
            BufferedReader buff = new BufferedReader(input);

            String line;
            while ((line = buff.readLine()) != null) {
                line = line.trim();
                
                // Comment
                if (line.charAt(0) == ';' || line.equals("")) {
                    continue;
                }
                
                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    logger.log(Level.WARNING, "Found entry with no password in "
                            + file.getName() + " for '" + line + "'");
                } else {
                    logins.put(parts[0], parts[1]);
                }
            }

            return logins;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e2) {
            }
        }
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
