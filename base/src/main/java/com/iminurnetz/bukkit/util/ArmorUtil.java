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

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmorUtil {
    /**
     * An enum of armor pieces and their associated inventory slots
     */
    public enum ArmorPiece {
        HELMET(103), CHESTPLATE(102), LEGGINGS(101), BOOTS(100);

        private final int slot;

        private ArmorPiece(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }

    public static final List<Material> ARMOR_MATERIALS = Arrays.asList(Material.LEATHER, Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.FIRE);

    public static boolean isArmor(ItemStack stack) {
        String material = stack.getType().name();
        for (ArmorPiece piece : ArmorPiece.values()) {
            if (material.endsWith(piece.name())) {
                return true;
            }
        }

        return false;
    }

    public static boolean wearsArmor(Player player, ItemStack stack) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() == stack.getType()) {
                return true;
            }
        }

        return false;
    }

    public static void remove(Player player, ItemStack stack) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        PlayerInventory inventory = player.getInventory();
        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() == stack.getType()) {
                switch (getArmorPiece(piece)) {
                case HELMET:
                    inventory.setHelmet(null);
                    break;
                case CHESTPLATE:
                    inventory.setChestplate(null);
                    break;
                case LEGGINGS:
                    inventory.setLeggings(null);
                    break;
                case BOOTS:
                    inventory.setBoots(null);
                    break;
                }
            }
        }
    }

    public static ItemStack setArmor(Player player, ItemStack stack) {
        PlayerInventory inventory = player.getInventory();
        ItemStack previous = null;
        switch (getArmorPiece(stack)) {
        case HELMET:
            previous = inventory.getHelmet();
            inventory.setHelmet(stack);
            break;
        case CHESTPLATE:
            previous = inventory.getChestplate();
            inventory.setChestplate(stack);
            break;
        case LEGGINGS:
            previous = inventory.getLeggings();
            inventory.setLeggings(stack);
            break;
        case BOOTS:
            previous = inventory.getBoots();
            inventory.setBoots(stack);
            break;
        }
        
        return previous;
    }

    public static void setArmor(Player player, Material material) {
        if (ARMOR_MATERIALS.contains(material)) {
            String m = material.name().replace("_INGOT", "");
            for (ArmorPiece piece : ArmorPiece.values()) {
                try {
                    ItemStack s = new Item(m + "_" + piece.name()).getStack();
                    setArmor(player, s);
                } catch (InstantiationException e) {
                    // shouldn't happen!!
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public static ArmorPiece getArmorPiece(ItemStack stack) {
        for (ArmorPiece piece : ArmorPiece.values()) {
            if (stack != null && stack.getType().name().endsWith(piece.name())) {
                return piece;
            }
        }

        return null;
    }

}
