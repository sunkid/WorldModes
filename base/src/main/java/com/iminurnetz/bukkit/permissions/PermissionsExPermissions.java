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

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

public class PermissionsExPermissions implements PermissionHandler {

    private PermissionManager handler;
	
    protected void setHandler(PermissionManager handler) {
		this.handler = handler;
	}

	@Override
	public boolean hasPermission(Player player, String permission) {
        return handler.has(player, permission);
	}

    @Override
    // TODO: fix for better multi-group support
    public String getGroup(Player player) {
        PermissionUser user = handler.getUser(player);
        return user.getGroups(player.getWorld().getName())[0].getName();
    }

    @Override
    public boolean parentGroupsInclude(Player player, String group) {
        PermissionUser user = handler.getUser(player);
        return user.inGroup(group, player.getWorld().getName(), true);
    }

}
