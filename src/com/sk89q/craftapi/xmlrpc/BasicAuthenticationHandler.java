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

import java.util.Map;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.webserver.RequestData;

/**
 * Authentication handler for the XML RPC server. This handler merely checks
 * to see whether a username and password pair is found in a provided
 * Map. Username and password comparisons are case-sensitive and passwords
 * are stored in plain-text.
 *
 * @author sk89q
 */
public class BasicAuthenticationHandler
        implements AbstractReflectiveHandlerMapping.AuthenticationHandler {
    /**
     * Stores a collection of username and password pairs for checking.
     * Passwords are stored as plain-text.
     */
    private Map<String,String> logins;

    /**
     * Creates a new instance with a list of acceptable username and password
     * pairs. Passwords are stored as plain-text and all comparisons are done
     * with case-sensitivity.
     * 
     * @param logins
     */
    public BasicAuthenticationHandler(Map<String,String> logins) {
        this.logins = logins;
    }

    /**
     * Checks the authorization of a request based on the provided username
     * and password pair. The check is case-sensitive.
     * 
     * @param request
     * @return
     */
    public boolean isAuthorized(XmlRpcRequest request) {
        if (request.getConfig() instanceof RequestData) {
            RequestData data = (RequestData)request.getConfig();

            String username = data.getBasicUserName();

            if (logins.containsKey(username)) {
                return data.getBasicPassword().equals(logins.get(username));
            }
        }

        return false;
    }

}
