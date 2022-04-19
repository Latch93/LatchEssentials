package discord.DiscordText;

import discord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DiscordTextCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();
        if (args[0].equalsIgnoreCase("purgeWhitelist")) {
            LatchDiscord.purge();
        } else if (args[0].equalsIgnoreCase("setdiscord")) {
            try {
                LatchDiscord.setDiscordUserNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("stat")) {
            sender.sendMessage(ChatColor.GREEN + "Total deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
            sender.sendMessage(ChatColor.GREEN + "Total mobs killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
            sender.sendMessage(ChatColor.GREEN + "Number of times jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
        }
        else if (args[0].equalsIgnoreCase("rtp")){
            RandomTeleport.randomTp(player);
        }
        else if (args[0].equalsIgnoreCase("lottery")){
            double maxLottoAmount = Constants.LOTTERY_MAX_AMOUNT;
            FileConfiguration lotteryCfg = Main.loadConfig(Constants.YML_LOTTERY_FILE_NAME);
            String lottoPlayerCheck = Constants.YML_PLAYERS + playerName;
            boolean playerBoughtIn = false;
            try {
                if (args[1].equalsIgnoreCase("check")){
                    if (lotteryCfg.isSet(lottoPlayerCheck)){
                        playerBoughtIn = lotteryCfg.getBoolean(lottoPlayerCheck + ".boughtIn");
                        if (Boolean.TRUE.equals(playerBoughtIn)){
                            player.sendMessage(ChatColor.GREEN + "You have bought in the current lotto for " + ChatColor.GOLD + "$" + maxLottoAmount);
                        } else {
                            player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/dt lottery buyin");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/dt lottery buyin");
                    }
                } else if (args[1].equalsIgnoreCase("buyin")){
                    double playerBalance = Main.econ.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    if (playerBalance >= maxLottoAmount){
                        player.sendMessage(ChatColor.GREEN + "You bought into the current lottery for " + ChatColor.GOLD + "$" + maxLottoAmount);
                        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                            if (olp.getName().equalsIgnoreCase(playerName)){
                                Main.econ.withdrawPlayer(olp, maxLottoAmount);
                            }
                        }
                        lotteryCfg.set(lottoPlayerCheck + ".boughtIn", true);
                    } else {
                        player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + maxLottoAmount + ChatColor.RED + " to buy into the lotto.");
                    }
                    try {
                        lotteryCfg.save(Main.lotteryFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (args[1].equalsIgnoreCase("total")) {
                    int count = 0;
                    for (String users : lotteryCfg.getConfigurationSection("players").getKeys(false)) {
                        if (Boolean.TRUE.equals(lotteryCfg.getBoolean(Constants.YML_PLAYERS + users + ".boughtIn"))) {
                            count++;
                        }
                    }
                    double totalLottoAmount = count * maxLottoAmount;
                    player.sendMessage(ChatColor.GREEN + "Current lottery worth " + ChatColor.GOLD + "$" + totalLottoAmount);
                } else if (args[1].equalsIgnoreCase("run")){
                    if (playerName.equalsIgnoreCase(Constants.SERVER_OWNER_NAME)){
                        Lottery.executeLotto(player);
                    }
                }
            } catch (IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lottery check] [/dt lottery buyin] [/dt lottery total]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return false;
    }
}
