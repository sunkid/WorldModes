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

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SerializableBlockType implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String material;
    
    public SerializableBlockType(Block block) {
        this(block.getType(), block.getData());
    }
    
    public SerializableBlockType(Material type, byte data) {
        this(type.name(), data);
    }

    public SerializableBlockType(String material, byte data) {
        this.material = material;
        this.data = data;
    }
    
    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public byte getData() {
        return data;
    }
    public void setData(byte data) {
        this.data = data;
    }
    private byte data;

    
    

}
