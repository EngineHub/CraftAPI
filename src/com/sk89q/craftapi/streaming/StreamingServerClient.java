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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author sk89q
 */
public class StreamingServerClient implements Runnable {
    protected static final int ERROR_UNKNOWN_PACKET = 1;
    protected static final int ERROR_BAD_PACKET = 2;
    protected static final int ERROR_INTERNAL = 3;
    protected static final int ERROR_AUTHENTICATION = 3;

    /**
     * Indicates state of the client.
     */
    protected enum State {
        UNAUTHENTICATED,
        READY
    }

    /**
     * Base64 encoder.
     */
    protected static final Base64 base64 = new Base64(999);
    /**
     * Client state.
     */
    protected State state = State.UNAUTHENTICATED;
    /**
     * Used for generating challenges.
     */
    protected SecureRandom random;
    /**
     * Base64 decoder/encoder;
     */
    protected Socket socket;
    /**
     * Server instance.
     */
    protected StreamingServer server;
    /**
     * Challenge to use to authenticate.
     */
    private byte[] challenge;

    /**
     * Reader input stream.
     */
    protected BufferedReader in;
    /**
     * Reader output stream.
     */
    protected PrintStream out;

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
        out = new PrintStream(socket.getOutputStream(), true, "utf-8");
    }

    /**
     * Listen for messages.
     */
    public void run() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");
                try {
                    if (state == State.UNAUTHENTICATED) {
                        handleUnauthenticated(parts);
                    } else {
                        handle(parts);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendError(ERROR_BAD_PACKET);
                } catch (UnsupportedEncodingException e) {
                    sendError(ERROR_BAD_PACKET);
                } catch (NumberFormatException e) {
                    sendError(ERROR_BAD_PACKET);
                }
            }
            
            socket.close();
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.lostClient();
        }
    }

    /**
     * Handle authenticated packets.
     *
     * @param parts
     */
    public void handle(String[] parts) throws UnsupportedEncodingException {
        sendError(ERROR_UNKNOWN_PACKET);
    }

    /**
     * Handle unauthenticated packets.
     * 
     * @param parts
     */
    public void handleUnauthenticated(String[] parts) throws UnsupportedEncodingException {
        // getChallenge
        if (parts[0].equals("getChallenge")) {
            send("challenge", base64.encodeToString(challenge));

        // challengeLogin
        } else if (parts[0].equals("challengeLogin")) {
            try {
                SecretKey key = new SecretKeySpec(challenge, "HMACSHA256");
                Mac mac = Mac.getInstance("HMACSHA256");
                mac.init(key);
                byte[] digest = base64.decode(decode(parts[2]).getBytes());
                if (server.getAuthenticationProvider()
                        .verifyCredentials(mac, parts[1], digest)) {
                    send("authOK");
                    state = State.READY;
                } else {
                    sendError(ERROR_AUTHENTICATION);
                }
            } catch (NoSuchAlgorithmException e) {
                sendError(ERROR_INTERNAL, e.getMessage());
            } catch (InvalidKeyException e) {
                sendError(ERROR_INTERNAL, e.getMessage());
            }

        // Unknown
        } else {
            sendError(ERROR_UNKNOWN_PACKET);
        }
    }

    /**
     * Send a message to the client.
     * 
     * @param args
     */
    protected void send(Object ... args) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof String) {
                try {
                    builder.append(URLEncoder.encode((String)arg, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    builder.append("UnsupportedEncodingException");
                }
            } else {
                builder.append(arg);
            }

            if (i != args.length - 1) {
                builder.append(" ");
            }
        }

        out.println(builder.toString());
    }

    /**
     * Send an error to the client.
     *
     * @param args
     */
    protected void sendError(int error) {
        send("err", error);
    }

    /**
     * Send an error to the client.
     *
     * @param args
     */
    protected void sendError(int error, Object ... args) {
        send("err", error, args);
    }

    /**
     * Decodes a string.
     * 
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String decode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "utf-8");
    }
}
