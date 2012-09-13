/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: chestharvester configuration
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

public class CHConfig {

	public static File pluginFolder = new File(
			"plugins" + File.separatorChar + "ChestHarvester"), configFile = null;
	// settings
	public boolean chestAutoCollect = true,
			autoStack = false,
			manualHarvest = true,
			redstoneHarvest = false,
			harvestPermission = false,
			directionalHarvest = true,
			useBonemeal = true,
			autotill = true,
			useHoe = true,
			replant = true,
			harvestCorners = true,
			harvestWheat = true,
			harvestReeds = true,
			harvestCactus = true,
			harvestPumpkins = true,
			harvestMelons = true,			
			harvestWarts = true,
			allDirections = false,
			storageCartsCollect = false,
			storageCartsEmpty = false;
	public long chestScanInterval = 5000, // every 5 seconds
			minFarmWait = 2000; // min. wait before will run autofarm again for a given chest
	public int autoFarmRange = 5,
			autoFarmHeight = 1,
			scanRange = 1,
			redstoneOverload = 1;
	public OverloadAction redstoneOverloadAction = OverloadAction.WAIT;
	protected ArrayList<String> disabledWorlds = new ArrayList<String>();
	private ChestHarvester plugin;

	public CHConfig(ChestHarvester plugin) {
		//pluginFolder = plugin.getDataFolder().;
		this.plugin = plugin;
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
			FileConfiguration config = plugin.getConfig();
			config.load(configFile);
			chestAutoCollect = config.getBoolean("AutoCollect", chestAutoCollect);
			allDirections = config.getBoolean("scanFull", allDirections);
			scanRange = config.getInt("scanDistance", scanRange);
			storageCartsCollect = config.getBoolean("storageCartsCollect", storageCartsCollect);
			storageCartsEmpty = config.getBoolean("storageCartsEmpty", storageCartsEmpty);
			autoStack = config.getBoolean("AutoStack", autoStack);
			manualHarvest = config.getBoolean("ManualHarvest", manualHarvest);
			redstoneHarvest = config.getBoolean("RedstoneHarvest", redstoneHarvest);
			harvestPermission = config.getBoolean("ManualHarvestPermissions", harvestPermission);
			chestScanInterval = config.getInt("AutoCollectScanInterval", (int) chestScanInterval / 1000) * 1000;
			minFarmWait = config.getInt("ManualHarvestWaitInterval", (int) minFarmWait / 1000) * 1000;
			redstoneOverload = config.getInt("RedstoneOverload", 1);
			String overloadAction = config.getString("RedstoneOverloadAction", "");
			if(overloadAction.equalsIgnoreCase("explode")) {
				redstoneOverloadAction = OverloadAction.EXPLODE;
			} else {
				redstoneOverloadAction = OverloadAction.WAIT;
			}
			Object v = config.get("harvesting");
			if (v instanceof MemorySection) {
				MemorySection n = (MemorySection) v;
				directionalHarvest = n.getBoolean("directional", directionalHarvest);
				useBonemeal = n.getBoolean("useBonemeal", useBonemeal);
				autoFarmRange = n.getInt("range", autoFarmRange);
				autoFarmHeight = n.getInt("height", autoFarmHeight);
				useHoe = n.getBoolean("useHoe", useHoe);
				harvestCorners = n.getBoolean("harvestCorners", harvestCorners);
				replant = n.getBoolean("replant", replant);
				autotill = n.getBoolean("autotill", autotill);
				harvestWheat = n.getBoolean("harvestWheat", harvestWheat);
				harvestReeds = n.getBoolean("harvestReeds", harvestReeds);
				harvestCactus = n.getBoolean("harvestCactus", harvestCactus);
				harvestPumpkins = n.getBoolean("harvestPumpkins", harvestPumpkins);
				harvestMelons = n.getBoolean("harvestMelons", harvestMelons);				
				harvestWarts = n.getBoolean("harvestWarts", harvestWarts);
			}
			disabledWorlds.clear();
			String wlds = config.getString("disabledWorlds");
			if (wlds != null) {
				for (String w : wlds.split(",")) {
					w = w.trim().toLowerCase();
					if (!w.isEmpty()) {
						disabledWorlds.add(w);
					}
				}
			}
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	private void extractConfig() {

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

				plugin.getLogger().info(" Default config file written " + configFile);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
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
	
	public static enum OverloadAction {
		WAIT,
		EXPLODE
	}
} // end class CHConfig

