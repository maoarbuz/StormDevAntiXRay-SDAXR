package mao.sdaxr.gui;

import mao.sdaxr.Sdaxr;
import mao.sdaxr.models.MiningSession;
import mao.sdaxr.models.OreType;
import mao.sdaxr.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SessionGUI {
    private final Sdaxr plugin;

    public SessionGUI() {
        this.plugin = Sdaxr.getInstance();
    }

    public void open(Player player, PlayerData targetPlayerData) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8Сессии игрока: " + targetPlayerData.getPlayerName());

        List<MiningSession> sessions = targetPlayerData.getSuspiciousSessions();

        for (int i = 0; i < Math.min(sessions.size(), 45); i++) {
            MiningSession session = sessions.get(i);
            gui.setItem(i, createSessionItem(session, i + 1));
        }

        gui.setItem(49, createInfoItem(targetPlayerData));

        player.openInventory(gui);
    }

    private ItemStack createSessionItem(MiningSession session, int sessionNumber) {
        Material material = Material.CLOCK;
        // Проверяем как обычные, так и глубинные редкие руды
        if (session.getOreCount(OreType.DIAMOND) > 0 || session.getOreCount(OreType.DEEPSLATE_DIAMOND) > 0) {
            material = Material.DIAMOND;
        } else if (session.getOreCount(OreType.EMERALD) > 0 || session.getOreCount(OreType.DEEPSLATE_EMERALD) > 0) {
            material = Material.EMERALD;
        } else if (session.getOreCount(OreType.GOLD) > 0 || session.getOreCount(OreType.DEEPSLATE_GOLD) > 0) {
            material = Material.GOLD_INGOT;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6Сессия #" + sessionNumber);

            List<String> lore = new ArrayList<>();
            lore.add("");

            lore.add("§fАлмазы: §b" + session.getOreCount(OreType.DIAMOND));
            lore.add("§fИзумруды: §a" + session.getOreCount(OreType.EMERALD));
            lore.add("§fЗолото: §6" + session.getOreCount(OreType.GOLD));
            lore.add("§fЖелезо: §7" + session.getOreCount(OreType.IRON));
            lore.add("§fМедь: §6" + session.getOreCount(OreType.COPPER));
            lore.add("§fУголь: §8" + session.getOreCount(OreType.COAL));
            lore.add("§fРедстоун: §c" + session.getOreCount(OreType.REDSTONE));
            lore.add("§fЛазурит: §9" + session.getOreCount(OreType.LAPIS));

            lore.add("§fГлубинные алмазы: §b" + session.getOreCount(OreType.DEEPSLATE_DIAMOND));
            lore.add("§fГлубинные изумруды: §a" + session.getOreCount(OreType.DEEPSLATE_EMERALD));
            lore.add("§fГлубинное золото: §6" + session.getOreCount(OreType.DEEPSLATE_GOLD));
            lore.add("§fГлубинное железо: §7" + session.getOreCount(OreType.DEEPSLATE_IRON));
            lore.add("§fГлубинная медь: §6" + session.getOreCount(OreType.DEEPSLATE_COPPER));
            lore.add("§fГлубинный уголь: §8" + session.getOreCount(OreType.DEEPSLATE_COAL));
            lore.add("§fГлубинный редстоун: §c" + session.getOreCount(OreType.DEEPSLATE_REDSTONE));
            lore.add("§fГлубинный лазурит: §9" + session.getOreCount(OreType.DEEPSLATE_LAPIS));

            lore.add("§fБулыжник: §8" + session.getOreCount(OreType.COBBLESTONE));
            lore.add("§fКамень: §7" + session.getOreCount(OreType.STONE));
            lore.add("§fГлубинный сланец: §8" + session.getOreCount(OreType.DEEPSLATE));
            lore.add("");
            lore.add("§7Всего блоков: §f" + session.getTotalOresMined());

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoItem(PlayerData playerData) {
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§aИнформация об игрокe");

            List<String> lore = new ArrayList<>();
            lore.add("§7Игрок: §f" + playerData.getPlayerName());
            lore.add("§7Подозрительных сессий: §e" + playerData.getTotalSuspiciousSessions());
            lore.add("");
            lore.add("§7Всего добыто блоков: §f" + playerData.getTotalOresMined());

            lore.add("§7Алмазов: §b" + playerData.getTotalOres(OreType.DIAMOND));
            lore.add("§7Изумрудов: §a" + playerData.getTotalOres(OreType.EMERALD));
            lore.add("§7Золота: §6" + playerData.getTotalOres(OreType.GOLD));
            lore.add("§7Железа: §7" + playerData.getTotalOres(OreType.IRON));
            lore.add("§7Меди: §6" + playerData.getTotalOres(OreType.COPPER));
            lore.add("§7Угля: §8" + playerData.getTotalOres(OreType.COAL));
            lore.add("§7Редстоуна: §c" + playerData.getTotalOres(OreType.REDSTONE));
            lore.add("§7Лазурита: §9" + playerData.getTotalOres(OreType.LAPIS));

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

            meta.setLore(lore);
            info.setItemMeta(meta);
        }

        return info;
    }
}