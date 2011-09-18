/**
 * Programmer: Jacob Scott
 * Program Name: CollectorScanner
 * Description:
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import com.jascotty2.ChestManip;
import java.util.logging.Level;
import me.jascotty2.bettershop.BetterShop;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 * @author jacob
 */
public class CollectorScanner implements Runnable {

    public long interval = 1000;
    public boolean autoStack = true;
    private int taskID = -1;
    ChestHarvester plugin;

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
    }

    public void cancel() {
        if (taskID != -1) {
            plugin.getServer().getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

    @Override
    public void run() {
        dropChestScan();
    }

    /**
     * scans for drops, then puts them into the first chest in a 1-block radius that can hold them
     */
    public void dropChestScan() {
        try {
            for (World world : plugin.getServer().getWorlds()) {
				String w = world.getName().toLowerCase();
				if(plugin.config.disabledWorlds.contains(w)){
					continue;
				}
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item){
                        Item item = (Item) entity;
                        Block[] blocks = {
                            item.getLocation().getBlock().getRelative(BlockFace.SELF),
                            item.getLocation().getBlock().getRelative(BlockFace.DOWN),
                            item.getLocation().getBlock().getRelative(BlockFace.NORTH),
                            item.getLocation().getBlock().getRelative(BlockFace.EAST),
                            item.getLocation().getBlock().getRelative(BlockFace.SOUTH),
                            item.getLocation().getBlock().getRelative(BlockFace.WEST),
                            /*item.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST),
                        item.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST)*/};
                        for (Block block : blocks) {
                            if (block.getType() == Material.CHEST) {
								if(plugin.betterShopPlugin != null
										&& BetterShop.getConfig().chestShopEnabled
										&& BetterShop.getChestShop() != null
										&& BetterShop.getChestShop().hasChestShop(block)){
									continue;
								}

                                Chest chest = (Chest) block.getState();
                                ItemStack chestInv[] = ChestManip.getContents(chest);

                                if (autoStack) {
                                    if (!ChestManip.is_fullStack(chestInv, item.getItemStack())) {
                                        ChestManip.addContentsStack(chest, item.getItemStack());
                                        item.remove();
                                        break;
                                    }
                                } else if (!ChestManip.is_full(chestInv, item.getItemStack())) {
                                    ChestManip.addContents(chest, item.getItemStack());
                                    item.remove();
                                    //return;
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            ChestHarvester.Log(Level.SEVERE, e);
        }
    }
} // end class CollectorScanner

