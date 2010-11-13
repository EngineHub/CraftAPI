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

import com.sk89q.craftapi.event.*;

/**
 * Event listener for Hey0's server mod.
 *
 * @author sk89q
 */
public class CraftAPIListener extends PluginListener {
    /**
     * Event dispatcher.
     */
    private EventDispatcher eventDispatcher;

    /**
     * Construct the object.
     * 
     * @param eventDispatcher
     */
    public CraftAPIListener(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Called on chat.
     * 
     * @param player
     * @param message
     * @return
     */
    @Override
    public boolean onChat(Player player, String message) {
        eventDispatcher.dispatch(new ChatEvent(player, message));
        return false;
    }

    /**
     * Called on login.
     * 
     * @param player
     */
    @Override
    public void onLogin(Player player) {
        eventDispatcher.dispatch(new PlayerConnectionEvent(player, true));
    }

    /**
     * Called on disconnect.
     *
     * @param player
     */
    @Override
    public void onDisconnect(Player player) {
        eventDispatcher.dispatch(new PlayerConnectionEvent(player, false));
    }
}