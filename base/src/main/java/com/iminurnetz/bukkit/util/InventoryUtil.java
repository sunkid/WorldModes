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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {
    
    public static void giveItems(Player player, List<ItemStack> items) {
		for (ItemStack is : items) {
			giveItems(player, is);
		}
	}

	public static void giveItems(Player player, ItemStack stack) {
	    if (ArmorUtil.isArmor(stack) && !ArmorUtil.wearsArmor(player, stack)) {
	        stack = ArmorUtil.setArmor(player, stack);
	        if (stack == null) {
	            return;
	        }
	    }
	    
	    if (player.getInventory().firstEmpty() == -1)
			player.getWorld().dropItemNaturally(player.getLocation(), stack);
		else {
			player.getInventory().addItem(stack);
			player.updateInventory();
		}
	}

	public static void giveItems(Player player, Material m) {
		ItemStack stack = new ItemStack(m, 1);
		giveItems(player, stack);
	}

	public static void giveItems(Player player, Material m, int preferredSlot) {
		int slot = player.getInventory().firstEmpty();
		if (slot > 0) {
			if (slot != preferredSlot) {
				switchItems(player, slot, preferredSlot);
			}
			player.getInventory().setItem(preferredSlot, new ItemStack(m, 1));
		} else {
			giveItems(player, m);
		}
	}

	public static void switchItems(Player player, int slot1, int slot2) {
		ItemStack is = player.getInventory().getItem(slot2);
		ItemStack is2 = player.getInventory().getItem(slot1);
		if (is2 != null && is2.getAmount() != 0) {
			player.getInventory().setItem(slot2, is2);
		} else {
			player.getInventory().setItem(slot2, null);
		}
		
		if (is != null && is.getAmount() != 0) {
			player.getInventory().setItem(slot1, is);
		} else {
			player.getInventory().setItem(slot1, null);
		}
	}

	public static void switchToItems(Player player, Material m) {
		if (player.getItemInHand().getType().equals(m)) {
			return;
		}
		
		int slot = player.getInventory().first(m);
		if (slot < 0 || slot == player.getInventory().getHeldItemSlot()) {
			return;
		} else if (slot < 9) {
			// I would really like to just switch to it, but it seems I cannot
		}
		
		switchItems(player, slot, player.getInventory().getHeldItemSlot());
	}

	/**
	 * Check if a chest is part of a large chest and return the second part.
	 * @param chest a {@link Chest} block to check
	 * @return the second part of the large chest or null if there isn't one
	 */
    public static Chest getNeighborChest(Chest chest) {
        return getNeighborChest(chest.getBlock());
    }
    
    public static Chest getNeighborChest(Block block) {
        for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST)) {
            BlockState state = block.getRelative(face).getState();
            if (state instanceof Chest) {
                return (Chest) state;            
            }
        }
        return null;
    }

    /**
     * Remove a set of materials from a player's inventory.
     * @param player the player
     * @param items a map of materials and number of items to be removed
     * @return a list of items that were not removed
     */
    public static List<ItemStack> remove(Player player, HashMap<Material, Integer> items) {
        ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
        if (items == null) {
            return stuff;
        }
        for (Material m : items.keySet()) {
            ItemStack stack = MaterialUtils.getStack(m, items.get(m));
            stuff.add(stack);
        }
        
        return remove(player, stuff);
    }

    public static List<ItemStack> remove(Player player, ItemStack stack) {
        return remove(player, Arrays.asList(stack));
    }

    /**
     * Remove a set of materials from a player's inventory.
     * @param player the player
     * @param items a list of ItemStacks to be removed
     * @return a list of items that were not removed
     */    
    public static List<ItemStack> remove(Player player, List<ItemStack> items) {        
        ArrayList<ItemStack> notRemoved = new ArrayList<ItemStack>();
        if (items == null) {
            return notRemoved;
        }
        
        HashMap<Material, Integer> tmp = new HashMap<Material, Integer>();
        int slot, toBeRemoved, n;
        PlayerInventory inventory = player.getInventory();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack posessions;
        for (ItemStack stack : items) {
            toBeRemoved = stack.getAmount();
            while (toBeRemoved > 0) {
                if (ArmorUtil.isArmor(stack) && ArmorUtil.wearsArmor(player, stack)) {
                    ArmorUtil.remove(player, stack);
                    toBeRemoved--;
                } else {
                    slot = inventory.first(stack.getType());
                
                    if (slot != -1) {
                        posessions = inventory.getItem(slot);
                        if (posessions.getAmount() > toBeRemoved) {
                            posessions.setAmount(posessions.getAmount() - toBeRemoved);
                            toBeRemoved = 0;
                        } else {
                            toBeRemoved -= posessions.getAmount();
                            inventory.setItem(slot, null);
                        }
                    } else {
                        break;
                    }
                }
            }
            
            n = 0;
            if (toBeRemoved > 0) {
                n = tmp.get(stack.getType()) == null ? 0 : tmp.get(stack.getType());
                tmp.put(stack.getType(), n + toBeRemoved);
            }
        }
        
        for (Material m : tmp.keySet()) {
            ItemStack stack = MaterialUtils.getStack(m, tmp.get(m));
            notRemoved.add(stack);
        }
        
        return notRemoved;
    }

    public static List<ItemStack> getInventoryContents(Inventory inventory) {
        ArrayList<ItemStack> contents = new ArrayList<ItemStack>();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                contents.add(stack);
            }
        }
        return contents;
    }

    public static boolean hasItem(Player player, Item item) {
        if (item == null) {
            return true;
        }
        
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null &&
                    item.getMaterial() == stack.getType() &&
                    ((item.getData() != null && stack.getData() != null &&
                            item.getData().getData() == stack.getData().getData()) ||
                            item.getData() == item.getData())) {
                return true;                
            }
        }
        
        return false;
    }

    public static void removeItemNearItemHeldInHand(Player player, Material material) {
        removeItemNearItemHeldInHand(player.getInventory(), material);
        player.updateInventory();
    }

    public static void removeItemNearItemHeldInHand(PlayerInventory inventory, Material material) {
        int slot = inventory.getHeldItemSlot();
        if (inventory.getItem(slot).getType() == material) {
            // default
        } else if (slot < 8 && inventory.getItem(slot + 1).getType() == material) {
            slot += 1;
        } else if (slot > 0) {
            slot -= 1;
        } else {
            return;
        }

        ItemStack stack = inventory.getItem(slot);
        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() == 0) {
            stack = null;
        }
        inventory.setItem(slot, stack);
    }
}
