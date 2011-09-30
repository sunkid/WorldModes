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

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.iminurnetz.bukkit.permissions.PermissionHandler;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;

public class WMPlayerListener extends PlayerListener implements Listener {
    private static final String PERMISSION_PREFIX = "worldmodes.";
    private final WorldModesPlugin plugin;
    private final PermissionHandler permissionHandler;

    public WMPlayerListener(WorldModesPlugin plugin, PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!hasPermission(player, player.getGameMode())) {
            player.setGameMode(plugin.getToggledMode(player));
        } else {
            checkAndSetDefaultMode(player);
        }
    }

    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (checkDropPermission(event)) {
            MessageUtils.send(event.getPlayer(), ChatColor.RED, "You are not allowed to drop items!");
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        if (block.getType() == Material.CHEST || block.getType() == Material.DISPENSER) {
            if (checkDropPermission(event)) {
                MessageUtils.send(event.getPlayer(), ChatColor.RED, "You are not allowed to use chests or dispensers!");
            }
        }
    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof StorageMinecart) {
            if (checkDropPermission(event)) {
                MessageUtils.send(event.getPlayer(), ChatColor.RED, "You are not allowed to use chests or dispensers!");
            }
        }
    }

    private boolean checkDropPermission(PlayerEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && !permissionHandler.hasPermission(player, "worldmodes.drop")) {
            ((Cancellable) event).setCancelled(true);
            return true;
        }

        return false;
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
        if (event.isCancelled()) {
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Player player = event.getPlayer();
                GameMode mode = player.getGameMode();
                // null checks on mode are to prevent spurious errors on start-up
                if (mode != null && !hasPermission(player, mode)) {
                    player.setGameMode(plugin.getToggledMode(player));
                } else if (mode != null) {
                    checkAndSetDefaultMode(player);
                }
            }
        }, 1);
    }

    private boolean hasPermission(Player player, GameMode mode) {
        String permission = PERMISSION_PREFIX + mode.name().toLowerCase();
        return permissionHandler.hasPermission(player, permission);
    }

    private void checkAndSetDefaultMode(Player player) {
        if (permissionHandler.hasPermission(player, "worldmodes.autoset.survival") && !permissionHandler.hasPermission(player, "worldmodes.autoset.creative")) {
            player.setGameMode(GameMode.SURVIVAL);
        } else if (!permissionHandler.hasPermission(player, "worldmodes.autoset.survival") && permissionHandler.hasPermission(player, "worldmodes.autoset.creative")) {
            player.setGameMode(GameMode.CREATIVE);
        }
    }
}
