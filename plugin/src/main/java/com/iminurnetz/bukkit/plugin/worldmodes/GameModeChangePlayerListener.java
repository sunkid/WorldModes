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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerListener;

import com.iminurnetz.bukkit.permissions.PermissionHandler;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;

public class GameModeChangePlayerListener extends PlayerListener {
    private final WorldModesPlugin plugin;
    private final PermissionHandler permissionHandler;

    protected GameModeChangePlayerListener(WorldModesPlugin plugin, PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        this.plugin = plugin;
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getNewGameMode() != GameMode.CREATIVE) {
            if (restoreInventory(player)) {
                MessageUtils.send(player, ChatColor.GREEN, "Your inventory has been restored");
            }
        } else if (!permissionHandler.hasPermission(player, "worldmodes.keep-inventory")) {
            if (storeInventory(player)) {
                MessageUtils.send(player, ChatColor.GREEN, "Your inventory will be restored when you leave creative mode!");
            }

        }
    }

    private boolean storeInventory(Player player) {
        PersistedInventory inventory = new PersistedInventory(player);
        File piFile = plugin.getPersistedInventoryFile(player);

        File dataDir = piFile.getParentFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(piFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(inventory);
            out.close();
            fos.close();
        } catch (Exception e) {
            plugin.log(Level.SEVERE, "Cannot store inventory for " + player.getName(), e);
            return false;
        }

        return true;
    }

    private boolean restoreInventory(Player player) {
        File piFile = plugin.getPersistedInventoryFile(player);
        if (!piFile.exists()) {
            return false;
        }

        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = new FileInputStream(piFile);
            in = new ObjectInputStream(fis);
        } catch (IOException e) {
            plugin.log(Level.SEVERE, "Cannot load cached inventory for " + player.getName(), e);
            return false;
        }

        PersistedInventory inventory = null;
        try {
            inventory = (PersistedInventory) in.readObject();
        } catch (Exception e) {
            plugin.log(Level.SEVERE, "Cannot load cached inventory for " + player.getName(), e);
            return false;
        }

        inventory.revertInventory(player);

        piFile.delete();
        return true;
    }
}
