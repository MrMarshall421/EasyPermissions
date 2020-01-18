package dev.mrmarshall.easypermissions.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.mrmarshall.easypermissions.app.EasyPermissions;
import dev.mrmarshall.easypermissions.sql.EasyPermsDB;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PermissionManager {

    @Inject private EasyPermissions plugin;
    @Inject private EasyPermsDB easyPermsDB;

    public Map<UUID, PermissionAttachment> permissions = new HashMap<>();

    public void loadPlayerPermissions(Player player) {
        try {
            String currentPlayerGroup = easyPermsDB.getCurrentPlayerGroup(player);
            String groupInheritance = easyPermsDB.getGroupInheritance(currentPlayerGroup);
            String[] currentPlayerPermissions = easyPermsDB.getCurrentPlayerPermissions(player).split(",");
            String[] currentGroupPermissions = easyPermsDB.getCurrentGroupPermissions(currentPlayerGroup).split(",");
            String[] groupInheritancePermissions = new String[0];
            if(easyPermsDB.getCurrentGroupPermissions(groupInheritance) != null) {
                groupInheritancePermissions = easyPermsDB.getCurrentGroupPermissions(easyPermsDB.getGroupInheritance(currentPlayerGroup)).split(",");
            }

            if(permissions.get(player.getUniqueId()) != null) {
                player.removeAttachment(permissions.get(player.getUniqueId()));
            }

            PermissionAttachment permissionAttachment = player.addAttachment(plugin);

            for(String playerPermission : currentPlayerPermissions) {
                permissionAttachment.setPermission(playerPermission, true);
            }

            for(String groupPermission : currentGroupPermissions) {
                permissionAttachment.setPermission(groupPermission, true);
            }

            for(String inheritancePermission : groupInheritancePermissions) {
                permissionAttachment.setPermission(inheritancePermission, true);
            }

            permissions.put(player.getUniqueId(), permissionAttachment);
        } catch(NullPointerException ex) {
        }
    }
}
