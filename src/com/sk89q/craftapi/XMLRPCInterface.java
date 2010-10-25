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

import com.sk89q.craftapi.xmlrpc.BasicAuthenticationHandler;
import com.sk89q.craftapi.xmlrpc.XMLRPCServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;

/**
 * XML-RPC server interface.
 *
 * @author sk89q
 */
public class XMLRPCInterface implements ServerInterface {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft");
    /**
     * XML-RPC server.
     */
    private XMLRPCServer xmlRPCServer;

    /**
     * Construct an instance.
     * 
     * @param port
     * @param serverAPI
     * @param logins
     */
    public XMLRPCInterface(int port, Map<String,Class> handlers,
            Map<String,String> logins) {
        BasicAuthenticationHandler auth = new BasicAuthenticationHandler(logins);

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
        if (xmlRPCServer != null) {
            Thread thread = new Thread(xmlRPCServer);
            thread.start();
        }
    }

    /**
     * Shutdown the server.
     */
    public void shutdown() {
        if (xmlRPCServer != null) {
            xmlRPCServer.shutdown();
        }
    }
}
