package com.buape.kiaimc.commands;

import org.bukkit.entity.Player;

import com.buape.kiaimc.KiaiMC;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;

@CommandAlias("xp")
public class XpCommand extends BaseCommand {
	private final KiaiMC plugin;
	private final String guildId;

	public XpCommand(KiaiMC plugin) {
		this.plugin = plugin;
		Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
		this.guildId = mainGuild.getId();
	}

	@Subcommand("add")
	@Description("Add XP to a player")
	@CommandPermission("kiaimc.xp.add")
	public void addXp(Player player, Player target, @Default("1") int amount) {
		plugin.api.addXp(guildId, player.getUniqueId().toString(), amount);
		player.sendMessage("You have given " + target.getName() + " " + amount + " XP");
	}

	@Subcommand("remove")
	@Description("Remove XP from a player")
	@CommandPermission("kiaimc.xp.remove")
	public void removeXp(Player player, Player target, @Default("1") int amount) {
		plugin.api.removeXp(guildId, player.getUniqueId().toString(), amount);
		player.sendMessage("You have taken " + amount + " XP from " + target.getName());
	}
}