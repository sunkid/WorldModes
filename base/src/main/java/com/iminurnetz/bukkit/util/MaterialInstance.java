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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

public class MaterialInstance {
	protected Material material;
	protected MaterialData data;

	public MaterialInstance(Block block) {
		this.material = block.getState().getType();
		this.data = block.getState().getData();
	}

	public MaterialInstance(Material material) {
		this(material, material.getNewData((byte) 0));
	}

	public MaterialInstance(Material material, MaterialData data) {
		this.material = material;
		this.data = data;
	}

	public MaterialData getData() {
		return data;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setData(MaterialData data) {
		this.data = data;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
}
