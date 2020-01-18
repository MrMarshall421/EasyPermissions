package dev.mrmarshall.easypermissions.commands;

import com.google.inject.Inject;
import dev.mrmarshall.easypermissions.managers.PermissionManager;
import dev.mrmarshall.easypermissions.sql.EasyPermsDB;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EasyPermissionsCommand implements CommandExecutor {

    @Inject private EasyPermsDB easyPermsDB;
    @Inject private PermissionManager permissionManager;

    //> Commands:
    //>  /ep <player/group> <add/remove> <permission>
    //>  /ep <player> setgroup <group>
    //>  /ep creategroup <group> <inheritance>

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.isOp()) {
                player.sendMessage("§aEasyPerms §7> §cYou are not allowed to do that!");
                return false;
            }
        }

        if(args.length == 3) {
            String target = args[0];

            if(target.equalsIgnoreCase("creategroup")) {
                String group = args[1];
                String inheritance = args[2];
                easyPermsDB.registerGroup(group, inheritance);

                sender.sendMessage("§aEasyPerms §7> §aGroup named " + group + " has been created");
                sender.sendMessage("§aEasyPerms §7> §7§oAdd permissions using /ep " + group + " add <permission>");

                return true;
            }

            if(easyPermsDB.getCurrentGroupPermissions(target) != null) {
                //> Target is a group
                switch(args[1]) {
                    case "add":
                        easyPermsDB.addGroupPermission(target, args[2]);
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            permissionManager.loadPlayerPermissions(all);
                        }
                        sender.sendMessage("§aEasyPerms §7> §bPermission " + args[2] + " has been added to group " + target);
                        break;
                    case "remove":
                        easyPermsDB.removeGroupPermission(target, args[2]);
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            permissionManager.loadPlayerPermissions(all);
                        }
                        sender.sendMessage("§aEasyPerms §7> §bPermission " + args[2] + " has been removed from group " + target);
                        break;
                    default:
                        sendHelpMessage(sender);
                        break;
                }
            } else {
                //> Target is a player
                Player targetPlayer = Bukkit.getPlayer(target);

                if(targetPlayer != null) {
                    switch(args[1]) {
                        case "add":
                            easyPermsDB.addPlayerPermission(targetPlayer, args[2]);
                            permissionManager.loadPlayerPermissions(targetPlayer);
                            sender.sendMessage("§aEasyPerms §7> §bPermission " + args[2] + " has been added to " + target);
                            break;
                        case "remove":
                            easyPermsDB.removePlayerPermission(targetPlayer, args[2]);
                            permissionManager.loadPlayerPermissions(targetPlayer);
                            sender.sendMessage("§aEasyPerms §7> §bPermission " + args[2] + " has been removed from " + target);
                            break;
                        case "setgroup":
                            easyPermsDB.setPlayerGroup(targetPlayer, args[2]);
                            permissionManager.loadPlayerPermissions(targetPlayer);
                            sender.sendMessage("§aEasyPerms §7> §bGroup of " + target + " is now " + args[2]);
                            break;
                        default:
                            sendHelpMessage(sender);
                            break;
                    }
                } else {
                    sender.sendMessage("§aEasyPerms §7> §cThere is no player or group called " + target);
                }
            }
        } else if(args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if(target != null) {
                String[] targetPermissions = easyPermsDB.getCurrentPlayerPermissions(target).split(",");

                sender.sendMessage("§aEasyPerms §7> §bPermissions of " + args[0] + ":");
                for(String permission : targetPermissions) {
                    if(permission.length() > 3) {
                        sender.sendMessage("§b- " + permission);
                    }
                }
            } else {
                sender.sendMessage("§aEasyPerms §7> §cThere is no player called " + args[0]);
            }
        } else {
            sendHelpMessage(sender);
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§b==========> §aEasyPerms §b<==========");
        sender.sendMessage("§a§l/ep <player/group> <add/remove> <permission>");
        sender.sendMessage("§a§l/ep <player> setgroup <group>");
        sender.sendMessage("§a§l/ep creategroup <group> <inheritance>");
        sender.sendMessage("§a§l/ep <player>");
    }
}
