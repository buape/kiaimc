package com.buape.kiaimc.modules;

import com.buape.kiaimc.KiaiMC;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

public class PlayTimeModule {
	private final KiaiMC plugin;

	public PlayTimeModule(KiaiMC plugin) {
		this.plugin = plugin;
	}

	public void onEnable() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::playTimeXp,
				plugin.getConfig().getInt("bonus-message.interval") * 20,
				plugin.getConfig().getInt("bonus-message.interval") * 20);
	}

	public void onDisable() {
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
