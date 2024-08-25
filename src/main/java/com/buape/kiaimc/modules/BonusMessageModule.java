package com.buape.kiaimc.modules;

import org.bukkit.event.Listener;

import com.buape.kiaimc.KiaiMC;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

public class BonusMessageModule implements Listener {
	private final KiaiMC plugin;

	public BonusMessageModule(KiaiMC kiaiMC) {
		this.plugin = kiaiMC;
	}

	public void onEnable() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::checkBonusMessage,
				plugin.getConfig().getInt("bonus-message.interval") * 20,
				plugin.getConfig().getInt("bonus-message.interval") * 20);
	}

	public void onDisable() {
		plugin.getServer().getScheduler().cancelTasks(plugin);
	}

	public void checkBonusMessage() {
		plugin.debug("Checking for bonus message");
		plugin.getServer().getOnlinePlayers().forEach(player -> {
			plugin.debug("Checking player " + player.getName());
			TextChannel channel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global");
			Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
			AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
			String discordPlayerId = accountLinkManager.getDiscordId(player.getUniqueId());
			if (discordPlayerId == null || discordPlayerId.isBlank()) {
				plugin.debug("Player " + player.getName() + " is not linked, not processing bonus message");
				return;
			}
			Member guildMember = mainGuild.getMemberById(discordPlayerId);
			plugin.api.virtualMessage(mainGuild.getId(), channel.getId(), guildMember);
		});
	}
}