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

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.iminurnetz.bukkit.permissions.PermissionHandler;

public class WMPlayerListener extends PlayerListener implements Listener {
    private static final String PERMISSION_PREFIX = "worldmodes.";
    private final WorldModesPlugin plugin;
    private final PermissionHandler permissionHandler;

    public WMPlayerListener(WorldModesPlugin plugin, PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        this.plugin = plugin;
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!hasPermission(player, event.getNewGameMode())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
        checkWorldTransition(event);
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        checkWorldTransition(event);
    }

    private void checkWorldTransition(final PlayerMoveEvent event) {
        if (event.isCancelled() || event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Player player = event.getPlayer();
                GameMode mode = player.getGameMode();
                if (!hasPermission(player, mode)) {
                    GameMode newMode = plugin.getToggledMode(player);
                    player.setGameMode(newMode);
                }
            }
        }, 1);
    }

    private boolean hasPermission(Player player, GameMode mode) {
        String permission = PERMISSION_PREFIX + mode.name().toLowerCase();
        return permissionHandler.hasPermission(player, permission);
    }
}
