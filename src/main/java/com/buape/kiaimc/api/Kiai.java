package com.buape.kiaimc.api;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

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

    public void virtualMessage(String guildId, String channelId, Member guildMember, String channelParentId, String messageId, String content) {
        HashMap<String, Object> channel = new HashMap<>();
        channel.put("id", channelId);
        channel.put("parentId", channelParentId);

        HashMap<String, Object> member = new HashMap<>();
        member.put("id", guildMember.getId());
        java.util.List<String> roleIds = new java.util.ArrayList<>();
        guildMember.getRoles().forEach(r -> roleIds.add(r.getId()));
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
        CompletableFuture<String> responseFuture = requestQueueManager.queueRequest("/" + guildId + "/member/" + userId, "GET", null);
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