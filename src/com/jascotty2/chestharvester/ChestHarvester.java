/**
 * Programmer: Jacob Scott
 * Program Name: ChestHarvester
 * Description: turns chests into stationary farming equipment
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.jascotty2.bettershop.BetterShop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author jacob
 */
public class ChestHarvester extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    public static final String name = "ChestHarvester";
    CHConfig config = new CHConfig(this);
    CollectorScanner chestScan = new CollectorScanner(this);
    AutoHarvester harvester = new AutoHarvester(this);
    CHPlayerListener playerListener = new CHPlayerListener(this);
	protected BetterShop betterShopPlugin = null;

    public void onEnable() {

        if (!config.load()) {
            Log("Error loading the configuration file: check for syntax errors");
        }

        CHPermissions.initialize(getServer());

        if (config.chestAutoCollect) {
            chestScan.start(config.chestScanInterval);
        }
		
		Plugin bs = getServer().getPluginManager().getPlugin("BetterShop");
		if(bs instanceof BetterShop){
			betterShopPlugin = (BetterShop) bs;
		}

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Highest, this);

        Log("Version " + this.getDescription().getVersion() + " enabled");
    }

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

    public static void Log(String txt) {
        logger.log(Level.INFO, String.format("[%s] %s", name, txt));
    }

    public static void Log(Level loglevel, String txt) {
        Log(loglevel, txt, true);
    }

    public static void Log(Level loglevel, String txt, boolean sendReport) {
        logger.log(loglevel, String.format("[%s] %s", name, txt == null ? "" : txt));
    }

    public static void Log(Level loglevel, String txt, Exception params) {
        if (txt == null) {
            Log(loglevel, params);
        } else {
            logger.log(loglevel, String.format("[%s] %s", name, txt == null ? "" : txt), (Exception) params);
        }
    }

    public static void Log(Level loglevel, Exception err) {
        logger.log(loglevel, String.format("[%s] %s", name, err == null ? "? unknown exception ?" : err.getMessage()), err);
    }
} // end class ChestHarvester

