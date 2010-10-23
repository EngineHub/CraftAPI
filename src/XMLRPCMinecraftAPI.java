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

/**
 *
 * @author sk89q
 */
public class XMLRPCMinecraftAPI {
    /**
     * Gets the block at a location.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getBlockID(int x, int y, int z) {
        return etc.getServer().getBlockIdAt(x, y, z);
    }

    /**
     * Gets the block at a location.
     *
     * @param x
     * @param y
     * @param z
     * @param id
     * @return
     */
    public boolean setBlockID(int x, int y, int z, int id) {
        etc.getServer().setBlockAt(id, x, y, z);
        return true;
    }
    /**
     * Gets the highest block at a certain location.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getHighestBlockY(int x, int z) {
        return etc.getServer().getHighestBlockY(x, z);
    }

    /**
     * Set time.
     *
     * @return
     */
    public int getTime() {
        return (int)etc.getServer().getTime();
    }

    /**
     * Set time.
     *
     * @param time
     * @return
     */
    public boolean setTime(int time) {
        etc.getServer().setTime(time);
        return true;
    }
}
