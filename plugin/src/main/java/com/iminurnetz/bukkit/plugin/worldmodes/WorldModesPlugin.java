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
package com.iminurnetz.bukkit.plugin.worldmodes;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class WorldModesPlugin extends BukkitPlugin {
    public int MIN_SERVER_VERSION = 1031;

    @Override
    public int getMinimumServerVersion() {
        return this.MIN_SERVER_VERSION;
    }

    @Override
    public void enablePlugin() throws Exception {
        PluginManager pm = getServer().getPluginManager();
        WMPlayerListener listener = new WMPlayerListener(this);
        pm.registerEvent(Type.PLAYER_GAME_MODE_CHANGE, listener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_PORTAL, listener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, listener, Priority.Monitor, this);
    }
}
