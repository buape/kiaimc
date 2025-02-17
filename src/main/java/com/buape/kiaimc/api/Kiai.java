package com.buape.kiaimc.api;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

public class Kiai {
    private static final Gson gson = new Gson();
    private final Logger logger;
    private final Boolean debug;
    private final RequestQueueManager requestQueueManager;

    public Kiai(String token, Logger logger, Boolean debug, String baseUrl) {
        this.logger = logger;
        this.debug = debug;
        this.requestQueueManager = new RequestQueueManager(logger, token, debug, baseUrl);
    }

    public void debug(String message) {
        if (this.debug) {
            this.logger.info(message);
        }
    }

    public void virtualMessage(String guildId, String channelId, Member guildMember, String channelParentId) {
        HashMap<String, Object> channel = new HashMap<>();
        channel.put("id", channelId);
        channel.put("parentId", channelParentId);

        HashMap<String, Object> member = new HashMap<>();
        member.put("id", guildMember.getId());
        member.put("roleIds", guildMember.getRoles().stream().map(role -> role.getId()).toList());

        HashMap<String, Object> guild = new HashMap<>();
        guild.put("id", guildId);

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("channel", channel);
        jsonMap.put("member", member);
        jsonMap.put("guild", guild);

        requestQueueManager.queueRequest("/virtual_message", "POST", jsonMap);
    }

    public void addXp(String guildId, String userId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP amount cannot be negative");
        }
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("xp", amount);
        try {
            requestQueueManager.queueRequest(guildId + "/member/" + userId + "/xp", "PATCH", jsonMap);
        } catch (Exception e) {
            logger.warning("Failed to add XP: " + e.getMessage());
            throw e;
        }
    }
    public void removeXp(String guildId, String userId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP amount cannot be negative");
        }
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("xp", 0 - amount);
        try {
            requestQueueManager.queueRequest(guildId + "/member/" + userId + "/xp", "PATCH", jsonMap);
        } catch (Exception e) {
            logger.warning("Failed to remove XP: " + e.getMessage());
            throw e;
        }
    }

    public CompletableFuture<User> getUser(String guildId, String userId) {
        return requestQueueManager.queueRequest("/" + guildId + "/member/" + userId, "GET", null)
                .thenApply(response -> {
                    JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                    return gson.fromJson(jsonObject.get("data"), User.class);
                });
    }
}