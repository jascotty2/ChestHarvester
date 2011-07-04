/**
 * Programmer: Jacob Scott
 * Program Name: AutoHarvester
 * Description: provides methods for farming, using resources from a chest
 * Date: Apr 2, 2011
 */
package com.jascotty2.chestharvester;

import com.jascotty2.ChestManip;
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

    enum DIRECTION {

        NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
    }

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
//            if (!canFarm(chestBlock)) {
//                return;
//            }

            int x = bl.getLocation().getBlockX();
            int y = bl.getLocation().getBlockY();
            int z = bl.getLocation().getBlockZ();

            // get the direction the player is facing
            double rot = (player.getLocation().getYaw() - 90) % 360;
            if (rot < 0) {
                rot += 360;
            }
            DIRECTION harvestDir = DIRECTION.NORTH;
            if (plugin.config.harvestCorners) {
                if ((0 <= rot && rot < 22.5) || (337.5 <= rot && rot < 360.0)) { // North
                    harvestDir = DIRECTION.NORTH;
                } else if (67.5 <= rot && rot < 112.5) { //East
                    harvestDir = DIRECTION.EAST;
                } else if (157.5 <= rot && rot < 202.5) { //South
                    harvestDir = DIRECTION.SOUTH;
                } else if (247.5 <= rot && rot < 292.5) { //West
                    harvestDir = DIRECTION.WEST;
                } else if (22.5 <= rot && rot < 67.5) { //Northeast
                    harvestDir = DIRECTION.NORTHEAST;
                } else if (112.5 <= rot && rot < 157.5) { //Southeast
                    harvestDir = DIRECTION.SOUTHEAST;
                } else if (202.5 <= rot && rot < 247.5) { //Southwest
                    harvestDir = DIRECTION.SOUTHWEST;
                } else if (292.5 <= rot && rot < 337.5) {//Northwest
                    harvestDir = DIRECTION.NORTHWEST;
                }
            } else {
                if ((0 <= rot && rot < 45) || (315 <= rot && rot < 360.0)) { // North
                    harvestDir = DIRECTION.NORTH;
                } else if (45 <= rot && rot < 135) { //East
                    harvestDir = DIRECTION.EAST;
                } else if (135 <= rot && rot < 225) { //South
                    harvestDir = DIRECTION.SOUTH;
                } else if (225 <= rot && rot < 315) { //West
                    harvestDir = DIRECTION.WEST;
                }
            }


            if (harvestDir == DIRECTION.NORTH) { // North: ++x
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.EAST) { //East: --z
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.SOUTH) { //South: --x
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.WEST) { //West: ++z
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.NORTHEAST) { //Northeast
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
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.SOUTHEAST) { //Southeast
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            if (dx >= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            }
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            if (dx <= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                            }
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.SOUTHWEST) { //Southwest
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
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            } else if (harvestDir == DIRECTION.NORTHWEST) { //Northwest
                for (int dx = 0; dx <= plugin.config.autoFarmRange; ++dx) {
                    for (int dz = 0; dz <= plugin.config.autoFarmRange; ++dz) {
                        for (int dy = plugin.config.autoFarmHeight; dy > -plugin.config.autoFarmHeight; --dy) {
                            if (dx <= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z + dz));
                            }
                            farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z + dz));
                            //farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x + dx, y + dy, z - dz));
                            if (dx >= dz) {
                                farmBlock(chestBlock, chestBlock.getWorld().getBlockAt(x - dx, y + dy, z - dz));
                            }
//                            if (!canFarm(chestBlock)) {
//                                return;
//                            }
                        }
                    }
                }
            }
        }
    }

//    boolean canFarm(Chest source) {
//        return !(ChestManip.is_full(source, Material.WHEAT) || ChestManip.is_full(source, Material.SEEDS));
//    }
    void farmBlock(Chest source, Block toFarm) {
        int id = toFarm.getTypeId();
        if (id == Material.CROPS.getId()) {
            if (plugin.config.useBonemeal && toFarm.getData() != 0x7
                    && ChestManip.containsItem(source, Material.INK_SACK, (short) 15)) {
                toFarm.setData((byte) 7);
                ChestManip.removeItem(source, Material.INK_SACK, (short) 15);
            }
            if (toFarm.getData() == 0x7 //fully grown
                    && !ChestManip.is_full(source, Material.WHEAT)
                    && !ChestManip.is_full(source, Material.SEEDS)) {
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
            if (id != 60 && plugin.config.useHoe) {
                // search for a hoe
                ItemStack[] chest = ChestManip.getContents(source);
                int hoe = -1;
                for (int i = 0; i < chest.length; ++i) {
                    if (chest[i] != null && (chest[i].getTypeId() >= 290 && chest[i].getTypeId() <= 294)) {
                        hoe = i;
                        break;
                    }
                }
                if (hoe >= 0) {
                    chest[hoe].setDurability((short) (chest[hoe].getDurability() + 1));
                    if (Material.getMaterial(chest[hoe].getTypeId()).getMaxDurability() <= chest[hoe].getDurability()) {
                        chest[hoe] = null;
                        ChestManip.setContents(source, chest);
                    }
                } else {
                    return;
                }
            }
            toFarm.setTypeId(60);
            toFarm.getRelative(BlockFace.UP).setTypeId(Material.CROPS.getId());
            ChestManip.removeItem(source, Material.SEEDS);
        } else if (plugin.config.harvestReeds
                && id == Material.SUGAR_CANE_BLOCK.getId()) {
            if (toFarm.getRelative(BlockFace.DOWN).getTypeId() == Material.SUGAR_CANE_BLOCK.getId()
                    && !ChestManip.is_full(source, Material.SUGAR_CANE)) {

                ChestManip.addContents(source, new ItemStack(Material.SUGAR_CANE, 1));
                toFarm.setTypeIdAndData(0, (byte)0, false);//toFarm.setTypeId(0);
                while (toFarm.getRelative(BlockFace.UP).getTypeId() == Material.SUGAR_CANE_BLOCK.getId()) {
                    toFarm = toFarm.getRelative(BlockFace.UP);
                    ChestManip.addContents(source, new ItemStack(Material.SUGAR_CANE, 1));
                    toFarm.setTypeIdAndData(0, (byte)0, false);
                }
            }
        } else if (plugin.config.harvestCactus
                && id == Material.CACTUS.getId()) {
            if (toFarm.getRelative(BlockFace.DOWN).getTypeId() == Material.CACTUS.getId()
                    && !ChestManip.is_full(source, Material.CACTUS)) {
                ChestManip.addContents(source, new ItemStack(Material.CACTUS, 1));
                toFarm.setTypeIdAndData(0, (byte)0, false);//toFarm.setTypeId(0);
            while (toFarm.getRelative(BlockFace.UP).getTypeId() == Material.CACTUS.getId()) {
                    toFarm = toFarm.getRelative(BlockFace.UP);
                    ChestManip.addContents(source, new ItemStack(Material.CACTUS, 1));
                    toFarm.setTypeIdAndData(0, (byte)0, false);
                }
            }
        }
    }
}// end class AutoHarvester

