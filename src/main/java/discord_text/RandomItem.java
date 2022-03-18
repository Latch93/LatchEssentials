package discord_text;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Random;

public class RandomItem implements CommandExecutor {
    Material[] m = Material.values();
    int randomItemCommandCost = 1000;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        econ = rsp.getProvider();
        Player player = (Player) sender;
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        assert econ != null;
        if (econ.getBalance(op) >= randomItemCommandCost) {
            Random rand = new Random();
            int n = rand.nextInt(m.length);
            Material itemToGive = m[n];
            if (String.valueOf(m[n]).equalsIgnoreCase("AIR")){
                itemToGive = Material.BEACON;
            }
            ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
            World world = player.getWorld();
            Location location = player.getLocation();
            world.dropItem(location, is);
            econ.withdrawPlayer(op, randomItemCommandCost);
            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + m[n]);
        } else {
            player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" +randomItemCommandCost);
            player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + econ.getBalance(op));
        }
        return true;
    }
}
