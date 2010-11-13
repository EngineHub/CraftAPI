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

package com.sk89q.craftapi.streaming;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.*;
import javax.net.ssl.*;
import com.sk89q.craftapi.auth.*;

/**
 *
 * @author sk89q
 */
public class StreamingServer implements Runnable {
    /**
     * Port.
     */
    private int port;
    /**
     * Number of connections.
     */
    private int num = 0;
    /**
     * Maximum number of connections.
     */
    private int max;
    /**
     * Address to listen to.
     */
    private InetAddress listenInterface;
    /**
     * Whether to use SSL;
     */
    private boolean useSSL;
    /**
     * Indicates that the server should be running.
     */
    private boolean running = true;
    /**
     * Authentication provider.
     */
    private AuthenticationProvider auth;
    /**
     * Thread pool.
     */
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    /**
     * Client class.
     */
    private StreamingServerFactory factory;

    /**
     * Construct the object.
     * 
     * @param port
     * @param maxConnections
     * @param auth
     */
    public StreamingServer(int port, int maxConnections,
            InetAddress listenInterface, boolean useSSL,
            AuthenticationProvider auth, StreamingServerFactory factory) {
        this.port = port;
        this.max = maxConnections;
        this.auth = auth;
        this.factory = factory;
        this.listenInterface = listenInterface;
        this.useSSL = useSSL;
    }

    /**
     * Run the server.
     */
    public void run() {
        try {
            ServerSocketFactory socketFactory;
            ServerSocket server;

            if (useSSL) {
                socketFactory = SSLServerSocketFactory.getDefault();
            } else {
                socketFactory = ServerSocketFactory.getDefault();
            }

            if (listenInterface != null) {
                server = socketFactory.createServerSocket(port, 5, listenInterface);
            } else {
                server = socketFactory.createServerSocket(port);
            }

            while (num < max && running) {
                Socket sock = server.accept();
                
                synchronized (this) {
                    try {
                        StreamingServerClient client =
                                factory.createClient(this, sock);
                        (new Thread(client)).start();
                        num++;
                    } catch (Throwable t) {
                        sock.close();
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Decrements the client counter.
     */
    public synchronized void lostClient() {
        num--;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        this.running = false;
    }

    /**
     * Get the authentication provider.
     *
     * @return
     */
    public AuthenticationProvider getAuthenticationProvider() {
        return auth;
    }
}
