/**
 * Programmer: Jacob Scott
 * Program Name: SRPermissions
 * Description:
 * Date: Jul 4, 2011
 */

package com.jascotty2.chestharvester;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.anjocaido.groupmanager.GroupManager;

/**
 * @author jacob
 */
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
            ChestHarvester.Log("Permissions enabled using: GroupManager v" + version);
        } else if (permissions != null/* && permissions.isEnabled()*/) {
            permissionPlugin = permissions;
            handler = PermissionHandler.PERMISSIONS;
            String version = permissions.getDescription().getVersion();
            ChestHarvester.Log("Permissions enabled using: Permissions v" + version);
        } else {
            handler = PermissionHandler.NONE;
            ChestHarvester.Log("No permission plugin loaded.");
        }
    }

    public static boolean permission(Player player, String permission) {
        try {
            switch (handler) {
                case PERMISSIONS:
                    return ((Permissions) permissionPlugin).getHandler().has(player, permission);
                case GROUP_MANAGER:
                    return ((GroupManager) permissionPlugin).getWorldsHolder().getWorldPermissions(player).has(player, permission);
                case NONE:
                    return true;
                default:
                    return true;
            }
        } catch (Exception ex) {
            if (!permErr) {
                ChestHarvester.Log(Level.SEVERE, "Unexpected Error checking permission: defaulting to true", ex);
                permErr = true;
            }
            return true;
        }
    }
} // end class SRPermissions
