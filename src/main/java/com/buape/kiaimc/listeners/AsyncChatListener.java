package com.buape.kiaimc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.buape.kiaimc.KiaiMC;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import io.papermc.paper.event.player.AsyncChatEvent;

public class AsyncChatListener implements Listener {

	private final KiaiMC kiaiMC;

	public AsyncChatListener(KiaiMC kiaiMC) {
		this.kiaiMC = kiaiMC;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
		Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
		TextChannel channel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global");

		String discordPlayerId = accountLinkManager.getDiscordId(player.getUniqueId());

		if (discordPlayerId == null || discordPlayerId.isBlank()) {
			this.kiaiMC
					.debug("Player " + player.getName() + " is not linked, not processing Kiai XP through DiscordSRV.");
		} else {
			this.kiaiMC.debug("Player " + player.getName() + " is linked to " + discordPlayerId
					+ ", processing Kiai XP through DiscordSRV. (Guild ID: " + mainGuild.getId() + ")");
			this.kiaiMC.triggerMessage(mainGuild.getId(), discordPlayerId, channel.getId());
		}
	}

}
