/**
 * Programmer: Jacob Scott
 * Program Name: CHPlayerListener
 * Description:
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * @author jacob
 */
public class CHPlayerListener extends PlayerListener {

    ChestHarvester plugin = null;

    public CHPlayerListener(ChestHarvester plugin) {
        this.plugin = plugin;
    } // end default constructor

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.isCancelled()
                && event.getAction() == Action.LEFT_CLICK_BLOCK
                && plugin.config.manualHarvest
                && event.getClickedBlock().getType() == Material.CHEST) {
            if (!plugin.config.harvestPermission
                    || CHPermissions.permission(event.getPlayer(), "ChestHarvester.harvest")) {
                if (plugin.config.directionalHarvest) {
                    plugin.harvester.autoFarm(event.getPlayer(), (Chest) event.getClickedBlock().getState());
                } else {
                    plugin.harvester.autoFarm((Chest) event.getClickedBlock().getState());
                }
            }
        }
    }
} // end class CHPlayerListener

