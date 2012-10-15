/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description: thread
 * to scan for chest collection
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.chestharvester;

import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.libv01.bukkit.inventory.ChestManip;
import me.jascotty2.libv01.bukkit.inventory.ItemStackManip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CollectorScanner implements Runnable {

	public long interval = 1000;
	public boolean autoStack = true;
	private int taskID = -1;
	ChestHarvester plugin;

	public CollectorScanner(ChestHarvester plugin) {
		this.plugin = plugin;
	}

	public void start() {
		autoStack = plugin.config.autoStack;
		start(interval);
	}

	public void start(long wait) {
		//(new Timer()).scheduleAtFixedRate(this, wait, wait);
		// 20 ticks per second
		this.interval = wait;
		taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 100, (wait * 20) / 1000);
	}

	public void cancel() {
		if (taskID != -1) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			taskID = -1;
		}
	}

	@Override
	public void run() {
//        long st = System.currentTimeMillis();
		dropChestScan();
//        System.out.println("scan latency: " + (System.currentTimeMillis() - st));
	}

	/**
	 * scans for drops, then puts them into the first chest in a 1-block radius
	 * that can hold them
	 */
	public void dropChestScan() {
		try {
			Map<Chest, ItemStack[]> chests = new HashMap<Chest, ItemStack[]>();
			Map<StorageMinecart, ItemStack[]> carts = new HashMap<StorageMinecart, ItemStack[]>();
			for (World world : plugin.getServer().getWorlds()) {
				String w = world.getName().toLowerCase();
				if (plugin.config.disabledWorlds.contains(w)) {
					continue;
				}
				for (Entity e : ChestHarvester.plugin.config.storageCartsEmpty
						? world.getEntitiesByClasses(Item.class, StorageMinecart.class)
						: world.getEntitiesByClass(Item.class)) {
					double close = Double.POSITIVE_INFINITY, d;
					Chest closest = null;
					for (Block block : getScanBlocks(e.getLocation().getBlock())) {
						if (block.getType() == Material.CHEST) {
//							if (plugin.betterShopPlugin != null
//									&& BetterShop.getSettings().chestShopEnabled
//									&& BetterShop.getChestShop() != null
//									&& BetterShop.getChestShop().hasChestShop(block)) {
//								continue;
//							}
							d = block.getLocation().add(.5, .5, .5).distance(e.getLocation());
							if (d < close) {
								close = d;
								closest = (Chest) block.getState();
							}
						}
					}
					if (closest != null) {
						ItemStack is[] = chests.get(closest);
						if (is == null) {
							is = closest.getInventory().getContents();//ChestManip.getContents(closest);
							for (int i = 0; i < is.length; ++i) {
								if (is[i] == null) {
									is[i] = new ItemStack(0, 0);
								}
							}
							chests.put(closest, is);
						}
						if (e instanceof Item) {
							Item item = (Item) e;
							ItemStack no = ItemStackManip.add(is, item.getItemStack(), autoStack);
							if (no.getAmount() == 0) {
								item.remove();
							} else {
								item.setItemStack(no);
							}
						} else { // is a StorageMinecart
							StorageMinecart cart = (StorageMinecart) e;
							Inventory inv = cart.getInventory();
							for (int i = 0; i < inv.getSize(); ++i) {
								ItemStack item = inv.getItem(i);
								if (item != null && item.getAmount() > 0
										&& ItemStackManip.amountCanHold(is, item, autoStack) > 0) {
									ItemStack no = ItemStackManip.add(is, item, autoStack);
									if (no.getAmount() == 0) {
										item.setAmount(0);
										inv.setItem(i, null);
									} else {
										item.setAmount(no.getAmount());
										inv.setItem(i, item);
									}
								}
							}
						}
					} else if (plugin.config.storageCartsCollect && e instanceof Item) {
						List<Entity> nearby = e.getNearbyEntities(plugin.config.scanRange * 2, plugin.config.scanRange * 2, plugin.config.scanRange * 2);
						close = Double.POSITIVE_INFINITY;
						StorageMinecart closestCart = null;
						for (Entity en : nearby) {
							if (en instanceof StorageMinecart) {
								d = en.getLocation().distance(e.getLocation());
								if (d < close) {
									close = d;
									closestCart = (StorageMinecart) en;
								}
							}
						}
						if (closestCart != null) {
							ItemStack[] inv = carts.get(closestCart);
							if (inv == null) {
								inv = closestCart.getInventory().getContents();
								carts.put(closestCart, inv);
							}
							ItemStack no = ItemStackManip.add(inv, ((Item) e).getItemStack(), autoStack);
							((Item) e).setItemStack(no);
							if (no.getAmount() == 0) {
								e.remove();
							}
						}
					}
				}
			}
			for (Map.Entry<Chest, ItemStack[]> c : chests.entrySet()) {
				ChestManip.setContents(c.getKey(), c.getValue());
			}
			for (Map.Entry<StorageMinecart, ItemStack[]> c : carts.entrySet()) {
				c.getKey().getInventory().setContents(c.getValue());
			}
			chests.clear();
			carts.clear();
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public Block[] getScanBlocks(Block b) {
		if ((plugin.config.scanRange <= 1 || plugin.config.scanRange > 3)) {
			return plugin.config.allDirections
					? new Block[]{
						b,
						b.getRelative(BlockFace.DOWN),
						b.getRelative(BlockFace.NORTH),
						b.getRelative(BlockFace.EAST),
						b.getRelative(BlockFace.SOUTH),
						b.getRelative(BlockFace.WEST),
						b.getRelative(BlockFace.UP),
						b.getRelative(BlockFace.NORTH_WEST),
						b.getRelative(BlockFace.NORTH_EAST),
						b.getRelative(BlockFace.SOUTH_EAST),
						b.getRelative(BlockFace.SOUTH_WEST)}
					: new Block[]{
						b,
						b.getRelative(BlockFace.DOWN),
						b.getRelative(BlockFace.NORTH),
						b.getRelative(BlockFace.EAST),
						b.getRelative(BlockFace.SOUTH),
						b.getRelative(BlockFace.WEST)};
		} else {
			ArrayList<Block> blocks = new ArrayList<Block>();
			for (int x = -plugin.config.scanRange;
					x <= plugin.config.scanRange; ++x) {
				for (int z = -plugin.config.scanRange;
						z <= plugin.config.scanRange; ++z) {
					for (int y = -plugin.config.scanRange;
							y <= (plugin.config.allDirections ? plugin.config.scanRange : 0); ++y) {
						Block ab = b.getRelative(x, y, z);
						if (plugin.config.allDirections) {
							blocks.add(ab);
						} else if (b.getLocation().distance(ab.getLocation()) <= plugin.config.scanRange) {
							blocks.add(ab);
						}
					}
				}
			}
			return blocks.toArray(new Block[0]);
		}
	}
} // end class CollectorScanner
