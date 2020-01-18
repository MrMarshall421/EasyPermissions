package dev.mrmarshall.easypermissions.listeners;

import com.google.inject.Inject;
import dev.mrmarshall.easypermissions.managers.PermissionManager;
import dev.mrmarshall.easypermissions.sql.EasyPermsDB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @Inject private EasyPermsDB easyPermsDB;
    @Inject private PermissionManager permissionManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore()) {
            //> Register new player to database
            easyPermsDB.registerPlayer(player);
        } else {
            permissionManager.loadPlayerPermissions(player);
        }
    }
}
