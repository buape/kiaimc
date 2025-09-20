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

		int interval = plugin.getConfig().getInt("bonus-message.interval");

		plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkBonusMessage, interval * 20, interval * 20);

		this.plugin.debug(String
				.format("Bonus message module has been enabled, will give a bonus message every %s seconds", interval));
	}

	public void disable() {
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
			String messageId = java.util.UUID.randomUUID().toString();
			String content = "Bonus message for " + player.getName();
			if (channel == null) {
				plugin.getLogger().warning("BonusMessageModule: channel is null, cannot send bonus message.");
				return;
			}
			if (channel.getParent() == null) {
				plugin.getLogger().warning("BonusMessageModule: channel parent is null, cannot send bonus message.");
				return;
			}
			if (guildMember == null) {
				plugin.getLogger().warning("BonusMessageModule: guildMember is null, cannot send bonus message.");
				return;
			}
			if (player == null) {
				plugin.getLogger().warning("BonusMessageModule: player is null, cannot send bonus message.");
				return;
			}
			plugin.api.virtualMessage(mainGuild.getId(), channel.getId(), guildMember, channel.getParent().getId(), messageId, content);
		});
	}
}