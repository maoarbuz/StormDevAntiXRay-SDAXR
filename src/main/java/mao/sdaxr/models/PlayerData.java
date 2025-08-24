package mao.sdaxr.models;

import mao.sdaxr.Sdaxr;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerData {
    private final UUID playerId;
    private final Map<OreType, Integer> totalOres;
    private final List<MiningSession> suspiciousSessions;
    private MiningSession currentSession;
    private String playerName;

    public PlayerData(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.totalOres = new EnumMap<>(OreType.class);
        this.suspiciousSessions = new CopyOnWriteArrayList<>();
        this.currentSession = null;

        for (OreType ore : OreType.values()) {
            totalOres.put(ore, 0);
        }
    }

    public void startNewSession() {
        currentSession = new MiningSession(playerId);
    }

    public void addOre(OreType oreType, int amount) {
        totalOres.put(oreType, totalOres.getOrDefault(oreType, 0) + amount);

        if (currentSession != null) {
            currentSession.addOre(oreType, amount);
        }
    }

    public void endCurrentSession() {
        currentSession = null;
    }

    public void checkSessionSuspicion(double threshold) {
        if (currentSession != null && currentSession.isSuspicious(threshold)) {
            currentSession.markAsSuspicious();
            if (!suspiciousSessions.contains(currentSession)) {
                suspiciousSessions.add(currentSession);

                if (Sdaxr.getInstance().getConfigManager().isDebug()) {
                    org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(playerId);
                    if (player != null) {
                        player.sendMessage("§7[DEBUG] Сессия помечена как подозрительная! Очки: " +
                                String.format("%.1f", currentSession.calculateSuspicionScore()));
                    }
                }
            }
        }
    }

    public double calculateXRayChance() {
        if (suspiciousSessions.isEmpty()) return 0.0;

        int totalSuspiciousOres = 0;
        int totalRareOres = 0;

        for (MiningSession session : suspiciousSessions) {
            totalSuspiciousOres += session.getTotalOresMined();
            totalRareOres += session.getOreCount(OreType.DIAMOND) + session.getOreCount(OreType.EMERALD) +
                    session.getOreCount(OreType.GOLD) + session.getOreCount(OreType.IRON) +
                    session.getOreCount(OreType.DEEPSLATE_DIAMOND) + session.getOreCount(OreType.DEEPSLATE_EMERALD) +
                    session.getOreCount(OreType.DEEPSLATE_GOLD) + session.getOreCount(OreType.DEEPSLATE_IRON);
        }

        if (totalSuspiciousOres == 0) return 0.0;

        double rareRatio = (double) totalRareOres / totalSuspiciousOres;
        double sessionFactor = suspiciousSessions.size() * 0.2;

        double ratioWeight = Sdaxr.getInstance().getConfigManager().getConfig().getDouble("xray-chance.ratio-weight", 70.0);
        double sessionWeight = Sdaxr.getInstance().getConfigManager().getConfig().getDouble("xray-chance.session-weight", 30.0);

        double chance = (rareRatio * ratioWeight) + (sessionFactor * sessionWeight);
        return Math.min(95.0, Math.max(0.0, chance));
    }

    public String getXRayChanceFormatted() {
        double chance = calculateXRayChance();
        return String.format("%.1f%%", chance);
    }

    public int getTotalOresMined() {
        return totalOres.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalOres(OreType oreType) {
        return totalOres.getOrDefault(oreType, 0);
    }

    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public List<MiningSession> getSuspiciousSessions() { return suspiciousSessions; }
    public int getTotalSuspiciousSessions() { return suspiciousSessions.size(); }
    public MiningSession getCurrentSession() { return currentSession; }
    public Map<OreType, Integer> getTotalOres() { return totalOres; }
}