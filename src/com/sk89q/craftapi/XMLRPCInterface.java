/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
