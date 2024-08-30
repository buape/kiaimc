package com.buape.kiaimc.modules;

import org.bukkit.entity.Player;

import com.buape.kiaimc.KiaiMC;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import github.scarsz.discordsrv.DiscordSRV;
import net.md_5.bungee.api.ChatColor;

public class CommandModule {
	private final KiaiMC plugin;

	public CommandModule(KiaiMC plugin) {
		this.plugin = plugin;
	}

	public void registerAllCommands() {
		new CommandAPICommand("addxp")
				.withArguments(new PlayerArgument("player"))
				.withArguments(new IntegerArgument("amount"))
				.withHelp("Add XP to a player", "Add XP in Kiai to player through Minecraft")
				.withUsage("/addxp <player> <amount>")
				.executes((sender, args) -> {
					Player player = (Player) args.get("player");
					int amount = (int) args.get("amount");
					String discordId = DiscordSRV.getPlugin().getAccountLinkManager()
							.getDiscordId(player.getUniqueId());
					if (discordId == null || discordId.isBlank()) {
						throw CommandAPI.failWithString(
								String.format("%s is not linked to a Discord account!", player.getName()));
					}
					plugin.api.addXp(DiscordSRV.getPlugin().getMainGuild().getId(), player.getUniqueId().toString(),
							amount);
					if (sender instanceof Player) {
						sender.sendMessage(
								ChatColor.GREEN + "You have given " + player.getName() + " " + amount + " XP");
					}
				}).register();

		new CommandAPICommand("removexp")
				.withArguments(new PlayerArgument("player"))
				.withArguments(new IntegerArgument("amount"))
				.withHelp("Remove XP from a player", "Remove XP in Kiai from player through Minecraft")
				.withUsage("/removexp <player> <amount>")
				.executes((sender, args) -> {
					Player player = (Player) args.get("player");
					int amount = (int) args.get("amount");
					String discordId = DiscordSRV.getPlugin().getAccountLinkManager()
							.getDiscordId(player.getUniqueId());
					if (discordId == null || discordId.isBlank()) {
						throw CommandAPI.failWithString(
								String.format("%s is not linked to a Discord account!", player.getName()));
					}
					plugin.api.removeXp(DiscordSRV.getPlugin().getMainGuild().getId(), player.getUniqueId()
							.toString(), amount);
					if (sender instanceof Player) {
						sender.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "You have taken " + amount
								+ " XP from " + player.getName());
					}
				}).register();
	}

}
