package com.buape.kiaimc;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.buape.kiaimc.api.Kiai;
import com.buape.kiaimc.modules.BonusMessageModule;
import com.buape.kiaimc.modules.ChatModule;

public final class KiaiMC extends JavaPlugin {
    public final Logger logger = this.getLogger();

    public final int currentConfig = 2;
    public Kiai api;

    @Override
    public void onEnable() {
        Boolean configIsValid = checkConfig();
        if (configIsValid) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();

            new Metrics(this, 18414);
            String token = getConfig().getString("token");

            if (token.isBlank()) {
                logger.severe("No token was supplied for the Kiai API, stopping KiaiMC.");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            this.api = new Kiai(token, this.logger, this.getConfig().getBoolean("debug"));

            getServer().getPluginManager().registerEvents(new ChatModule(this), this);
            getServer().getPluginManager().registerEvents(new BonusMessageModule(this), this);
            getServer().getPluginManager().registerEvents(new ChatModule(this), this);

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