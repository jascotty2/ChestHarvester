/**
 * Programmer: Jacob Scott
 * Program Name: AutoHarvester
 * Description: provides methods for farming, using resources from a chest
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author jacob
 */
public class AutoHarvester {

    ChestHarvester plugin = null;
    static HashMap<Location, Long> farmTimes = new HashMap<Location, Long>();

    public AutoHarvester(ChestHarvester plugin) {
        this.plugin = plugin;
    } // end default constructor

    public void autoFarm(Chest chestBlock) {
        Block bl = chestBlock.getBlock();
        if (!farmTimes.containsKey(bl.getLocation())
                || System.currentTimeMillis() - farmTimes.get(bl.getLocation()) > plugin.config.chestScanInterval) {
            farmTimes.put(bl.getLocation(), System.currentTimeMillis());
            //ItemStack items[] = ChestManip.getContents(chestBlock);
            //Location loc = bl.getLocation().clone();
            int x = bl.getLocation().getBlockX();
            int y = bl.getLocation().getBlockY();
            int z = bl.getLocation().getBlockZ();
            /*
            for (int dx = -range; dx <= range; ++dx) {
            for (int dy = range; dy > -range; --dy) {
            for (int dz = -range; dz <= range; ++dz) {
            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
            }
            }
            }*/
            for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                    for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                        farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                        farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                        farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                        farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                    }
                }
            }
        }
    }

    public void autoFarm(Player player, Chest chestBlock) {
        Block bl = chestBlock.getBlock();
        if (!farmTimes.containsKey(bl.getLocation())
                || System.currentTimeMillis() - farmTimes.get(bl.getLocation()) > plugin.config.chestScanInterval) {
            farmTimes.put(bl.getLocation(), System.currentTimeMillis());

            int x = bl.getLocation().getBlockX();
            int y = bl.getLocation().getBlockY();
            int z = bl.getLocation().getBlockZ();
            
            // get the direction the player is facing
            double rot = (player.getLocation().getYaw() - 90) % 360;
            if (rot < 0) {
                rot += 360;
            }
            if ((0 <= rot && rot < 22.5) || (337.5 <= rot && rot < 360.0)) { // North: ++x
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (67.5 <= rot && rot < 112.5) { //East: --z
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (157.5 <= rot && rot < 202.5) { //South: --x
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (247.5 <= rot && rot < 292.5) { //West: ++z
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (22.5 <= rot && rot < 67.5) { //Northeast
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            if (dx >= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            }
                            if (dx <= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            }
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (112.5 <= rot && rot < 157.5) { //Southeast
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            if(dx >= dz)
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            if(dx <= dz)
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (202.5 <= rot && rot < 247.5) { //Southwest
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            if (dx <= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            }
                            if (dx >= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            }
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            } else if (292.5 <= rot && rot < 337.5) { //Northwest
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            if(dx <= dz)
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            if(dx >= dz)
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                        }
                    }
                }
            }
        }
    }

    void farmBlock(Chest source, Block toFarm) {
        int id = toFarm.getTypeId();
        if (id == Material.CROPS.getId()) {
            if (plugin.config.useBonemeal && toFarm.getData() != 0x7
                    && ChestManip.containsItem(source, Material.INK_SACK, (short)15)){
                toFarm.setData((byte)7);
                ChestManip.removeItem(source, Material.INK_SACK, (short)15);
            }
            if (toFarm.getData() == 0x7) {//fully grown
                int w = Rand.RandomInt(1, 2);
                int s = Rand.RandomInt(1, 3);
                
                ChestManip.addContents(source, new ItemStack(Material.WHEAT, w));
                if (s > 0) {
                    ChestManip.addContents(source, new ItemStack(Material.SEEDS, s));
                }
                toFarm.setTypeId(0);
            }
        } else if ((id == 2 || id == 3 || id == 60)
                && toFarm.getRelative(BlockFace.UP).getTypeId() == 0
                && ChestManip.containsItem(source, Material.SEEDS)) {
            toFarm.setTypeId(60);
            toFarm.getRelative(BlockFace.UP).setTypeId(Material.CROPS.getId());
            ChestManip.removeItem(source, Material.SEEDS);
        }
    }
}// end class AutoHarvester

