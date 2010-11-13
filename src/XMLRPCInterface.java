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
import org.apache.xmlrpc.XmlRpcException;
import com.sk89q.craftapi.xmlrpc.BasicAuthenticationHandler;
import com.sk89q.craftapi.xmlrpc.XMLRPCServer;
import com.sk89q.craftapi.*;
import com.sk89q.craftapi.auth.*;

/**
 * XML-RPC server interface.
 *
 * @author sk89q
 */
public class XMLRPCInterface implements ServerInterface {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft.CraftAPI.XML-RPC");
    /**
     * XML-RPC server.
     */
    private XMLRPCServer xmlRPCServer;

    /**
     * Construct an instance.
     * 
     * @param port
     * @param serverAPI
     * @param logiauthns
     */
    public XMLRPCInterface(int port, Map<String,Class> handlers,
            AuthenticationProvider authProvider) {
        BasicAuthenticationHandler auth =
                new BasicAuthenticationHandler(authProvider);

        try {
            xmlRPCServer = new XMLRPCServer(port, auth, handlers);
        } catch (XmlRpcException e) {
            logger.log(Level.SEVERE, "Failed to init the XML-RPC server", e);
        }
    }

    /**
     * Start the server.
     */
    public void start() {
        logger.log(Level.INFO, "Starting XML-RPC server");
        if (xmlRPCServer != null) {
            Thread thread = new Thread(xmlRPCServer);
            thread.start();
        }
    }

    /**
     * Shutdown the server.
     */
    public void shutdown() {
        logger.log(Level.INFO, "Shutting down XML-RPC server");
        if (xmlRPCServer != null) {
            xmlRPCServer.shutdown();
        }
    }
}
