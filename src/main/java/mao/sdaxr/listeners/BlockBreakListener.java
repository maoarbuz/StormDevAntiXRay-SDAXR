package mao.sdaxr.listeners;

import mao.sdaxr.Sdaxr;
import mao.sdaxr.models.OreType;
import mao.sdaxr.models.PlayerData;
import mao.sdaxr.utils.DataManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;

public class BlockBreakListener implements Listener {
    private final Sdaxr plugin;
    private final DataManager dataManager;
    private final List<Material> trackedOres = Arrays.asList(
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.GOLD_ORE,
            Material.IRON_ORE,
            Material.COPPER_ORE,
            Material.COAL_ORE,
            Material.REDSTONE_ORE,
            Material.LAPIS_ORE,

            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_LAPIS_ORE,

            Material.COBBLESTONE,
            Material.STONE,
            Material.DEEPSLATE,
            Material.DIRT,
            Material.SAND,
            Material.GRAVEL,
            Material.NETHERRACK,
            Material.END_STONE
    );

    public BlockBreakListener() {
        this.plugin = Sdaxr.getInstance();
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!trackedOres.contains(event.getBlock().getType())) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        OreType oreType = OreType.fromMaterial(event.getBlock().getType());

        if (oreType == null) {
            return;
        }

        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
        playerData.setPlayerName(player.getName());

        if (playerData.getCurrentSession() == null) {
            playerData.startNewSession();

            if (plugin.getConfigManager().isDebug()) {
                player.sendMessage("§7[DEBUG] Начата новая сессия добычи");
            }
        }

        playerData.addOre(oreType, 1);

        if (plugin.getConfigManager().isDebug()) {
            player.sendMessage("§7[DEBUG] Сломано: " + oreType.getDisplayName());
        }

        playerData.checkSessionSuspicion(plugin.getConfigManager().getSuspicionThreshold());

        if (playerData.getCurrentSession() != null &&
                playerData.getCurrentSession().isSuspicious() &&
                dataManager.canSendNotification(player.getUniqueId())) {

            sendSuspicionAlert(playerData);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());

        if (playerData != null && playerData.getCurrentSession() != null) {
            playerData.checkSessionSuspicion(plugin.getConfigManager().getSuspicionThreshold());
            playerData.endCurrentSession();
            dataManager.savePlayerData(player.getUniqueId());
        }
    }

    private void sendSuspicionAlert(PlayerData playerData) {
        String message = plugin.getConfigManager().getMessage("suspicion-alert")
                .replace("{player}", playerData.getPlayerName());

        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("sdaxr.messages")) {
                onlinePlayer.sendMessage(message);

                if (onlinePlayer.hasPermission("sdaxr.admin")) {
                    onlinePlayer.sendMessage("§7Больше информации в /sdaxr info");
                }
            }
        }

        plugin.getLogger().warning("Подозрительная сессия у игрока: " + playerData.getPlayerName() +
                " | Очки: " + String.format("%.1f", playerData.getCurrentSession().calculateSuspicionScore()));
    }
}