/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: turns chests into stationary farming equipment
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

import me.jascotty2.bettershop.BetterShop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestHarvester extends JavaPlugin {

	protected static ChestHarvester plugin;
    CHConfig config = new CHConfig(this);
    CollectorScanner chestScan = new CollectorScanner(this);
    AutoHarvester harvester = new AutoHarvester(this);
    CHPlayerListener playerListener = new CHPlayerListener(this);
    protected BetterShop betterShopPlugin = null;

	@Override
    public void onEnable() {
		plugin = this;

        if (!config.load()) {
            getLogger().warning("Error loading the configuration file: check for syntax errors");
        }

        CHPermissions.initialize(getServer());

        if (config.chestAutoCollect) {
            chestScan.start(config.chestScanInterval);
        }

        Plugin bs = getServer().getPluginManager().getPlugin("BetterShop");
        if (bs instanceof BetterShop && !bs.getDescription().getVersion().equals("2.0.3")) {
            betterShopPlugin = (BetterShop) bs;
        }

        getServer().getPluginManager().registerEvents(playerListener, this);
    }

	@Override
    public void onDisable() {
        chestScan.cancel();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("chestharvester")) {
            if (!sender.isOp()) {
                sender.sendMessage("Chest Harvester commands are for OPs only!");
            } else {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    if (config.load()) {
                        chestScan.cancel();
                        chestScan = new CollectorScanner(this);
                        if (config.chestAutoCollect) {
                            chestScan.start(config.chestScanInterval);
                        }

                        sender.sendMessage(ChatColor.AQUA.toString() + "Config Reloaded Successfully");
                    } else {
                        sender.sendMessage(ChatColor.RED.toString() + "Error loading the configuration file: check for syntax errors");
                        //Log("Error loading the configuration file: check for syntax errors");
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
} // end class ChestHarvester

