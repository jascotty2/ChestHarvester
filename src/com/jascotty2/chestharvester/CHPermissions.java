/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: permissions handler
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

import me.jascotty2.libv01.util.Str;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.anjocaido.groupmanager.GroupManager;

public class CHPermissions {

    private enum PermissionHandler {

        PERMISSIONS, GROUP_MANAGER, NONE
    }
    private static PermissionHandler handler;
    private static Plugin permissionPlugin;
    private static boolean permErr = false;

    public static void initialize(Server server) {
        Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
        Plugin permissions = server.getPluginManager().getPlugin("Permissions");

        if (groupManager != null/* && groupManager.isEnabled()*/) {
            permissionPlugin = groupManager;
            handler = PermissionHandler.GROUP_MANAGER;
            String version = groupManager.getDescription().getVersion();
            ChestHarvester.plugin.getLogger().info("Permissions enabled using: GroupManager v" + version);
        } else if (permissions != null/* && permissions.isEnabled()*/) {
            permissionPlugin = permissions;
            handler = PermissionHandler.PERMISSIONS;
            String version = permissions.getDescription().getVersion();
            ChestHarvester.plugin.getLogger().info("Permissions enabled using: Permissions v" + version);
        } else {
            handler = PermissionHandler.NONE;
            //ChestHarvester.Log("No permission plugin loaded.");
        }
    }

    public static boolean permission(Player player, String permission) {
        try {
            switch (handler) {
                case PERMISSIONS:
                    return ((Permissions) permissionPlugin).getHandler().has(player, permission);
                case GROUP_MANAGER:
                    return ((GroupManager) permissionPlugin).getWorldsHolder().getWorldPermissions(player).has(player, permission);
                default:
                    if (player == null || player.isOp() || !(player instanceof Player) // ops override permission check (double-check is a Player)
                            || permission == null || permission.length() == 0) {
                        return true;
                    }
                    return has((Player) player, permission);
            }
        } catch (Exception ex) {
            if (!permErr) {
                ChestHarvester.plugin.getLogger().log(Level.SEVERE, "Unexpected Error checking permission: defaulting to builtin", ex);
                permErr = true;
            }
            return true;
        }
    }

    public static boolean has(Player player, String node) {
        try {
            if (player.hasPermission(node)) {
                return true;
            } else if (!node.contains("*") && Str.count(node, '.') >= 2) {
                return player.hasPermission(node.substring(0, node.lastIndexOf('.') + 1) + "*");
            }
            return false;
        } catch (Exception e) {
            ChestHarvester.plugin.getLogger().log(Level.SEVERE, "Error checking permission..", e);
        }
        return false;
    }
} // end class SRPermissions
