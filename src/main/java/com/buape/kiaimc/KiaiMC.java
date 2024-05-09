package com.buape.kiaimc;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import com.buape.kiaimc.listeners.AsyncChatListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

public final class KiaiMC extends JavaPlugin {
    public final Logger logger = this.getLogger();

    public final int currentConfig = 1;
    private String token;

    @Override
    public void onEnable() {
        Boolean configIsValid = checkConfig();
        if (configIsValid) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();

            new Metrics(this, 18414);
            this.token = getConfig().getString("token");

            if (this.token.isBlank()) {
                logger.severe("No token was supplied for the Kiai API, stopping KiaiMC.");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            getServer().getPluginManager().registerEvents(new AsyncChatListener(this), this);
        }
    }

    public void triggerMessage(String guildId, String userId, String channelId) {
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();

        Member guildMember = mainGuild.getMemberById(userId);

        // Channel ID
        HashMap<String, Object> channel = new HashMap<>();
        channel.put("id", channelId);

        // Member ID
        HashMap<String, Object> member = new HashMap<>();
        member.put("id", userId);

        // Role list
        HashMap<String, Object> role = new HashMap<>();
        guildMember.getRoles().forEach(r -> role.put("id", r.getId()));

        // Add roles to member
        member.put("roles", new HashMap[] { role });

        // Guild ID
        HashMap<String, Object> guild = new HashMap<>();
        guild.put("id", guildId);

        // Combine them together
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("channel", channel);
        jsonMap.put("member", member);
        jsonMap.put("guild", guild);

        var objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper
                    .writeValueAsString(jsonMap);

            this.debug("Sending request with body from DiscordSRV data: " + requestBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.kiaibot.com/v1/guild/" + guildId + "/virtual_message"))
                    .header("Authorization", this.token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401 || response.statusCode() == 403) {
                this.logger.warning(
                        "Your API token is not authorized in guild " + mainGuild + " (ID received from DiscordSRV)");
            }

            this.debug(
                    "Response with DiscordSRV data: " + response.body() + " and status code " + response.statusCode());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        logger.info("KiaiMC has been disabled.");
        Bukkit.getScheduler().cancelTasks(this);
    }

    public Boolean checkConfig() {
        int configVersion = this.getConfig().getInt("config-version");
        if (configVersion != this.currentConfig) {
            File oldConfigTo = new File(this.getDataFolder(), "config-old-" + configVersion + ".yml");
            File old = new File(this.getDataFolder(), "config.yml");
            try {
                FileUtils.moveFile(old, oldConfigTo);
                getConfig().options().copyDefaults();
                saveDefaultConfig();
                this.logger.severe("Your config is outdated. Your old config has been moved to " + oldConfigTo.getName()
                        + ", and the new version has been applied in its place.");
            } catch (Exception e) {
                File newConfig = new File(this.getDataFolder(), "config-new.yml");
                InputStream newConfigData = this.getResource("config.yml");
                try {
                    FileUtils.copyInputStreamToFile(newConfigData, newConfig);
                    this.logger.severe(
                            "Your config is outdated, but I was unable to replace your old config. Instead, the new config has been saved to "
                                    + newConfig.getName() + ".");
                } catch (Exception e1) {
                    this.logger.severe(
                            "Your config is outdated, but I could not move your old config to a backup or copy in the new config format.");
                }

            }

            this.logger.severe(
                    "The plugin will now disable, please migrate the values from your old config to the new one.");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        } else {
            File newConfig = new File(this.getDataFolder(), "config-new.yml");
            if (newConfig.exists())
                FileUtils.deleteQuietly(newConfig);
        }
        return true;
    }

    public void debug(String message) {
        if (this.getConfig().getBoolean("debug")) {
            this.logger.info(message);
        }
    }

}