package mao.sdaxr.models;

import mao.sdaxr.Sdaxr;
import org.bukkit.Material;

public enum OreType {
    DIAMOND("diamond", "Алмаз", Material.DIAMOND_ORE, Material.DIAMOND),
    EMERALD("emerald", "Изумруд", Material.EMERALD_ORE, Material.EMERALD),

    DEEPSLATE_DIAMOND("deepslate_diamond", "Глубинный алмаз", Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND),
    DEEPSLATE_EMERALD("deepslate_emerald", "Глубинный изумруд", Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD),
    DEEPSLATE_GOLD("deepslate_gold", "Глубинное золото", Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
    DEEPSLATE_IRON("deepslate_iron", "Глубинное железо", Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
    DEEPSLATE_COAL("deepslate_coal", "Глубинный уголь", Material.DEEPSLATE_COAL_ORE, Material.COAL),
    DEEPSLATE_REDSTONE("deepslate_redstone", "Глубинный редстоун", Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE),
    DEEPSLATE_LAPIS("deepslate_lapis", "Глубинный лазурит", Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_LAZULI),
    DEEPSLATE_COPPER("deepslate_copper", "Глубинная медь", Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),

    GOLD("gold", "Золото", Material.GOLD_ORE, Material.GOLD_INGOT),
    IRON("iron", "Железо", Material.IRON_ORE, Material.IRON_INGOT),
    COPPER("copper", "Медь", Material.COPPER_ORE, Material.COPPER_INGOT),

    COAL("coal", "Уголь", Material.COAL_ORE, Material.COAL),
    REDSTONE("redstone", "Редстоун", Material.REDSTONE_ORE, Material.REDSTONE),
    LAPIS("lapis", "Лазурит", Material.LAPIS_ORE, Material.LAPIS_LAZULI),

    COBBLESTONE("cobblestone", "Булыжник", Material.COBBLESTONE, Material.COBBLESTONE),
    STONE("stone", "Камень", Material.STONE, Material.STONE),
    DEEPSLATE("deepslate", "Глубинный сланец", Material.DEEPSLATE, Material.DEEPSLATE),
    DIRT("dirt", "Земля", Material.DIRT, Material.DIRT),
    SAND("sand", "Песок", Material.SAND, Material.SAND),
    GRAVEL("gravel", "Гравий", Material.GRAVEL, Material.GRAVEL),
    NETHERRACK("netherrack", "Незеррак", Material.NETHERRACK, Material.NETHERRACK),
    END_STONE("end_stone", "Эндстоун", Material.END_STONE, Material.END_STONE);

    private final String configKey;
    private final String displayName;
    private final Material oreMaterial;
    private final Material itemMaterial;

    OreType(String configKey, String displayName, Material oreMaterial, Material itemMaterial) {
        this.configKey = configKey;
        this.displayName = displayName;
        this.oreMaterial = oreMaterial;
        this.itemMaterial = itemMaterial;
    }

    public double getBaseMultiplier() {
        return Sdaxr.getInstance().getConfigManager().getConfig().getDouble("multipliers." + configKey, getDefaultMultiplier());
    }

    private double getDefaultMultiplier() {
        switch (this) {
            case DIAMOND: return 15.0;
            case EMERALD: return 12.0;
            case DEEPSLATE_DIAMOND: return 14.5;
            case DEEPSLATE_EMERALD: return 11.5;
            case DEEPSLATE_GOLD: return 4.8;
            case DEEPSLATE_IRON: return 2.9;
            case DEEPSLATE_COAL: return 0.95;
            case DEEPSLATE_REDSTONE: return 0.75;
            case DEEPSLATE_LAPIS: return 0.75;
            case DEEPSLATE_COPPER: return 0.5;
            case GOLD: return 5.0;
            case IRON: return 3.0;
            case COPPER: return 0.5;
            case COAL: return 1.0;
            case REDSTONE: return 0.8;
            case LAPIS: return 0.8;
            case COBBLESTONE: return -0.2;
            case STONE: return -0.1;
            case DEEPSLATE: return -0.15;
            case DIRT: return -0.05;
            case SAND: return -0.05;
            case GRAVEL: return -0.05;
            case NETHERRACK: return -0.03;
            case END_STONE: return -0.03;
            default: return 0.0;
        }
    }

    public String getDisplayName() { return displayName; }
    public Material getOreMaterial() { return oreMaterial; }
    public Material getItemMaterial() { return itemMaterial; }

    public static OreType fromMaterial(Material material) {
        for (OreType ore : values()) {
            if (ore.getOreMaterial() == material) {
                return ore;
            }
        }
        return null;
    }

    public boolean isCommonBlock() {
        return getBaseMultiplier() < 0;
    }

    public boolean isRareOre() {
        return getBaseMultiplier() >= 5.0;
    }

    public boolean isDeepslateOre() {
        return this.name().startsWith("DEEPSLATE_");
    }
}