package mao.sdaxr;

import mao.sdaxr.commands.CommandManager;
import mao.sdaxr.listeners.BlockBreakListener;
import mao.sdaxr.utils.ConfigManager;
import mao.sdaxr.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Sdaxr extends JavaPlugin {
    private static Sdaxr instance;
    private ConfigManager configManager;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            configManager = new ConfigManager();
            dataManager = new DataManager();

            registerCommands();
            registerListeners();
            startSessionCheckTask();

            getLogger().info("\n" +
                    "░SSSSSS╗DDDDDD╗░░AAAAA╗░XX╗░░XX╗RRRRRR╗░\n" +
                    "SS╔════╝DD╔══DD╗AA╔══AA╗╚XX╗XX╔╝RR╔══RR╗\n" +
                    "╚SSSSS╗░DD║░░DD║AAAAAAA║░╚XXX╔╝░RRRRRR╔╝\n" +
                    "░╚═══SS╗DD║░░DD║AA╔══AA║░XX╔XX╗░RR╔══RR╗\n" +
                    "SSSSSS╔╝DDDDDD╔╝AA║░░AA║XX╔╝╚XX╗RR║░░RR║\n" +
                    "╚═════╝░╚═════╝░╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░╚═╝");
            getLogger().info("Загружено данных игроков: " + dataManager.getPlayerDataCount());

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Ошибка при запуске плагина:", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        getLogger().info("SDAntiXRay отключен!");
    }

    private void registerCommands() {
        getCommand("sdaxr").setExecutor(new CommandManager());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    private void startSessionCheckTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                mao.sdaxr.models.PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
                if (playerData != null && playerData.getCurrentSession() != null) {
                    long sessionDuration = configManager.getSessionDuration() * 1000L;
                    if (playerData.getCurrentSession().getDuration() > sessionDuration) {
                        if (instance.getConfigManager().isDebug()) {
                            player.sendMessage("§7[DEBUG] Сессия завершена по времени");
                        }
                        playerData.endCurrentSession();
                    }
                }
            }
        }, 20L, 20L);
    }

    public static Sdaxr getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public DataManager getDataManager() { return dataManager; }
}