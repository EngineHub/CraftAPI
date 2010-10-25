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

import com.sk89q.craftapi.xmlrpc.APIException;

/**
 *
 * @author sk89q
 */
public class XMLRPCMinecraftAPI {
    /**
     * Thrown when a bad block index is supplied.
     */
    private static final int BAD_BLOCK_INDEX = 10;
    
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
     * Gets a cuboid. A byte array is returned with the index of each byte
     * defined to be y * width * length + z * width + x. The value of each
     * byte is the block ID. Note that index 0 is min X, min Y, min Z, and it
     * has no relevance with the x1, y1, z1 parameters.
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public byte[] getCuboidIDs(int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        int width = Math.abs(maxX - minX) + 1;
        int height = Math.abs(maxY - minY) + 1;
        int length = Math.abs(maxZ - minZ) + 1;

        byte[] data = new byte[width * height * length];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    int index = y * width * length + z * width + x;
                    data[index] =
                            (byte)etc.getServer().getBlockIdAt(x + minX, y + minY, z + minZ);
                }
            }
        }
        return data;
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
     * Set a cuboid. A byte array must be provided with indexes defined as
     * y * width * length + z * width + x. The value of each
     * byte is the block ID. The minimum X, Y, and Z coordinate must be
     * provided.
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param width
     * @param height
     * @param length
     * @param data
     * @return
     */
    public boolean setCuboidIDs(int x1, int y1, int z1,
            int width, int height, int length, byte[] data)
            throws APIException{
        Server server = etc.getServer();

        try {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        int index = y * width * length + z * width + x;
                        server.setBlockAt((int)data[index], x + x1, y + y1, z + z1);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new APIException(BAD_BLOCK_INDEX, "index = y * width * length + z * width + x");
        }

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
