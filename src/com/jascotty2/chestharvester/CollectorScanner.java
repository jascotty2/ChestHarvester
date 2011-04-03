/**
 * Programmer: Jacob Scott
 * Program Name: CollectorScanner
 * Description:
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import java.util.Timer;
import java.util.TimerTask;
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
public class CollectorScanner extends TimerTask {

    public long interval = 1000;
    public boolean autoStack = true;
    public Timer scanTimer = null;
    ChestHarvester plugin;

    public CollectorScanner(ChestHarvester plugin) {
        this.plugin = plugin;
    } // end default constructor

    public void start() {
        autoStack = plugin.config.autoStack;
        start(interval);
    }

    public void start(long interval) {
        if (scanTimer == null) {
            this.interval = interval;
            scanTimer = new Timer();
            scanTimer.scheduleAtFixedRate(this, 100, interval);
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
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getClass().getName().contains("CraftItem")) {
                    Item item = (Item) entity;
                    /*
                    Block[] blocks = {item.getLocation().getBlock().getRelative(BlockFace.SELF),
                    item.getLocation().getBlock().getRelative(BlockFace.UP),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.NORTH),
                    item.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.SOUTH),
                    item.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH_EAST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH_WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST),
                    item.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH_WEST)};//*/
                    Block[] blocks = {
                        item.getLocation().getBlock().getRelative(BlockFace.SELF),
                        item.getLocation().getBlock().getRelative(BlockFace.DOWN),
                        item.getLocation().getBlock().getRelative(BlockFace.NORTH),
                        item.getLocation().getBlock().getRelative(BlockFace.EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH),
                        item.getLocation().getBlock().getRelative(BlockFace.WEST),
                        item.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST),
                        item.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST),
                        item.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST)};
                    for (Block block : blocks) {
                        if (block.getType() == Material.CHEST) {
                            Chest chest = (Chest) block.getState();
                            ItemStack chestInv[] = ChestManip.getContents(chest);

                            if (autoStack) {
                                if (!ChestManip.is_fullStack(chestInv, item.getItemStack())) {
                                    ChestManip.addContentsStack(chest, item.getItemStack());
                                    item.remove();
                                    break;
                                }
                            } else if (!ChestManip.is_full(chestInv, item.getItemStack())) {
                                {
                                    ChestManip.addContents(chest, item.getItemStack());
                                }
                                item.remove();
                                //return;
                                break;
                            }
                        }

                    }
                }
            }
        }
    }
} // end class CollectorScanner

