package lmp.customRecipes;

import lmp.constants.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

public class CustomRecipes {

    public static void addCustomRecipes() {
        addRottenFleshToLeatherSmelt();
        addSculkSensorRecipe();
        addBundleRecipe();
        addLatchAppleRecipe();
        addExperienceStorageBottleRecipe();
        addHastePotion();
        add33IronRepairRecipe();
        add66IronRepairRecipe();
        add100IronRepairRecipe();
        add33DiamondRepairRecipe();
        add66DiamondRepairRecipe();
        add100DiamondRepairRecipe();
        add25NetheriteRepairRecipe();
        add50NetheriteRepairRecipe();
        add100NetheriteRepairRecipe();
    }

    public static void addRottenFleshToLeatherSmelt() {
        ItemStack leather = new ItemStack(Material.LEATHER);
        NamespacedKey leatherKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "LEATHER");
        float experience = 0.35f;
        int cookingTime = 200;
        FurnaceRecipe leatherRecipe = new FurnaceRecipe(leatherKey, leather, Material.ROTTEN_FLESH, experience, cookingTime);
        Bukkit.addRecipe(leatherRecipe);
    }

    public static void addSculkSensorRecipe() {
        ItemStack sculkSensor = new ItemStack(Material.SCULK_SENSOR);
        NamespacedKey sculkSensorKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "SCULK_SENSOR");
        ShapedRecipe sculkSensorRecipe = new ShapedRecipe(sculkSensorKey, sculkSensor);
        sculkSensorRecipe.shape("ECE", "EIE", "OOO");
        sculkSensorRecipe.setIngredient('E', Material.ENDER_EYE);
        sculkSensorRecipe.setIngredient('C', Material.END_CRYSTAL);
        sculkSensorRecipe.setIngredient('O', Material.OBSIDIAN);
        sculkSensorRecipe.setIngredient('I', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(sculkSensorRecipe);
    }

    public static void addBundleRecipe() {
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        NamespacedKey bundleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "BUNDLE");
        ShapedRecipe bundleRecipe = new ShapedRecipe(bundleKey, bundle);
        bundleRecipe.shape("SRS", "RXR", "RRR");
        bundleRecipe.setIngredient('S', Material.STRING);
        bundleRecipe.setIngredient('R', Material.RABBIT_HIDE);
        bundleRecipe.setIngredient('X', Material.AIR);
        Bukkit.addRecipe(bundleRecipe);
    }

    public static void addHastePotion() {
        ItemStack hastePotion = new ItemStack(Material.POTION);
        NamespacedKey hastePotionKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "POTION");
        PotionMeta meta = (PotionMeta) hastePotion.getItemMeta();
        assert meta != null;
        meta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 10), true);
        hastePotion.setItemMeta(meta);
        ShapedRecipe hastePotionRecipe = new ShapedRecipe(hastePotionKey, hastePotion);
        hastePotionRecipe.shape("CAC", "RER", "CAC");
        hastePotionRecipe.setIngredient('R', Material.RABBIT_HIDE);
        hastePotionRecipe.setIngredient('E', Material.EXPERIENCE_BOTTLE);
        hastePotionRecipe.setIngredient('C',Material.COPPER_BLOCK);
        hastePotionRecipe.setIngredient('A',Material.AIR);
        Bukkit.addRecipe(hastePotionRecipe);
    }

    public static void addLatchAppleRecipe() {
        ItemStack latchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta im = latchApple.getItemMeta();
        assert im != null;
        im.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        im.addEnchant(Enchantment.KNOCKBACK, 3, true);
        im.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
        im.setDisplayName(ChatColor.GOLD + "Holy Latch Apple");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Latch's own apple?");
        lore.add("What does it do?");
        im.setLore(lore);
        latchApple.setItemMeta(im);
        NamespacedKey latchAppleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "ENCHANTED_GOLDEN_APPLE");
        ShapedRecipe latchAppleRecipe = new ShapedRecipe(latchAppleKey, latchApple);
        latchAppleRecipe.shape("GGG", "GAG", "GGG");
        latchAppleRecipe.setIngredient('G', Material.GOLD_BLOCK);
        latchAppleRecipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(latchAppleRecipe);
    }

    public static void addExperienceStorageBottleRecipe() {
        ItemStack xpStorageBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta im = xpStorageBottle.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "Experience Storage Bottle");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Experience Storage Bottle");
        lore.add("XP: 0");
        lore.add("To add experience, hold in hand and run /lmp xpDeposit [amount]");
        lore.add("To withdraw experience, hold in hand and run /lmp xpWithdraw [amount]");
        im.setLore(lore);
        xpStorageBottle.setItemMeta(im);
        NamespacedKey xpStorageBottleKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "EXPERIENCE_BOTTLE");
        ShapedRecipe xpStorageBottleRecipe = new ShapedRecipe(xpStorageBottleKey, xpStorageBottle);
        xpStorageBottleRecipe.shape("DBD", "BAB", "EBE");
        xpStorageBottleRecipe.setIngredient('E', Material.EMERALD);
        xpStorageBottleRecipe.setIngredient('B', Material.EXPERIENCE_BOTTLE);
        xpStorageBottleRecipe.setIngredient('D', Material.DIAMOND);
        xpStorageBottleRecipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        Bukkit.addRecipe(xpStorageBottleRecipe);
    }

    public static void add33IronRepairRecipe() {
        ItemStack iron33RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = iron33RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "33% Iron Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Iron Repair Item");
        lore.add("Repairs Iron Items with Mending");
        lore.add("33%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        iron33RepairItem.setItemMeta(im);
        NamespacedKey iron33RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "IRON_33_REPAIR");
        ShapedRecipe iron33RepairItemRecipe = new ShapedRecipe(iron33RepairItemKey, iron33RepairItem);
        iron33RepairItemRecipe.shape("RNR", "NPN", "RNR");
        iron33RepairItemRecipe.setIngredient('R', Material.REDSTONE);
        iron33RepairItemRecipe.setIngredient('N', Material.IRON_NUGGET);
        iron33RepairItemRecipe.setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(iron33RepairItemRecipe);
    }

    public static void add66IronRepairRecipe() {
        ItemStack iron66RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = iron66RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "66% Iron Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Iron Repair Item");
        lore.add("Repairs Iron Items with Mending");
        lore.add("66%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        iron66RepairItem.setItemMeta(im);
        NamespacedKey iron66RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "IRON_66_REPAIR");
        ShapedRecipe iron66RepairItemRecipe = new ShapedRecipe(iron66RepairItemKey, iron66RepairItem);
        iron66RepairItemRecipe.shape("RNR", "IPI", "RNR");
        iron66RepairItemRecipe.setIngredient('R', Material.REDSTONE);
        iron66RepairItemRecipe.setIngredient('I', Material.IRON_INGOT);
        iron66RepairItemRecipe.setIngredient('N', Material.IRON_NUGGET);
        iron66RepairItemRecipe.setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(iron66RepairItemRecipe);
    }

    public static void add100IronRepairRecipe() {
        ItemStack iron100RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = iron100RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "100% Iron Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Iron Repair Item");
        lore.add("Repairs Iron Items with Mending");
        lore.add("100%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        iron100RepairItem.setItemMeta(im);
        NamespacedKey iron100RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "IRON_100_REPAIR");
        ShapedRecipe iron100RepairItemRecipe = new ShapedRecipe(iron100RepairItemKey, iron100RepairItem);
        iron100RepairItemRecipe.shape("RIR", "IPI", "RIR");
        iron100RepairItemRecipe.setIngredient('R', Material.REDSTONE);
        iron100RepairItemRecipe.setIngredient('I', Material.IRON_INGOT);
        iron100RepairItemRecipe.setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(iron100RepairItemRecipe);
    }

    public static void add33DiamondRepairRecipe() {
        ItemStack diamond33RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = diamond33RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "33% Diamond Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Diamond Repair Item");
        lore.add("Repairs Diamond Items with Mending");
        lore.add("33%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        diamond33RepairItem.setItemMeta(im);
        NamespacedKey diamond33RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "DIAMOND_33_REPAIR");
        ShapedRecipe diamond33RepairItemRecipe = new ShapedRecipe(diamond33RepairItemKey, diamond33RepairItem);
        diamond33RepairItemRecipe.shape("LIL", "RPR", "LDL");
        diamond33RepairItemRecipe.setIngredient('R', Material.REDSTONE);
        diamond33RepairItemRecipe.setIngredient('I', Material.IRON_INGOT);
        diamond33RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        diamond33RepairItemRecipe.setIngredient('P', Material.PAPER);
        diamond33RepairItemRecipe.setIngredient('L', Material.LAPIS_LAZULI);
        Bukkit.addRecipe(diamond33RepairItemRecipe);
    }

    public static void add66DiamondRepairRecipe() {
        ItemStack diamond66RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = diamond66RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "66% Diamond Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Diamond Repair Item");
        lore.add("Repairs Diamond Items with Mending");
        lore.add("66%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        diamond66RepairItem.setItemMeta(im);
        NamespacedKey diamond66RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "DIAMOND_66_REPAIR");
        ShapedRecipe diamond66RepairItemRecipe = new ShapedRecipe(diamond66RepairItemKey, diamond66RepairItem);
        diamond66RepairItemRecipe.shape("LIL", "DPD", "LIL");
        diamond66RepairItemRecipe.setIngredient('I', Material.IRON_INGOT);
        diamond66RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        diamond66RepairItemRecipe.setIngredient('P', Material.PAPER);
        diamond66RepairItemRecipe.setIngredient('L', Material.LAPIS_LAZULI);
        Bukkit.addRecipe(diamond66RepairItemRecipe);
    }

    public static void add100DiamondRepairRecipe() {
        ItemStack diamond100RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = diamond100RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "100% Diamond Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Diamond Repair Item");
        lore.add("Repairs Diamond Items with Mending");
        lore.add("100%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        diamond100RepairItem.setItemMeta(im);
        NamespacedKey diamond100RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "DIAMOND_100_PAPER");
        ShapedRecipe diamond100RepairItemRecipe = new ShapedRecipe(diamond100RepairItemKey, diamond100RepairItem);
        diamond100RepairItemRecipe.shape("LDL", "DPD", "LDL");
        diamond100RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        diamond100RepairItemRecipe.setIngredient('P', Material.PAPER);
        diamond100RepairItemRecipe.setIngredient('L', Material.LAPIS_LAZULI);
        Bukkit.addRecipe(diamond100RepairItemRecipe);
    }

    public static void add25NetheriteRepairRecipe() {
        ItemStack netherite25RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = netherite25RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "25% Netherite Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Netherite Repair Item");
        lore.add("Repairs Netherite Items with Mending");
        lore.add("25%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        netherite25RepairItem.setItemMeta(im);
        NamespacedKey netherite25RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "NETHERITE_25_REPAIR");
        ShapedRecipe netherite25RepairItemRecipe = new ShapedRecipe(netherite25RepairItemKey, netherite25RepairItem);
        netherite25RepairItemRecipe.shape("OCO", "DPD", "OAO");
        netherite25RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        netherite25RepairItemRecipe.setIngredient('P', Material.PAPER);
        netherite25RepairItemRecipe.setIngredient('C', Material.COPPER_BLOCK);
        netherite25RepairItemRecipe.setIngredient('O', Material.OBSIDIAN);
        netherite25RepairItemRecipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        Bukkit.addRecipe(netherite25RepairItemRecipe);
    }

    public static void add50NetheriteRepairRecipe() {
        ItemStack netherite50RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = netherite50RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "50% Netherite Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Netherite Repair Item");
        lore.add("Repairs Netherite Items with Mending");
        lore.add("50%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        netherite50RepairItem.setItemMeta(im);
        NamespacedKey netherite50RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "NETHERITE_50_REPAIR");
        ShapedRecipe netherite50RepairItemRecipe = new ShapedRecipe(netherite50RepairItemKey, netherite50RepairItem);
        netherite50RepairItemRecipe.shape("DOD", "APA", "DOD");
        netherite50RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        netherite50RepairItemRecipe.setIngredient('P', Material.PAPER);
        netherite50RepairItemRecipe.setIngredient('O', Material.OBSIDIAN);
        netherite50RepairItemRecipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        Bukkit.addRecipe(netherite50RepairItemRecipe);
    }

    public static void add100NetheriteRepairRecipe() {
        ItemStack netherite100RepairItem = new ItemStack(Material.PAPER);
        ItemMeta im = netherite100RepairItem.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.GOLD + "100% Netherite Repair Item");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Netherite Repair Item");
        lore.add("Repairs Netherite Items with Mending");
        lore.add("100%");
        lore.add("Place in offhand. Hold item to repair in main hand.");
        lore.add("/lmp repair");
        im.setLore(lore);
        netherite100RepairItem.setItemMeta(im);
        NamespacedKey netherite100RepairItemKey = new NamespacedKey(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), "NETHERITE_100_REPAIR");
        ShapedRecipe netherite100RepairItemRecipe = new ShapedRecipe(netherite100RepairItemKey, netherite100RepairItem);
        netherite100RepairItemRecipe.shape("OIO", "DPD", "ONO");
        netherite100RepairItemRecipe.setIngredient('D', Material.DIAMOND);
        netherite100RepairItemRecipe.setIngredient('P', Material.PAPER);
        netherite100RepairItemRecipe.setIngredient('O', Material.OBSIDIAN);
        netherite100RepairItemRecipe.setIngredient('I', Material.IRON_BLOCK);
        netherite100RepairItemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(netherite100RepairItemRecipe);
    }

}
