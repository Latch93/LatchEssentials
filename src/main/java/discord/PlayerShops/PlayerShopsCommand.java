package discord.PlayerShops;

import discord.Api;
import discord.Backbacks.Inventories;
import discord.Constants;
import discord.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class PlayerShopsCommand implements CommandExecutor {
    Inventory inv;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                FileConfiguration playerShopCfg = Api.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
                Player player = (Player) sender;
                int invSize = 27;
                String playerName = player.getName();
                if (args[0].equalsIgnoreCase(Constants.MY_SHOP_COMMAND)) {
                    String invTitle = playerName + "'s Shop";
                    inv = Inventories.setInventoryWhenOpened(player, Constants.YML_PLAYER_SHOP_FILE_NAME, invSize, invTitle, playerName);
                    Objects.requireNonNull(player.getPlayer()).openInventory(Inventories.setLoreInPlayerShop(playerName, inv, player.getName()));
                }
                if (args[0].equalsIgnoreCase(Constants.SET_WORTH_COMMAND)) {
                    try {
                        int itemWorth = Integer.parseInt(args[1]);
                        if (!player.getInventory().getItemInMainHand().getType().isAir()){
                            ItemStack itemStack = player.getInventory().getItemInMainHand();
                            int totalItemCount = player.getInventory().getItemInMainHand().getAmount();
                            ItemStack singleItemStack = player.getInventory().getItemInMainHand();
                            singleItemStack.setAmount(1);
                            player.sendMessage(ChatColor.GREEN + "Set value of item to " + ChatColor.GOLD + "$" + itemWorth);
                            playerShopCfg.set(player.getName() + ".itemWorth." + singleItemStack, itemWorth);
                            itemStack.setAmount(totalItemCount);
                            playerShopCfg.save(Api.getConfigFile(Constants.YML_PLAYER_SHOP_FILE_NAME));
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.RED + "Error: Set item worth like this -> /ps setworth 10");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)) {
                    try {
                        String playerShopToOpen = args[1];
                        String invTitle = args[1] + "'s Shop";
                        inv = Inventories.setInventoryWhenOpened(player, Constants.YML_PLAYER_SHOP_FILE_NAME, invSize, invTitle, playerShopToOpen);
                        Objects.requireNonNull(player.getPlayer()).openInventory(Inventories.setLoreInPlayerShop(playerShopToOpen, inv, player.getName()));
                    } catch (ArrayIndexOutOfBoundsException e){
                        player.sendMessage(ChatColor.RED + "That player does not have a shop.");
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return false;
    }
}