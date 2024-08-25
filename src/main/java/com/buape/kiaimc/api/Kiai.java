package com.buape.kiaimc.api;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

public class Kiai {
    private final Logger logger;
    private final Boolean debug;
    private final RequestQueueManager requestQueueManager;

    public Kiai(String token, Logger logger, Boolean debug) {
        this.logger = logger;
        this.debug = debug;
        this.requestQueueManager = new RequestQueueManager(logger, token, debug);
    }

    private void debug(String message) {
        if (this.debug) {
            this.logger.info(message);
        }
    }

    public void virtualMessage(String guildId, String channelId, Member guildMember) {
        HashMap<String, Object> channel = new HashMap<>();
        channel.put("id", channelId);

        HashMap<String, Object> member = new HashMap<>();
        member.put("id", guildMember.getId());

        HashMap<String, Object> role = new HashMap<>();
        guildMember.getRoles().forEach(r -> role.put("id", r.getId()));

        member.put("roles", new HashMap[] { role });

        HashMap<String, Object> guild = new HashMap<>();
        guild.put("id", guildId);

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("channel", channel);
        jsonMap.put("member", member);
        jsonMap.put("guild", guild);

        this.debug("Sending request with body from DiscordSRV data: " + jsonMap.toString());
        requestQueueManager.queueRequest("/guild/" + guildId + "/virtual_message", "POST", jsonMap);
    }

    public void addXp(String guildId, String userId, int amount) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("amount", amount);

        this.debug("Sending request with body: " + jsonMap.toString());
        requestQueueManager.queueRequest("/guild/" + guildId + "/user/" + userId + "/xp", "PATCH", jsonMap);
    }

    public void removeXp(String guildId, String userId, int amount, String reason) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("amount", amount);
        jsonMap.put("remove", true);

        this.debug("Sending request with body: " + jsonMap.toString());
        requestQueueManager.queueRequest("/guild/" + guildId + "/user/" + userId + "/xp", "PATCH", jsonMap);
    }

    public CompletableFuture<String> getUser(String guildId, String userId) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", userId);

        this.debug("Sending request with body: " + jsonMap.toString());
        return requestQueueManager.queueRequest("/guild/" + guildId + "/user/" + userId + "/xp", "GET", jsonMap);
    }
}