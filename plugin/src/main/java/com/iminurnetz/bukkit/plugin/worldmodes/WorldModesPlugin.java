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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.permissions.PermissionHandler;
import com.iminurnetz.bukkit.permissions.PermissionHandlerService;
import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;

public class WorldModesPlugin extends BukkitPlugin {
    private PermissionHandler permissionHandler;

    @Override
    public void enablePlugin() throws Exception {
        PluginManager pm = getServer().getPluginManager();
        permissionHandler = PermissionHandlerService.getHandler(this);

        WMPlayerListener listener = new WMPlayerListener(this, permissionHandler);

        pm.registerEvent(Type.PLAYER_GAME_MODE_CHANGE, listener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_DROP_ITEM, listener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, listener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, listener, Priority.Lowest, this);

        pm.registerEvent(Type.PLAYER_JOIN, listener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_CHANGED_WORLD, listener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_RESPAWN, listener, Priority.Monitor, this);

        pm.registerEvent(Type.PLAYER_GAME_MODE_CHANGE, new GameModeChangePlayerListener(this, permissionHandler), Priority.Monitor, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        boolean senderIsPlayer = false;
        if (sender instanceof Player) {
            player = (Player) sender;
            senderIsPlayer = true;
        }

        Player targetPlayer = null;
        GameMode mode = null;
        switch (args.length) {
        case 0:
            if (senderIsPlayer) {
                targetPlayer = player;
                mode = getToggledMode(player);
            }
            break;

        case 1:
            targetPlayer = getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                if (senderIsPlayer) {
                    targetPlayer = player;
                    mode = extractMode(args[0]);
                }
            } else {
                mode = getToggledMode(targetPlayer);
            }
            break;

        case 2:
            targetPlayer = getServer().getPlayer(args[0]);
            mode = extractMode(args[1]);
            break;

        default:
            return false;
        }

        if (senderIsPlayer) {
            if (player.equals(targetPlayer)) {
                if (!permissionHandler.hasPermission(player, "worldmodes.set.self")) {
                    MessageUtils.send(sender, ChatColor.RED, "You are not permitted to change your game mode!");
                    return true;
                }
            } else if (targetPlayer != null) {
                String group = permissionHandler.getGroup(targetPlayer);
                if (group != null) {
                    group = group.toLowerCase();
                } else {
                    group = "others";
                }

                if (!permissionHandler.hasPermission(player, "worldmodes.set.others") && !permissionHandler.hasPermission(player, "worldmodes.set." + group)) {
                    MessageUtils.send(sender, ChatColor.RED, "You are not permitted to change " + player.getName() + "'s game mode!");
                    return true;
                }
            }
        }
        
        if (targetPlayer == null) {
            MessageUtils.send(sender, ChatColor.RED, "You must provide a valid player name!");
            return false;
        } else if (mode == null) {
            return false;
        }
        
        if (mode == targetPlayer.getGameMode()) {
            if (senderIsPlayer) {
                MessageUtils.send(sender, ChatColor.RED, "Your game mode is already set to " + ChatColor.YELLOW + mode.name() + ChatColor.RED + "!");
            } else {
                MessageUtils.send(sender, ChatColor.RED, targetPlayer.getName() + "'s game mode is already set to " + ChatColor.YELLOW + mode.name() + ChatColor.RED + "!");
            }
            return true;
        }

        targetPlayer.setGameMode(mode);

        if (senderIsPlayer && targetPlayer.getGameMode() != mode) {
            MessageUtils.send(sender, ChatColor.RED, "The game mode change was cancelled!");
        }

        return true;
    }

    private GameMode extractMode(String arg) {
        if (arg == null) {
            return null;
        }

        try {
            int value = Integer.valueOf(arg);
            return GameMode.getByValue(value);
        } catch (NumberFormatException e) {
            if (arg.startsWith("s")) {
                return GameMode.SURVIVAL;
            } else if (arg.startsWith("c")) {
                return GameMode.CREATIVE;
            }
        }

        return null;
    }

    protected GameMode getToggledMode(Player player) {
        return GameMode.getByValue(player.getGameMode().getValue() == 0 ? 1 : 0);
    }
}
