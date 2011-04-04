/**
 * Programmer: Jacob Scott
 * Program Name: CHConfig
 * Description:
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * @author jacob
 */
public class CHConfig {

    public static File pluginFolder = new File(
            "plugins" + File.separatorChar + ChestHarvester.name), configFile = null;
    // settings
    public boolean chestAutoCollect = true,
            autoStack = false,
            manualHarvest = true,
            directionalHarvest = true,
            useBonemeal = true;
    public long chestScanInterval = 5000, // every 5 seconds
            minFarmWait = 2000; // min. wait before will run autofarm again for a given chest
    public int autoFarmRange = 5,
            autoFarmHeight = 3;

    public CHConfig(ChestHarvester plugin) {
        //pluginFolder = plugin.getDataFolder().;
        configFile = new File(pluginFolder, "config.yml");
    }

    public boolean load() {
        if (pluginFolder != null && !pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        if (!configFile.exists()) {
            extractConfig();
        }
        try {
            Configuration config = new Configuration(configFile);
            config.load();
            chestAutoCollect = config.getBoolean("AutoCollect", chestAutoCollect);
            autoStack = config.getBoolean("AutoStack", autoStack);
            manualHarvest = config.getBoolean("ManualHarvest", manualHarvest);
            chestScanInterval = config.getInt("AutoCollectScanInterval", (int) chestScanInterval / 1000) * 1000;
            minFarmWait = config.getInt("ManualHarvestWaitInterval", (int) minFarmWait / 1000) * 1000;
            ConfigurationNode n = config.getNode("harvesting");
            if (n != null) {
                directionalHarvest = n.getBoolean("directional", directionalHarvest);
                useBonemeal = n.getBoolean("useBonemeal", useBonemeal);
                autoFarmRange = n.getInt("range", autoFarmRange);
                autoFarmHeight = n.getInt("height", autoFarmHeight);
            }
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    private static void extractConfig() {

        InputStream input = CHConfig.class.getResourceAsStream("/config.yml");
        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(configFile);
                byte[] buf = new byte[8192];
                int length = 0;

                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                ChestHarvester.Log(" Default config file written " + configFile);
            } catch (Exception e) {
                ChestHarvester.Log(Level.SEVERE, e);
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception e) {
                }
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (Exception e) {
                }
            }
        }
    }
} // end class CHConfig

