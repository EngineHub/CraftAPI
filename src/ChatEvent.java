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
 *
 * @author sk89q
 */
public class ChatEvent extends Event {
    /**
     * Player.
     */
    private Player player;
    /**
     * Message.
     */
    private String message;

    /**
     * Construct the object.
     * 
     * @param player
     * @param message
     */
    public ChatEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
