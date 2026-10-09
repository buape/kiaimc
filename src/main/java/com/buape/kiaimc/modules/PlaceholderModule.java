package com.buape.kiaimc.modules;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.OfflinePlayer;

import com.buape.kiaimc.KiaiMC;
import com.buape.kiaimc.api.KiaiUser;

import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderModule extends PlaceholderExpansion {
    private static final long CACHE_TTL_MS = TimeUnit.SECONDS.toMillis(30);

    private final KiaiMC plugin;
    private final Map<UUID, CachedUser> cache = new ConcurrentHashMap<>();

    public PlaceholderModule(KiaiMC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "kiaimc";
    }

    @Override
    public String getAuthor() {
        return "Buape Studios";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || params == null) {
            return "";
        }

        KiaiUser user = getCachedUser(player);
        if (user == null) {
            return "";
        }

        return switch (params.toLowerCase()) {
            case "xp" -> formatInteger(user.xp);
            case "current_level", "level" -> formatInteger(user.currentLevel);
            case "next_level" -> formatInteger(user.nextLevel);
            case "next_level_xp" -> formatInteger(user.nextLevelXp);
            case "messages", "messages_sent" -> formatInteger(user.messagesSent);
            case "voice", "voice_minutes" -> formatInteger(user.voiceMinutes);
            case "rank_card_background" -> user.rankCardBackground == null ? "" : user.rankCardBackground;
            case "current_xp_streak", "xp_streak" -> formatInteger(user.currentXpStreak);
            case "streak_done_today" -> user.streakDoneToday == null ? "false" : user.streakDoneToday.toString();
            default -> null;
        };
    }

    private KiaiUser getCachedUser(OfflinePlayer player) {
        CachedUser cached = cache.get(player.getUniqueId());
        long now = System.currentTimeMillis();
        if (cached == null || now - cached.updatedAt > CACHE_TTL_MS) {
            refreshUser(player);
        }
        return cached == null ? null : cached.user;
    }

    private void refreshUser(OfflinePlayer player) {
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
        if (discordId == null || discordId.isBlank()) {
            cache.remove(player.getUniqueId());
            return;
        }

        String guildId = DiscordSRV.getPlugin().getMainGuild().getId();
        plugin.api.getUser(guildId, discordId).thenAccept(user -> {
            cache.put(player.getUniqueId(), new CachedUser(user, System.currentTimeMillis()));
        }).exceptionally(error -> {
            plugin.debug("Unable to refresh PlaceholderAPI data for " + player.getName() + ": " + error.getMessage());
            return null;
        });
    }

    private String formatInteger(Integer value) {
        return value == null ? "0" : value.toString();
    }

    private record CachedUser(KiaiUser user, long updatedAt) {
    }
}
