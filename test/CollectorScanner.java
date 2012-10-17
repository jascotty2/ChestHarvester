/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: thread to scan for chest collection
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jascotty2.chestharvester;

import com.jascotty2.ChestManip;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.jascotty2.bettershop.BetterShop;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class CollectorScanner implements Runnable {

    public long interval = 1000;
    public boolean autoStack = true;
    private int taskID = -1, threadId = -1;
    ThreadScanner thread = new ThreadScanner();
    ChestHarvester plugin;
    final Map<Item, Block> job = new HashMap<Item, Block>();

    public CollectorScanner(ChestHarvester plugin) {
        this.plugin = plugin;
    } // end default constructor

    public void start() {
        autoStack = plugin.config.autoStack;
        start(interval);
    }

    public void start(long wait) {
        //(new Timer()).scheduleAtFixedRate(this, wait, wait);
        // 20 ticks per second
        this.interval = wait;
        taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 100, (wait * 20) / 1000);
        threadId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, thread, 90, (wait * 20) / 500);
    }

    public void cancel() {
        if (taskID != -1) {
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

    @Override
    public void run() {
        long st = System.currentTimeMillis();
        dropChestScan();
        System.out.println("latency: " + (System.currentTimeMillis() - st));
    }

    /**
     * scans for drops, then puts them into the first chest in a 1-block radius that can hold them
     */
    public void dropChestScan() {
        int mvd = 0;
        try {
            synchronized (job) {
                for (Map.Entry<Item, Block> e : job.entrySet()) {
                    Item item = e.getKey();
                    Block block = e.getValue();
                    if (block.getType() == Material.CHEST && !item.isDead()) {
                        Chest chest = (Chest) block.getState();
                        ItemStack chestInv[] = ChestManip.getContents(chest);

                        if (autoStack) {
                            if (!ChestManip.is_fullStack(chestInv, item.getItemStack())) {
                                ChestManip.addContentsStack(chest, item.getItemStack());
                                item.remove();
                                ++mvd;
                            }
                        } else if (!ChestManip.is_full(chestInv, item.getItemStack())) {
                            ChestManip.addContents(chest, item.getItemStack());
                            item.remove();
                            ++mvd;
                        }
                    }
                }
                System.out.println(mvd + " collected");
            }
        } catch (Exception e) {
            ChestHarvester.Log(Level.SEVERE, e);
        }
    }

    class ThreadScanner implements Runnable {

        public void run() {
            synchronized (job) {
                job.clear();
                for (World world : plugin.getServer().getWorlds()) {
                    String w = world.getName().toLowerCase();
                    if (plugin.config.disabledWorlds.contains(w)) {
                        continue;
                    }
                    Collection<Item> ens = world.getEntitiesByClass(Item.class);
                    System.out.println("scanning " + ens.size());
                    for (Item item : ens) {
                        Block[] blocks = {
                            item.getLocation().getBlock().getRelative(BlockFace.SELF),
                            item.getLocation().getBlock().getRelative(BlockFace.DOWN),
                            item.getLocation().getBlock().getRelative(BlockFace.NORTH),
                            item.getLocation().getBlock().getRelative(BlockFace.EAST),
                            item.getLocation().getBlock().getRelative(BlockFace.SOUTH),
                            item.getLocation().getBlock().getRelative(BlockFace.WEST), /*item.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST),
                        item.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST)*/};
                        for (Block block : blocks) {
                            if (block.getType() == Material.CHEST) {
                                if (plugin.betterShopPlugin != null
                                        && BetterShop.getSettings().chestShopEnabled
                                        && BetterShop.getChestShop() != null
                                        && BetterShop.getChestShop().hasChestShop(block)) {
                                    continue;
                                }
                                job.put(item, block);
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
} // end class CollectorScanner

