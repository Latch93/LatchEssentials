package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RandomItem {
    public static void getRandomItem1(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            FileConfiguration randomItemGenCfg = Api.getFileConfiguration(YmlFileNames.YML_RANDOM_ITEM_GEN_FILE_NAME);
            int randomItemCost = randomItemGenCfg.getInt("randomItemGen1.cost");
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ;
            assert rsp != null;
            econ = rsp.getProvider();
            Player player = event.getPlayer();
            List<String> items = randomItemGenCfg.getStringList("items1");
            OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
            Location leverLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.x"), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.y"), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.z"));
            DecimalFormat df = new DecimalFormat("0.00");
            if (event.getClickedBlock().getLocation().equals(leverLocation)) {
                Block block = event.getClickedBlock();
                assert block != null;
                if (block.getLocation().equals(leverLocation)) {
                    if (econ.getBalance(op) >= randomItemCost) {
                        Random rand = new Random();
                        int n = rand.nextInt(items.size());
                        Material itemToGive = Material.valueOf(items.get(n));
                        Api.messageInConsole(ChatColor.GREEN + "Item to Give: " + ChatColor.GOLD + itemToGive);
                        ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                        World world = player.getWorld();
                        Location dropLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.x"), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.y"), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.z"));
                        try {
                            world.dropItem(dropLocation, is);
                            //world.playSound()
                            econ.withdrawPlayer(op, randomItemCost);
                            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + itemToGive.toString());
                            EmbedBuilder eb = new EmbedBuilder();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                            Date date = new Date(System.currentTimeMillis());
                            eb.setTitle("$" + randomItemCost + " --- Discord Username: " + LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(player.getUniqueId().toString())).getUser().getName() + "\nMC Username: " + player.getName() + "\nRandom Item: " + itemToGive.toString() + "\nLocal Time: " + formatter.format(date), null);
                            eb.setColor(new Color(0xE1E2BF0D, true));
                            TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById(Constants.RANDOM_ITEM_LOG_CHANNEL_ID);
                            assert randomItemLogChannel != null;
                            randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "New Balance: " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op))));
                        } catch (IllegalArgumentException e) {
                            Api.messageInConsole(ChatColor.RED + "Can't give air in Random Item " + e);
                            player.sendMessage(ChatColor.RED + "An error occurred. Please click for a random item again :)");
                        }

                    } else {
                        player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" + randomItemCost);
                        player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op)));
                    }
                }
            }
        }
    }

    public static void getRandomItem2(PlayerInteractEvent event) {
        FileConfiguration randomItemGenCfg = Api.getFileConfiguration(YmlFileNames.YML_RANDOM_ITEM_GEN_FILE_NAME);
        int randomItemCost = randomItemGenCfg.getInt("randomItemGen2.cost");
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ;
        assert rsp != null;
        DecimalFormat df = new DecimalFormat("0.00");
        econ = rsp.getProvider();
        Player player = event.getPlayer();
        List<String> items = randomItemGenCfg.getStringList("items2");
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        Location leverLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen2.buttonLocation.x"), randomItemGenCfg.getInt("randomItemGen2.buttonLocation.y"), randomItemGenCfg.getInt("randomItemGen2.buttonLocation.z"));
        if (event.getClickedBlock() != null && event.getClickedBlock().getLocation().equals(leverLocation)) {
            Block block = event.getClickedBlock();
            assert block != null;
            if (block.getLocation().equals(leverLocation)) {
                if (econ.getBalance(op) >= randomItemCost) {
                    Random rand = new Random();
                    int n = rand.nextInt(items.size());
                    Material itemToGive = Material.valueOf(items.get(n));
                    Api.messageInConsole(ChatColor.GREEN + "Item to Give: " + ChatColor.GOLD + itemToGive);
                    ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                    World world = player.getWorld();
                    Location dropLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen2.itemDropLocation.x"), randomItemGenCfg.getInt("randomItemGen2.itemDropLocation.y"), randomItemGenCfg.getInt("randomItemGen2.itemDropLocation.z"));
                    try {
                        world.dropItem(dropLocation, is);
                        //world.playSound()
                        econ.withdrawPlayer(op, randomItemCost);
                        player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + itemToGive.toString());
                        EmbedBuilder eb = new EmbedBuilder();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        eb.setTitle("$" + randomItemCost + " --- Discord Username: " + LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(player.getUniqueId().toString())).getUser().getName() + "\nMC Username: " + player.getName() + "\nRandom Item: " + itemToGive.toString() + "\nLocal Time: " + formatter.format(date), null);
                        eb.setColor(new Color(0xE1E2BF0D, true));
                        TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById(Constants.RANDOM_ITEM_LOG_CHANNEL_ID);
                        assert randomItemLogChannel != null;
                        randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                    } catch (IllegalArgumentException e) {
                        Api.messageInConsole(ChatColor.RED + "Can't give air in Random Item " + e);
                        player.sendMessage(ChatColor.RED + "An error occurred. Please click for a random item again :)");
                    }

                } else {
                    player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" + randomItemCost);
                    player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op)));
                }
            }
        }

    }

    public static void getRandomItem3(PlayerInteractEvent event) {
        FileConfiguration randomItemGenCfg = Api.getFileConfiguration(YmlFileNames.YML_RANDOM_ITEM_GEN_FILE_NAME);
        int randomItemCost = randomItemGenCfg.getInt("randomItemGen3.cost");
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ;
        assert rsp != null;
        econ = rsp.getProvider();
        DecimalFormat df = new DecimalFormat("0.00");
        Player player = event.getPlayer();
        List<String> items = randomItemGenCfg.getStringList("items3");
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        Location leverLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen3.buttonLocation.x"), randomItemGenCfg.getInt("randomItemGen3.buttonLocation.y"), randomItemGenCfg.getInt("randomItemGen3.buttonLocation.z"));
        if (event.getClickedBlock() != null && event.getClickedBlock().getLocation().equals(leverLocation)) {
            Block block = event.getClickedBlock();
            assert block != null;
            if (block.getLocation().equals(leverLocation)) {
                if (econ.getBalance(op) >= randomItemCost) {
                    Random rand = new Random();
                    int n = rand.nextInt(items.size());
                    Material itemToGive = Material.valueOf(items.get(n));
                    Api.messageInConsole(ChatColor.GREEN + "Item to Give: " + ChatColor.GOLD + itemToGive);
                    ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                    World world = player.getWorld();
                    Location dropLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen3.itemDropLocation.x"), randomItemGenCfg.getInt("randomItemGen3.itemDropLocation.y"), randomItemGenCfg.getInt("randomItemGen3.itemDropLocation.z"));
                    try {
                        world.dropItem(dropLocation, is);
                        //world.playSound()
                        econ.withdrawPlayer(op, randomItemCost);
                        player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + itemToGive.toString());
                        EmbedBuilder eb = new EmbedBuilder();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        eb.setTitle("$" + randomItemCost + " --- Discord Username: " + LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(player.getUniqueId().toString())).getUser().getName() + "\nMC Username: " + player.getName() + "\nRandom Item: " + itemToGive.toString() + "\nLocal Time: " + formatter.format(date), null);
                        eb.setColor(new Color(0xE1E2BF0D, true));
                        TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById(Constants.RANDOM_ITEM_LOG_CHANNEL_ID);
                        assert randomItemLogChannel != null;
                        randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                    } catch (IllegalArgumentException e) {
                        Api.messageInConsole(ChatColor.RED + "Can't give air in Random Item " + e);
                        player.sendMessage(ChatColor.RED + "An error occurred. Please click for a random item again :)");
                    }

                } else {
                    player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" + randomItemCost);
                    player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op)));
                }
            }
        }
    }

    public static void getRandomItem(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            Location buttonLocation = event.getClickedBlock().getLocation();
//            ArrayList<String> randomItemGen1
            FileConfiguration randomItemGenCfg = Api.getFileConfiguration(YmlFileNames.YML_RANDOM_ITEM_GEN_FILE_NAME);
            int randomItemCost = randomItemGenCfg.getInt("randomItemGen1.cost");
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ;
            assert rsp != null;
            econ = rsp.getProvider();
            Player player = event.getPlayer();
            List<String> items = randomItemGenCfg.getStringList("items1");
            OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
            Location leverLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.x"), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.y"), randomItemGenCfg.getInt("randomItemGen1.buttonLocation.z"));
            DecimalFormat df = new DecimalFormat("0.00");
            if (event.getClickedBlock().getLocation().equals(leverLocation)) {
                Block block = event.getClickedBlock();
                assert block != null;
                if (block.getLocation().equals(leverLocation)) {
                    if (econ.getBalance(op) >= randomItemCost) {
                        Random rand = new Random();
                        int n = rand.nextInt(items.size());
                        Material itemToGive = Material.valueOf(items.get(n));
                        Api.messageInConsole(ChatColor.GREEN + "Item to Give: " + ChatColor.GOLD + itemToGive);
                        ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                        World world = player.getWorld();
                        Location dropLocation = new Location(event.getPlayer().getWorld(), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.x"), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.y"), randomItemGenCfg.getInt("randomItemGen1.itemDropLocation.z"));
                        try {
                            world.dropItem(dropLocation, is);
                            //world.playSound()
                            econ.withdrawPlayer(op, randomItemCost);
                            player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + itemToGive.toString());
                            EmbedBuilder eb = new EmbedBuilder();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                            Date date = new Date(System.currentTimeMillis());
                            eb.setTitle("$" + randomItemCost + " --- Discord Username: " + LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(player.getUniqueId().toString())).getUser().getName() + "\nMC Username: " + player.getName() + "\nRandom Item: " + itemToGive.toString() + "\nLocal Time: " + formatter.format(date), null);
                            eb.setColor(new Color(0xE1E2BF0D, true));
                            TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById(Constants.RANDOM_ITEM_LOG_CHANNEL_ID);
                            assert randomItemLogChannel != null;
                            randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "New Balance: " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op))));
                        } catch (IllegalArgumentException e) {
                            Api.messageInConsole(ChatColor.RED + "Can't give air in Random Item " + e);
                            player.sendMessage(ChatColor.RED + "An error occurred. Please click for a random item again :)");
                        }

                    } else {
                        player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" + randomItemCost);
                        player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + df.format(econ.getBalance(op)));
                    }
                }
            }
        }
    }

}
