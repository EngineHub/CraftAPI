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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * Indicates that the server should be running.
     */
    private boolean running = true;
    /**
     * Thread pool.
     */
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Construct the object.
     * 
     * @param port
     * @param maxConnections
     */
    public StreamingServer(int port, int maxConnections) {
        this.port = port;
        this.max = maxConnections;
    }

    /**
     * Run the server.
     */
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);
            Socket server;

            while (num < max && running) {
                Socket sock = listener.accept();
                
                synchronized (this) {
                    try {
                        StreamingServerClient client = new StreamingServerClient(this, sock);
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
}
