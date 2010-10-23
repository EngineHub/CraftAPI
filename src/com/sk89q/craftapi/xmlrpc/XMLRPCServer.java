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

package com.sk89q.craftapi.xmlrpc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.server.XmlRpcErrorLogger;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;
import org.apache.xmlrpc.webserver.WebServer;
import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;

/**
 * Threaded XML-RPC server.
 *
 * @author sk89q
 */
public class XMLRPCServer implements Runnable {
    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger("Minecraft");
    /**
     * XML-RPC web server.
     */
    private WebServer webServer;

    /**
     * Create a new instance of the server with an authentication handler
     * and a Class to use for API requests.
     *
     * @param port
     * @param authHandler
     * @param apiClass
     * @throws XmlRpcException
     */
    public XMLRPCServer(int port, AuthenticationHandler authHandler,
            Map<String,Class> handlers)
            throws XmlRpcException {
        webServer = new WebServer(port) {
            @Override
            protected XmlRpcStreamServer newXmlRpcStreamServer() {
                XmlRpcStreamServer streamServer = super.newXmlRpcStreamServer();

                // Reduce the error spam
                streamServer.setErrorLogger(new XmlRpcErrorLogger() {
                    @Override
                    public void log(String msg, Throwable throwable) {
                        if (throwable instanceof APIException) {
                        } else {
                            logger.log(Level.WARNING, "XML-RPC server error: "
                                    + throwable.getMessage());
                            //super.log(msg, throwable);
                        }
                    }
                });
                
                return streamServer;
            }
        };
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.setAuthenticationHandler(authHandler);
        for (Map.Entry<String,Class> entry : handlers.entrySet()) {
            phm.addHandler(entry.getKey(), entry.getValue());
        }
        XmlRpcSystemImpl.addSystemHandler(phm);
        xmlRpcServer.setHandlerMapping(phm);

        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl)xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(false);
        serverConfig.setContentLengthOptional(false);
    }

    /**
     * Run the server.
     */
    public void run() {
        try {
            logger.log(Level.INFO, "Starting XML-RPC server");
            webServer.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start XML-RPC server", e);
        }
    }

    /**
     * Shutdown the server.
     */
    public void shutdown() {
        logger.log(Level.INFO, "Shutting down XML-RPC server");
        webServer.shutdown();
    }
}
