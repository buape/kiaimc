package com.buape.kiaimc.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Date;

import com.buape.kiaimc.KiaiMC;

public class LastOnlineCommand implements CommandExecutor {

    private final KiaiMC plugin;

    public LastOnlineCommand(KiaiMC KiaiMC) {
        this.plugin = KiaiMC;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /lastonline <player>");
            return true;
        }
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }
        if (target.getLastSeen() == 0) {
            sender.sendMessage(ChatColor.RED + "Player has never played before.");
            return true;
        }
        Date lastSeen = new Date(target.getLastSeen());
        sender.sendMessage(target.getName() + " was last seen " + lastSeen.toString());

        return true;
    }
}