package me.eventually.hikarisync.core.command;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisyncapi.HSAddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;


public class HikariSyncCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) return false;
        switch (args[0]) {
            case "addons" -> {
                List<HSAddon> addons = HikariSyncCore.getAddonManager().getAddonClasses();
                commandSender.sendMessage("Addons: " + addons.size());
                for (HSAddon addon : addons) {
                    commandSender.sendMessage(addon.getAddonName() + " " + addon.getAddonVersion());
                }
            }
            case "reconnect" -> {
                if (HikariSyncCore.getInstance().initDbConnection()) {
                    commandSender.sendMessage("Reconnected to database.");
                }else {
                    commandSender.sendMessage("Failed to reconnect to database.");
                    commandSender.sendMessage("1.The database is already connected.");
                    commandSender.sendMessage("2.Your database config has mistakes.");
                }
                return true;

            }
            case "version" -> {
                commandSender.sendMessage("HikariSync version: " + HikariSyncCore.getInstance().getDescription().getVersion());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        switch (args.length){
            case 0, 1 -> {
                return List.of("addons", "reconnect", "version");
            }
        }
        return List.of();
    }
}
