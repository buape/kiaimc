package com.buape.kiaimc.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.buape.kiaimc.KiaiMC;

public class ActivityCommand implements CommandExecutor {

    private final KiaiMC plugin;

    public ActivityCommand(KiaiMC KiaiMC) {
        this.plugin = KiaiMC;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            this.plugin.activityCheck.checkPlayer(player);
            player.sendMessage(ChatColor.GREEN + "Your roles have been synced!");
        } else
            this.plugin.logger.info("This command can only be run by players");

        return true;
    }
}