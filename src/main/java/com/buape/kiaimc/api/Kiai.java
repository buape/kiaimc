package com.buape.kiaimc.api;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;

public class Kiai {
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

    public void virtualMessage(String guildId, String channelId, Member guildMember, String channelParentId,
            String messageId, String content) {
        // Defensive input validation
        if (guildMember == null) {
            logger.severe("Kiai.virtualMessage: guildMember is null");
            throw new IllegalArgumentException("guildMember cannot be null");
        }
        if (guildId == null || guildId.isEmpty()) {
            logger.severe("Kiai.virtualMessage: guildId is null or empty");
            throw new IllegalArgumentException("guildId cannot be null or empty");
        }
        if (channelId == null || channelId.isEmpty()) {
            logger.severe("Kiai.virtualMessage: channelId is null or empty");
            throw new IllegalArgumentException("channelId cannot be null or empty");
        }
        if (messageId == null || messageId.isEmpty()) {
            logger.severe("Kiai.virtualMessage: messageId is null or empty");
            throw new IllegalArgumentException("messageId cannot be null or empty");
        }
        if (content == null || content.isEmpty()) {
            logger.severe("Kiai.virtualMessage: content is null or empty");
            throw new IllegalArgumentException("content cannot be null or empty");
        }
        // channelParentId is optional, but log if missing
        if (channelParentId == null || channelParentId.isEmpty()) {
            logger.warning("Kiai.virtualMessage: channelParentId is null or empty");
        }

        HashMap<String, Object> channel = new HashMap<>();
        channel.put("id", channelId);
        channel.put("parentId", channelParentId);

        HashMap<String, Object> member = new HashMap<>();
        member.put("id", guildMember.getId());
        java.util.List<String> roleIds = new java.util.ArrayList<>();
        java.util.List<Role> roles = guildMember.getRoles();
        if (roles != null) {
            roles.forEach(r -> roleIds.add(r.getId()));
        }
        member.put("roleIds", roleIds);

        HashMap<String, Object> guild = new HashMap<>();
        guild.put("id", guildId);

        HashMap<String, Object> message = new HashMap<>();
        message.put("id", messageId);
        message.put("content", content);

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("channel", channel);
        jsonMap.put("member", member);
        jsonMap.put("guild", guild);
        jsonMap.put("message", message);

        requestQueueManager.queueRequest("/virtual_message", "POST", jsonMap);
    }

    public void addXp(String guildId, String userId, int amount) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("xp", amount);
        requestQueueManager.queueRequest("/" + guildId + "/member/" + userId + "/xp", "PATCH", jsonMap);
    }

    public void removeXp(String guildId, String userId, int amount) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("xp", 0 - amount);
        requestQueueManager.queueRequest("/" + guildId + "/member/" + userId + "/xp", "PATCH", jsonMap);
    }

    public CompletableFuture<KiaiUser> getUser(String guildId, String userId) {
        CompletableFuture<String> responseFuture = requestQueueManager.queueRequest("/" + guildId + "/member/" + userId,
                "GET", null);
        CompletableFuture<KiaiUser> userFuture = new CompletableFuture<>();
        responseFuture.thenAccept(response -> {
            try {
                com.google.gson.JsonObject obj = new com.google.gson.JsonParser().parse(response).getAsJsonObject();
                com.google.gson.JsonObject data = obj.getAsJsonObject("data");
                KiaiUser user = new com.google.gson.Gson().fromJson(data, KiaiUser.class);
                userFuture.complete(user);
            } catch (Exception e) {
                userFuture.completeExceptionally(e);
            }
        });
        return userFuture;
    }
}