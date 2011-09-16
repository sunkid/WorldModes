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

package com.iminurnetz.bukkit.permissions;

import org.bukkit.entity.Player;

public class DefaultPermissions implements PermissionHandler {

	private boolean defaultPermission = false;
	private boolean enableOps = true;
	
	public DefaultPermissions(boolean defaultPermission) {
		this.defaultPermission = defaultPermission;
	}
	
	public void allowEverything() {
		defaultPermission = true;
	}
	
	public void denyEverything() {
		defaultPermission = false;
	}
	
	public void enableOps(boolean bool) {
		this.enableOps = bool;
	}
	
    @Override
	public boolean hasPermission(Player player, String permission) {
        return (defaultPermission || (enableOps && player.isOp()) || player.hasPermission(permission));
	}

    @Override
    public String getGroup(Player player) {
         return null;
    }

    @Override
    public boolean parentGroupsInclude(Player player, String group) {
        return false;
    }

}
