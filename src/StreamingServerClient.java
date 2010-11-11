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
 * along with this p
rogram. If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.*;
import java.net.*;

class StreamingServerClient implements Runnable {
    /**
     * Client socket.
     */
    private Socket socket;
    /**
     * Server instance.
     */
    private StreamingServer server;

    /**
     * Construct the instance.
     * 
     * @param server
     * @param socket
     */
    public StreamingServerClient(StreamingServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    /**
     * Listen for messages.
     */
    public void run() {
        try {
            String line;
            InputStreamReader inReader = new InputStreamReader(socket.getInputStream(), "utf-8");
            BufferedReader in = new BufferedReader(inReader);
            PrintStream out = new PrintStream(socket.getOutputStream());

            while ((line = in.readLine()) != null) {
            }
            
            socket.close();
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.lostClient();
        }
    }
}
