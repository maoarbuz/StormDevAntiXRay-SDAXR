package mao.sdaxr.models;

import mao.sdaxr.Sdaxr;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MiningSession {
    private final UUID playerId;
    private final long startTime;
    private final Map<OreType, Integer> oresMined;
    private boolean isSuspicious;

    public MiningSession(UUID playerId) {
        this.playerId = playerId;
        this.startTime = System.currentTimeMillis();
        this.oresMined = new EnumMap<>(OreType.class);
        this.isSuspicious = false;

        for (OreType ore : OreType.values()) {
            oresMined.put(ore, 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiningSession that = (MiningSession) o;
        return startTime == that.startTime && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, startTime);
    }

    public void addOre(OreType oreType, int amount) {
        oresMined.put(oreType, oresMined.getOrDefault(oreType, 0) + amount);
    }

    public int getOreCount(OreType oreType) {
        return oresMined.getOrDefault(oreType, 0);
    }

    public int getTotalOresMined() {
        return oresMined.values().stream().mapToInt(Integer::intValue).sum();
    }

    public double calculateSuspicionScore() {
        double score = 0;
        int totalRareOres = 0;
        int totalCommonBlocks = 0;

        for (OreType ore : OreType.values()) {
            int count = oresMined.get(ore);

            if (ore.isRareOre()) {
                totalRareOres += count;
                score += count * ore.getBaseMultiplier();
            } else if (ore.isCommonBlock()) {
                totalCommonBlocks += count;
                score += count * ore.getBaseMultiplier();
            } else {
                score += count * ore.getBaseMultiplier();
            }
        }

        if (totalRareOres > 0) {
            double ratio = (double) totalRareOres / Math.max(1, totalCommonBlocks);
            double ratioBonus = ratio * Sdaxr.getInstance().getConfigManager().getConfig().getDouble("balance.ratio-multiplier", 3.0);
            score += ratioBonus;

            if (Sdaxr.getInstance().getConfigManager().isDebug()) {
                org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.sendMessage("§7[DEBUG] Соотношение редкие/обычные: " + String.format("%.2f", ratio) +
                            " | Бонус: " + String.format("%.1f", ratioBonus));
                }
            }
        }

        int maxCommonBlocks = Sdaxr.getInstance().getConfigManager().getConfig().getInt("balance.max-common-blocks", 128);
        if (totalCommonBlocks > maxCommonBlocks) {
            score += (maxCommonBlocks - totalCommonBlocks) * 0.1;
        }

        return Math.max(0, score);
    }

    public boolean isSuspicious(double threshold) {
        int minRareOres = Sdaxr.getInstance().getConfigManager().getConfig().getInt("balance.min-rare-ores", 2);

        int totalRareOres = getOreCount(OreType.DIAMOND) + getOreCount(OreType.EMERALD) +
                getOreCount(OreType.GOLD) + getOreCount(OreType.IRON) +
                getOreCount(OreType.DEEPSLATE_DIAMOND) + getOreCount(OreType.DEEPSLATE_EMERALD) +
                getOreCount(OreType.DEEPSLATE_GOLD) + getOreCount(OreType.DEEPSLATE_IRON);

        if (totalRareOres < minRareOres) {
            return false;
        }

        return calculateSuspicionScore() > threshold;
    }

    public void markAsSuspicious() {
        this.isSuspicious = true;
    }

    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }

    public String getDurationFormatted() {
        long seconds = getDuration() / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%dм %dс", minutes, seconds);
    }

    public UUID getPlayerId() { return playerId; }
    public long getStartTime() { return startTime; }
    public Map<OreType, Integer> getOresMined() { return oresMined; }
    public boolean isSuspicious() { return isSuspicious; }
}