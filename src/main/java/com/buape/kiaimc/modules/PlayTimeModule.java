package com.buape.kiaimc.modules;

import com.buape.kiaimc.KiaiMC;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

public class PlayTimeModule {
	private final KiaiMC plugin;

	public PlayTimeModule(KiaiMC plugin) {
		this.plugin = plugin;

		int interval = plugin.getConfig().getInt("playtime-xp.interval");
		int amount = plugin.getConfig().getInt("playtime-xp.amount");

		plugin.getServer().getScheduler().runTaskTimer(plugin, this::playTimeXp, interval * 20, interval * 20);

		this.plugin.debug(
				String.format("Playtime XP module has been enabled, will give %s xp every %s seconds", amount,
						interval));
	}

	public void disable() {
		plugin.getServer().getScheduler().cancelTasks(plugin);
	}

	public void playTimeXp() {
		plugin.debug("Checking for playtime XP");
		plugin.getServer().getOnlinePlayers().forEach(player -> {
			plugin.debug("Checking player " + player.getName());
			Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
			AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
			String discordPlayerId = accountLinkManager.getDiscordId(player.getUniqueId());
			if (discordPlayerId == null || discordPlayerId.isBlank()) {
				plugin.debug("Player " + player.getName() + " is not linked, not processing playtime XP");
				return;
			}
			plugin.api.addXp(mainGuild.getId(), discordPlayerId, plugin.getConfig().getInt("playtime-xp.amount"));
		});
	}

}
