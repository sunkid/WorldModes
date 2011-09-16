/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
package com.iminurnetz.bukkit.util;

import java.io.Serializable;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BlockLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String world;

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    private final int x;
    private final int y;
    private final int z;
    
    public BlockLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockLocation(Block block) {
        this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof BlockLocation) {
            BlockLocation other = (BlockLocation) o;
            return other.world.equals(world) &&
                other.x == x &&
                other.y == y &&
                other.z == z;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return (((37 * x) + y) * 31 + z) * 17 + world.hashCode();
    }

    @Override
    public String toString() {
        return "BlockLocation[" + world + " x=" + x + ", y=" + y + ", z=" + z + "]";
    }

    public BlockLocation move(Vector move) {
        return new BlockLocation(world, x + move.getBlockX(), y + move.getBlockY(), z + move.getBlockZ());
    }
}