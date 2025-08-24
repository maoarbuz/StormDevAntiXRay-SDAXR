package mao.sdaxr.utils;

import mao.sdaxr.Sdaxr;
import mao.sdaxr.models.MiningSession;
import mao.sdaxr.models.OreType;
import mao.sdaxr.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DataManager {
    private final Sdaxr plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, Long> lastNotificationTime;
    private final File dataFolder;
    private final File sessionsFolder;

    public DataManager() {
        this.plugin = Sdaxr.getInstance();
        this.playerDataMap = new ConcurrentHashMap<>();
        this.lastNotificationTime = new ConcurrentHashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.sessionsFolder = new File(plugin.getDataFolder(), "sessions");

        if (!dataFolder.exists()) dataFolder.mkdirs();
        if (!sessionsFolder.exists()) sessionsFolder.mkdirs();

        loadAllData();
        startCleanupTask();
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, id -> {
            Player player = Bukkit.getPlayer(id);
            String name = player != null ? player.getName() : "Unknown";
            PlayerData data = new PlayerData(id, name);
            loadPlayerSessions(data);
            return data;
        });
    }

    public PlayerData getPlayerData(String playerName) {
        for (PlayerData data : playerDataMap.values()) {
            if (data.getPlayerName().equalsIgnoreCase(playerName)) {
                return data;
            }
        }
        return null;
    }

    public void savePlayerData(UUID playerId) {
        PlayerData data = playerDataMap.get(playerId);
        if (data == null) return;

        File playerFile = new File(dataFolder, playerId.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("player-name", data.getPlayerName());
        config.set("last-update", System.currentTimeMillis());

        for (OreType ore : OreType.values()) {
            config.set("ores." + ore.name().toLowerCase(), data.getTotalOres(ore));
        }

        config.set("total-suspicious-sessions", data.getTotalSuspiciousSessions());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Не удалось сохранить данные игрока " + playerId, e);
        }

        saveSuspiciousSessions(data);
    }

    private void saveSuspiciousSessions(PlayerData data) {
        File playerSessionsFolder = new File(sessionsFolder, data.getPlayerId().toString());
        if (!playerSessionsFolder.exists()) playerSessionsFolder.mkdirs();

        int sessionNumber = 0;
        for (MiningSession session : data.getSuspiciousSessions()) {
            if (!session.isSuspicious()) continue;

            File sessionFile = new File(playerSessionsFolder, "suspicious_session_" + sessionNumber + ".yml");
            YamlConfiguration sessionConfig = new YamlConfiguration();

            sessionConfig.set("start-time", session.getStartTime());
            sessionConfig.set("duration", session.getDuration());
            sessionConfig.set("suspicion-score", session.calculateSuspicionScore());

            for (OreType ore : OreType.values()) {
                sessionConfig.set("ores." + ore.name().toLowerCase(), session.getOreCount(ore));
            }

            try {
                sessionConfig.save(sessionFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Не удалось сохранить сессию игрока " + data.getPlayerId(), e);
            }

            sessionNumber++;
        }

        File[] oldSessionFiles = playerSessionsFolder.listFiles((dir, name) ->
                name.startsWith("suspicious_session_") && name.endsWith(".yml"));

        if (oldSessionFiles != null) {
            for (File oldFile : oldSessionFiles) {
                int fileNumber = Integer.parseInt(oldFile.getName().replace("suspicious_session_", "").replace(".yml", ""));
                if (fileNumber >= data.getSuspiciousSessions().size()) {
                    oldFile.delete();
                }
            }
        }
    }

    private void loadPlayerSessions(PlayerData data) {
        File playerSessionsFolder = new File(sessionsFolder, data.getPlayerId().toString());
        if (!playerSessionsFolder.exists()) return;

        File[] sessionFiles = playerSessionsFolder.listFiles((dir, name) ->
                name.startsWith("suspicious_session_") && name.endsWith(".yml"));

        if (sessionFiles == null) return;

        Arrays.sort(sessionFiles, (f1, f2) -> {
            int num1 = Integer.parseInt(f1.getName().replace("suspicious_session_", "").replace(".yml", ""));
            int num2 = Integer.parseInt(f2.getName().replace("suspicious_session_", "").replace(".yml", ""));
            return Integer.compare(num1, num2);
        });

        for (File sessionFile : sessionFiles) {
            YamlConfiguration sessionConfig = YamlConfiguration.loadConfiguration(sessionFile);

            MiningSession session = new MiningSession(data.getPlayerId());
            session.markAsSuspicious();

            for (OreType ore : OreType.values()) {
                int count = sessionConfig.getInt("ores." + ore.name().toLowerCase(), 0);
                session.addOre(ore, count);
            }

            data.getSuspiciousSessions().add(session);
        }
    }

    public void loadAllData() {
        File[] playerFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles == null) return;

        for (File file : playerFiles) {
            try {
                String fileName = file.getName().replace(".yml", "");
                UUID playerId = UUID.fromString(fileName);

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String playerName = config.getString("player-name", "Unknown");

                PlayerData data = new PlayerData(playerId, playerName);

                for (OreType ore : OreType.values()) {
                    int count = config.getInt("ores." + ore.name().toLowerCase(), 0);
                    data.getTotalOres().put(ore, count);
                }

                loadPlayerSessions(data);

                playerDataMap.put(playerId, data);

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неверный формат файла данных: " + file.getName());
            }
        }
    }

    public void saveAllData() {
        for (UUID playerId : playerDataMap.keySet()) {
            savePlayerData(playerId);
        }
    }

    public boolean canSendNotification(UUID playerId) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastNotificationTime.getOrDefault(playerId, 0L);
        int cooldown = plugin.getConfigManager().getNotificationCooldown() * 1000;

        if (currentTime - lastTime >= cooldown) {
            lastNotificationTime.put(playerId, currentTime);
            return true;
        }
        return false;
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            lastNotificationTime.entrySet().removeIf(entry -> {
                int cooldown = plugin.getConfigManager().getNotificationCooldown() * 1000 * 2;
                return System.currentTimeMillis() - entry.getValue() > cooldown;
            });
        }, 20L * 60, 20L * 60);
    }

    public Collection<PlayerData> getAllPlayerData() {
        return playerDataMap.values();
    }

    public int getPlayerDataCount() {
        return playerDataMap.size();
    }

    public List<PlayerData> getSuspiciousPlayers() {
        List<PlayerData> suspicious = new ArrayList<>();
        for (PlayerData data : playerDataMap.values()) {
            if (data.getTotalSuspiciousSessions() > 0) {
                suspicious.add(data);
            }
        }
        suspicious.sort((d1, d2) -> Double.compare(d2.calculateXRayChance(), d1.calculateXRayChance()));
        return suspicious;
    }
}