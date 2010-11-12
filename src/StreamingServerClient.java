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
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

class StreamingServerClient implements Runnable {
    /**
     * Indicates state of the client.
     */
    private enum State {
        UNAUTHENTICATED,
        READY
    }

    private static Base64 base64 = new Base64(999);
    /**
     * Client socket.
     */
    
    private State state = State.UNAUTHENTICATED;
    /**
     * Used for generating challenges.
     */
    private SecureRandom random;
    /**
     * Base64 decoder/encoder;
     */
    private Socket socket;
    /**
     * Server instance.
     */
    private StreamingServer server;
    /**
     * Challenge to use to authenticate.
     */
    private byte[] challenge;

    private BufferedReader in;
    private PrintStream out;

    /**
     * Construct the instance.
     * 
     * @param server
     * @param socket
     */
    public StreamingServerClient(StreamingServer server, Socket socket)
            throws Throwable {
        this.server = server;
        this.socket = socket;

        random = SecureRandom.getInstance("SHA1PRNG");
        challenge = new byte[64];
        random.nextBytes(challenge);

        InputStreamReader inReader = new InputStreamReader(socket.getInputStream(), "utf-8");
        in = new BufferedReader(inReader);
        out = new PrintStream(socket.getOutputStream());
    }

    /**
     * Listen for messages.
     */
    public void run() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");
                handleUnauthenticated(parts);
            }
            
            socket.close();
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.lostClient();
        }
    }

    public void handleUnauthenticated(String[] parts) {
        if (parts[0].equals("get-challenge")) {
            out.println("challenge " + base64.encodeToString(challenge));
        }
    }
}
