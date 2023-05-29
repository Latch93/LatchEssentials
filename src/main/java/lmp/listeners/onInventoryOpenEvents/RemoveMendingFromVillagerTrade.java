package lmp.listeners.onInventoryOpenEvents;

import lmp.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RemoveMendingFromVillagerTrade implements Listener {

    public RemoveMendingFromVillagerTrade(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void removeMendingFromVillagerTrade(InventoryOpenEvent event) {
        List<String> enabledRemoveMendingVillagerWorlds = new ArrayList<>();
        enabledRemoveMendingVillagerWorlds.add("world");
        enabledRemoveMendingVillagerWorlds.add("world_nether");
        enabledRemoveMendingVillagerWorlds.add("world_the_end");
        try {
            if (enabledRemoveMendingVillagerWorlds.contains(event.getPlayer().getWorld().getName()) && event.getView().getType() != InventoryType.MERCHANT) {
                return;
            }
            Merchant merchant = ((MerchantInventory) event.getInventory()).getMerchant();
            List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
            MerchantRecipe swiftSneakRecipe = null;
            if (enabledRemoveMendingVillagerWorlds.contains(event.getPlayer().getWorld().getName())) {
                for (MerchantRecipe recipe : recipes) {
                    if (recipe.getResult().getType() == Material.ENCHANTED_BOOK && recipe.getResult().getItemMeta() != null && recipe.getResult().getItemMeta().toString().contains("MENDING")) {
                        ItemStack swiftSneakBook = new ItemStack(Material.ENCHANTED_BOOK);
                        EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) swiftSneakBook.getItemMeta();
                        assert enchantmentMeta != null;
                        enchantmentMeta.addStoredEnchant(Enchantment.SOUL_SPEED, 3, true);
                        ItemMeta im = swiftSneakBook.getItemMeta();
                        assert im != null;
                        swiftSneakBook.setItemMeta(enchantmentMeta);
                        swiftSneakRecipe = new MerchantRecipe(swiftSneakBook, 1);
                        List<ItemStack> ingredients = new ArrayList<>();
                        ingredients.add(new ItemStack(Material.BOOK));
                        ingredients.add(new ItemStack(Material.EMERALD, 32));
                        swiftSneakRecipe.setIngredients(ingredients);
                        recipes.removeIf(thisRecipe -> (thisRecipe.getResult().getType() == Material.ENCHANTED_BOOK && thisRecipe.getResult().getItemMeta() != null && thisRecipe.getResult().getItemMeta().toString().contains("MENDING")));
                        recipes.add(swiftSneakRecipe);
                        merchant.setRecipes(recipes);
                    }
                }
            }
        } catch (ClassCastException ignored){
        }
    }
}
