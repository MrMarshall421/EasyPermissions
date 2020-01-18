package dev.mrmarshall.easypermissions.app;

import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.mrmarshall.easypermissions.commands.EasyPermissionsCommand;
import dev.mrmarshall.easypermissions.listeners.PlayerJoinListener;
import dev.mrmarshall.easypermissions.sql.EasyPermsDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyPermissions extends JavaPlugin {

    @Inject private EasyPermsDB easyPermsDB;
    @Inject private PlayerJoinListener playerJoinListener;
    @Inject private EasyPermissionsCommand easyPermissionsCommand;

    @Override
    public void onLoad() {
        EasyPermissionsBinderModule binder = new EasyPermissionsBinderModule(this);
        Injector injector = binder.createInjector();
        injector.injectMembers(this);
    }

    @Override
    public void onEnable() {
        easyPermsDB.createTables();

        //> Register Events
        Bukkit.getPluginManager().registerEvents(playerJoinListener, this);

        //> Register Commands
        getCommand("easypermissions").setExecutor(easyPermissionsCommand);
    }
}
