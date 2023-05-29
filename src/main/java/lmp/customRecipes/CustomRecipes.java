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

}
