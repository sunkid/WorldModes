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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.iminurnetz.bukkit.util.SerializableItemStack;

public class PersistedInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<SerializableItemStack> inventory;
    private final List<SerializableItemStack> armor;

    protected PersistedInventory(Player player) {
        inventory = new ArrayList<SerializableItemStack>();
        armor = new ArrayList<SerializableItemStack>();

        for (ItemStack s : player.getInventory().getContents()) {
            if (s == null || s.getAmount() == 0) {
                this.inventory.add(null);
                continue;
            }
            this.inventory.add(new SerializableItemStack(s));
        }

        for (ItemStack s : player.getInventory().getArmorContents()) {
            if (s == null || s.getAmount() == 0) {
                this.armor.add(null);
                continue;
            }
            this.armor.add(new SerializableItemStack(s));
        }
    }

    protected void revertInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        for (int n = 0; n < inventory.size(); n++) {
            if (inventory.get(n) == null) {
                playerInventory.setItem(n, null);
            } else {
                playerInventory.setItem(n, inventory.get(n).getStack());
            }
        }

        ItemStack[] armorContent = new ItemStack[armor.size()];
        for (int n = 0; n < armor.size(); n++) {
            if (armor.get(n) == null) {
                armorContent[n] = new ItemStack(0);
            } else {
                armorContent[n] = armor.get(n).getStack();
            }
        }
        player.getInventory().setArmorContents(armorContent);
    }
}
