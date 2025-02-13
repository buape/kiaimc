package com.buape.kiaimc.api;

public class User {
	private String id;
	private String guildId;
	private int currentLevel;
	private int nextLevel;
	private int nextLevelXp;
	private int xp;
	private int messagesSent;
	private int voiceMinutes;
	private String rankCardBackground;
	private int currentXpStreak;
	private boolean streakDoneToday;

	public String getId() {
		return id;
	}

	public String getGuildId() {
		return guildId;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public int getNextLevel() {
		return nextLevel;
	}

	public int getNextLevelXp() {
		return nextLevelXp;
	}

	public int getXp() {
		return xp;
	}

	public int getMessagesSent() {
		return messagesSent;
	}

	public int getVoiceMinutes() {
		return voiceMinutes;
	}

	public String getRankCardBackground() {
		return rankCardBackground;
	}

	public int getCurrentXpStreak() {
		return currentXpStreak;
	}

	public boolean isStreakDoneToday() {
		return streakDoneToday;
	}
}