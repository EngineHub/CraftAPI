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

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.webserver.RequestData;
import com.sk89q.craftapi.auth.*;

/**
 * Authentication handler for the XML RPC server.
 *
 * @author sk89q
 */
public class BasicAuthenticationHandler
        implements AbstractReflectiveHandlerMapping.AuthenticationHandler {
    /**
     * Stores a collection of username and password pairs for checking.
     * Passwords are stored as plain-text.
     */
    private AuthenticationProvider auth;

    /**
     * Creates a new instance.
     * 
     * @param logins
     */
    public BasicAuthenticationHandler(AuthenticationProvider auth) {
        this.auth = auth;
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
            return auth.verifyCredentials(data.getBasicUserName(),
                                          data.getBasicPassword());
        }

        return false;
    }

}
