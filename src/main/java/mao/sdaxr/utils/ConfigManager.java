package mao.sdaxr.utils;

import mao.sdaxr.Sdaxr;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final Sdaxr plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager() {
        this.plugin = Sdaxr.getInstance();
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.getLogger().info("Конфигурационный файл создан!");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось сохранить конфигурацию:", e);
        }
    }

    private void setDefaults() {
        config.addDefault("suspicion-threshold", 20.0);
        config.addDefault("session-duration", 300);
        config.addDefault("notification-cooldown", 30);
        config.addDefault("debug", false);
        config.addDefault("balance.min-rare-ores", 2);
        config.addDefault("balance.max-common-blocks", 128);
        config.addDefault("balance.ratio-multiplier", 3.0);

        config.addDefault("xray-chance.ratio-weight", 70.0);
        config.addDefault("xray-chance.session-weight", 30.0);

        config.addDefault("multipliers.diamond", 15.0);
        config.addDefault("multipliers.emerald", 12.0);
        config.addDefault("multipliers.deepslate_diamond", 14.5);
        config.addDefault("multipliers.deepslate_emerald", 11.5);
        config.addDefault("multipliers.deepslate_gold", 4.8);
        config.addDefault("multipliers.deepslate_iron", 2.9);
        config.addDefault("multipliers.deepslate_coal", 0.95);
        config.addDefault("multipliers.deepslate_redstone", 0.75);
        config.addDefault("multipliers.deepslate_lapis", 0.75);
        config.addDefault("multipliers.deepslate_copper", 0.5);
        config.addDefault("multipliers.gold", 5.0);
        config.addDefault("multipliers.iron", 3.0);
        config.addDefault("multipliers.copper", 0.5);
        config.addDefault("multipliers.coal", 1.0);
        config.addDefault("multipliers.redstone", 0.8);
        config.addDefault("multipliers.lapis", 0.8);
        config.addDefault("multipliers.cobblestone", -0.2);
        config.addDefault("multipliers.stone", -0.1);
        config.addDefault("multipliers.deepslate", -0.15);
        config.addDefault("multipliers.dirt", -0.05);
        config.addDefault("multipliers.sand", -0.05);
        config.addDefault("multipliers.gravel", -0.05);
        config.addDefault("multipliers.netherrack", -0.03);
        config.addDefault("multipliers.end_stone", -0.03);

        config.addDefault("messages.suspicion-alert", "&c⚠ Подозрение на XRay: &6{player} &7добыл подозрительное количество руд!");
        config.addDefault("messages.no-permission", "&cУ вас нет прав для использования этой команды!");
        config.addDefault("messages.reload-success", "&aКонфигурация перезагружена!");

        config.options().copyDefaults(true);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        setDefaults();

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось сохранить конфигурацию:", e);
        }
    }

    public double getSuspicionThreshold() {
        return config.getDouble("suspicion-threshold");
    }

    public int getSessionDuration() {
        return config.getInt("session-duration");
    }

    public int getNotificationCooldown() {
        return config.getInt("notification-cooldown");
    }

    public boolean isDebug() {
        return config.getBoolean("debug");
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Сообщение не найдено: " + path)
                .replace('&', '§');
    }

    public FileConfiguration getConfig() {
        return config;
    }
}