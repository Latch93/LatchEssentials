package discord_text;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class RandomItem{
    public static final Material[] m = Material.values();
    public static final int RANDOM_ITEM_COST = 1000;
    public static void getRandomItem(PlayerInteractEvent event) {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        assert rsp != null;
        econ = rsp.getProvider();
        Player player = event.getPlayer();
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        Location leverLocation = new Location(event.getPlayer().getWorld(), 10005, 68, 9998 );
        if(Objects.requireNonNull(event.getClickedBlock()).getLocation().equals(leverLocation)){
            Block block = event.getClickedBlock();
            assert block != null;
            if (block.getLocation().equals(leverLocation)){
                if (econ.getBalance(op) >= RANDOM_ITEM_COST) {
                    Random rand = new Random();
                    int n = rand.nextInt(m.length);
                    Material itemToGive = m[n];
                    if (String.valueOf(m[n]).equalsIgnoreCase("AIR")){
                        itemToGive = Material.BEACON;
                    }
                    System.out.println("Item to Give: " + itemToGive);

                    ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                    World world = player.getWorld();
                    Location dropLocation = new Location(event.getPlayer().getWorld(), 10006, 70, 9998 );
                    try {
                        world.dropItem(dropLocation,is);
                        econ.withdrawPlayer(op, RANDOM_ITEM_COST);
                        player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + m[n]);
                        EmbedBuilder eb = new EmbedBuilder();
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        eb.setTitle("Discord Username: " + LatchDiscord.getDiscordUserName(player.getName()) + "\nMC Username: " + player.getName() + "\nRandom Item: " + m[n].toString() + "\nLocal Time: " + formatter.format(date), null);
                        eb.setColor(new Color(0xE1E2BF0D, true));
                        TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById("955870340670689340");
                        assert randomItemLogChannel != null;
                        randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                    } catch (IllegalArgumentException e){
                        System.out.println("Can't give air in Random Item " + e);
                        player.sendMessage(ChatColor.RED + "An error occured. Please click for a random item again :)");
                    }

                } else {
                    player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" +RANDOM_ITEM_COST);
                    player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + econ.getBalance(op));
                }
            }
        }

    }
}
