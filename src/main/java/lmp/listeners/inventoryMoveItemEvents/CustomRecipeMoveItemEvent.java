package lmp.listeners.inventoryMoveItemEvents;

import lmp.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class CustomRecipeMoveItemEvent implements Listener {


    public CustomRecipeMoveItemEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}


    @EventHandler
    public void CustomRecipeMoveItemEventToGUI(InventoryClickEvent event){
        String customRecipeGUIName = event.getView().getTitle();
        if (customRecipeGUIName.contains("Custom Recipes")){
            Inventory customRecipeGUIInv = event.getClickedInventory();
            Main.log.info("HUZZAH");
            ArrayList<Integer> disabledSlotList = new ArrayList<>();
            disabledSlotList.add(0);
            disabledSlotList.add(1);
            disabledSlotList.add(2);
            disabledSlotList.add(6);
            disabledSlotList.add(7);
            disabledSlotList.add(8);
            disabledSlotList.add(9);
            disabledSlotList.add(10);
            disabledSlotList.add(11);
            disabledSlotList.add(15);
            disabledSlotList.add(17);
            disabledSlotList.add(18);
            disabledSlotList.add(19);
            disabledSlotList.add(20);
            disabledSlotList.add(24);
            disabledSlotList.add(25);
            disabledSlotList.add(26);
            if (disabledSlotList.contains(event.getSlot())){
                event.setCancelled(true);
            } else{
                checkEnchantedDiamondRecipe(event.getClickedInventory());
            }
        }
    }

    public void checkEnchantedDiamondRecipe(Inventory customRecipeGUI){
        ItemStack slotDiamondItem = new ItemStack(Material.DIAMOND, 32);
        ArrayList<Integer> slotsToCheck = new ArrayList<>();
        slotsToCheck.add(5);
        slotsToCheck.add(12);
        slotsToCheck.add(13);
        slotsToCheck.add(14);
        slotsToCheck.add(22);
        boolean isRecipeValid = true;
        for (int slot : slotsToCheck){
            if (!customRecipeGUI.getItem(slot).equals(slotDiamondItem)){
                isRecipeValid = false;
            }
        }
        if (Boolean.TRUE.equals(isRecipeValid)){
            ItemStack enchantedDiamond = new ItemStack(Material.DIAMOND, 1);
            ItemStack air = new ItemStack(Material.AIR, 1);
            ItemMeta im = enchantedDiamond.getItemMeta();
            assert im != null;
            im.setLore(Collections.singletonList("Enchanted Diamond"));
            im.addEnchant(Enchantment.MENDING, 1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            enchantedDiamond.setItemMeta(im);
            customRecipeGUI.setItem(16, enchantedDiamond);
            customRecipeGUI.setItem(5, air);
            customRecipeGUI.setItem(12, air);
            customRecipeGUI.setItem(13, air);
            customRecipeGUI.setItem(14, air);
            customRecipeGUI.setItem(22, air);
        }
    }
}
