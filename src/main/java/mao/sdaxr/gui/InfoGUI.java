package mao.sdaxr.gui;

import mao.sdaxr.Sdaxr;
import mao.sdaxr.models.OreType;
import mao.sdaxr.models.PlayerData;
import mao.sdaxr.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class InfoGUI implements Listener {
    private final Sdaxr plugin;
    private final DataManager dataManager;

    public InfoGUI() {
        this.plugin = Sdaxr.getInstance();
        this.dataManager = plugin.getDataManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8SDAntiXRay - Подозрительные игроки");

        List<PlayerData> suspiciousPlayers = dataManager.getSuspiciousPlayers();

        for (int i = 0; i < Math.min(suspiciousPlayers.size(), 45); i++) {
            PlayerData playerData = suspiciousPlayers.get(i);
            gui.setItem(i, createPlayerHead(playerData));
        }

        gui.setItem(49, createInfoItem());

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8SDAntiXRay - Подозрительные игроки")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }

            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.getOwningPlayer() != null) {
                    PlayerData playerData = dataManager.getPlayerData(skullMeta.getOwningPlayer().getUniqueId());
                    if (playerData != null) {
                        new SessionGUI().open((Player) event.getWhoClicked(), playerData);
                    }
                }
            }
        }

        if (event.getView().getTitle().startsWith("§8Сессии игрока: ")) {
            event.setCancelled(true);
        }
    }

    private ItemStack createPlayerHead(PlayerData playerData) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6" + playerData.getPlayerName());
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerData.getPlayerId()));

            List<String> lore = new ArrayList<>();
            lore.add("§7Подозрительных сессий: §e" + playerData.getTotalSuspiciousSessions());
            lore.add("");

            lore.add("§fАлмазы: §b" + playerData.getTotalOres(OreType.DIAMOND));
            lore.add("§fИзумруды: §a" + playerData.getTotalOres(OreType.EMERALD));
            lore.add("§fЗолото: §6" + playerData.getTotalOres(OreType.GOLD));
            lore.add("§fЖелезо: §7" + playerData.getTotalOres(OreType.IRON));
            lore.add("§fМедь: §6" + playerData.getTotalOres(OreType.COPPER));
            lore.add("§fУголь: §8" + playerData.getTotalOres(OreType.COAL));
            lore.add("§fРедстоун: §c" + playerData.getTotalOres(OreType.REDSTONE));
            lore.add("§fЛазурит: §9" + playerData.getTotalOres(OreType.LAPIS));

            lore.add("§fГлубинные алмазы: §b" + playerData.getTotalOres(OreType.DEEPSLATE_DIAMOND));
            lore.add("§fГлубинные изумруды: §a" + playerData.getTotalOres(OreType.DEEPSLATE_EMERALD));
            lore.add("§fГлубинное золото: §6" + playerData.getTotalOres(OreType.DEEPSLATE_GOLD));
            lore.add("§fГлубинное железо: §7" + playerData.getTotalOres(OreType.DEEPSLATE_IRON));
            lore.add("§fГлубинная медь: §6" + playerData.getTotalOres(OreType.DEEPSLATE_COPPER));
            lore.add("§fГлубинный уголь: §8" + playerData.getTotalOres(OreType.DEEPSLATE_COAL));
            lore.add("§fГлубинный редстоун: §c" + playerData.getTotalOres(OreType.DEEPSLATE_REDSTONE));
            lore.add("§fГлубинный лазурит: §9" + playerData.getTotalOres(OreType.DEEPSLATE_LAPIS));

            lore.add("§fБулыжник: §8" + playerData.getTotalOres(OreType.COBBLESTONE));
            lore.add("§fКамень: §7" + playerData.getTotalOres(OreType.STONE));
            lore.add("§fГлубинный сланец: §8" + playerData.getTotalOres(OreType.DEEPSLATE));
            lore.add("");
            lore.add("§eНажмите для просмотра сессий");

            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }

    private ItemStack createInfoItem() {
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§aИнформация о системе");

            List<String> lore = new ArrayList<>();
            lore.add("§7Всего отслеживается игроков: §f" + dataManager.getPlayerDataCount());
            lore.add("§7Подозрительных игроков: §c" + dataManager.getSuspiciousPlayers().size());

            meta.setLore(lore);
            info.setItemMeta(meta);
        }

        return info;
    }
}