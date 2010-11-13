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

package com.sk89q.craftapi.auth;

import java.util.List;
import java.util.Arrays;
import javax.crypto.Mac;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Provides authentication from hierarchical configuration files. This works
 * with the Apache Commons configuration classes.
 *
 * @author sk89q
 */
public class ConfigurationAuthentication implements AuthenticationProvider {
    /**
     * Configuration file to use.
     */
    private HierarchicalConfiguration config;
    /**
     * Services to check against.
     */
    private String service;

    /**
     * Construct the object.
     *
     * @param config
     */
    public ConfigurationAuthentication(HierarchicalConfiguration config) {
        this.config = config;
        service = null;
    }

    /**
     * Construct the object.
     *
     * @param config
     */
    public ConfigurationAuthentication(HierarchicalConfiguration config,
            String service) {
        this.config = config;
        this.service = service;
    }

    /**
     * Verify username and password pairs.
     *
     * @param username
     * @param password
     * @return
     */
    public boolean verifyCredentials(String username, String password) {
        List credentials =
                config.configurationsAt("credential");
        for (Object c : credentials) {
            HierarchicalConfiguration credential = (HierarchicalConfiguration)c;
            String user = credential.getString("username");
            String pass = credential.getString("password");
            if (user != null && pass != null
                    && user.equals(username) && pass.equals(password)
                    && implementsService(credential)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Verify username and password pairs using a HMAC digest.
     *
     * @param username
     * @param password
     * @return
     */
    public boolean verifyCredentials(Mac mac, String username, byte[] digest) {
        List credentials =
                config.configurationsAt("credential");
        for (Object c : credentials) {
            HierarchicalConfiguration credential = (HierarchicalConfiguration)c;
            String user = credential.getString("username");
            String pass = credential.getString("password");
            if (user != null && pass != null && user.equals(username)
                    && implementsService(credential)) {
                byte[] testDigest = mac.doFinal(pass.getBytes());

                if (Arrays.equals(testDigest, digest)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if a credential has access to the desired service.
     *
     * @param credential
     * @return
     */
    private boolean implementsService(HierarchicalConfiguration credential) {
        if (service == null) {
            return true;
        }

        for (Object serv : credential.getList("service")) {
            if (serv instanceof String) {
                if (service.equals(serv)) {
                    return true;
                }
            }
        }

        return false;
    }
}
