package discord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Lottery {
    public static void executeLotto(Player player) throws IOException {
        FileConfiguration lotteryCfg = Main.loadConfig(Constants.YML_LOTTERY_FILE_NAME);
        double maxLottoAmount = Constants.LOTTERY_MAX_AMOUNT;
        try {
            int count = 0;
            ArrayList<String> playerList = new ArrayList<>();
            for (String user : lotteryCfg.getConfigurationSection("players").getKeys(false)) {
                if (Boolean.TRUE.equals(lotteryCfg.getBoolean(Constants.YML_PLAYERS + user + ".boughtIn"))) {
                    playerList.add(user);
                    lotteryCfg.set(Constants.YML_PLAYERS + user + ".boughtIn", false);
                    count++;
                }
            }
            Random rand = new Random();
            int n = rand.nextInt(playerList.size());
            double totalLottoAmount = count * maxLottoAmount;
            OfflinePlayer offlinePlayer = null;
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                if (olp.getName().equalsIgnoreCase(playerList.get(n))){
                    offlinePlayer = olp;
                }
            }
            Main.econ.depositPlayer(offlinePlayer, totalLottoAmount);
            assert offlinePlayer != null;
            Bukkit.broadcastMessage(ChatColor.GOLD + offlinePlayer.getName() + ChatColor.GREEN + " won the lottery!!! They won " + ChatColor.GOLD + "$" + totalLottoAmount );
            lotteryCfg.save(Main.lotteryFile);
        } catch (IllegalArgumentException e){
            player.sendMessage(ChatColor.RED + "No one bought into the lottery.");
        }

    }

    public static void lottoCommands(Player player, String parameter){
        String playerName = player.getName();
        double maxLottoAmount = Constants.LOTTERY_MAX_AMOUNT;
        FileConfiguration lotteryCfg = Main.loadConfig(Constants.YML_LOTTERY_FILE_NAME);
        String lottoPlayerCheck = Constants.YML_PLAYERS + playerName;
        boolean playerBoughtIn = false;
        try {
            if (parameter.equalsIgnoreCase("check")){
                if (lotteryCfg.isSet(lottoPlayerCheck)){
                    playerBoughtIn = lotteryCfg.getBoolean(lottoPlayerCheck + ".boughtIn");
                    if (Boolean.TRUE.equals(playerBoughtIn)){
                        player.sendMessage(ChatColor.GREEN + "You have bought in the current lotto for " + ChatColor.GOLD + "$" + maxLottoAmount);
                    } else {
                        player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/dt lotto buyin");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/dt lotto buyin");
                }
            } else if (parameter.equalsIgnoreCase("buyin")){
                double playerBalance = Main.econ.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (playerBalance >= maxLottoAmount){
                    if (Boolean.FALSE.equals(lotteryCfg.getBoolean(Constants.YML_PLAYERS + playerName + ".boughtIn"))){
                        player.sendMessage(ChatColor.GREEN + "You bought into the current lottery for " + ChatColor.GOLD + "$" + maxLottoAmount);
                        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                            if (olp.getName().equalsIgnoreCase(playerName)){
                                Main.econ.withdrawPlayer(olp, maxLottoAmount);
                            }
                        }
                        lotteryCfg.set(lottoPlayerCheck + ".boughtIn", true);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You have already bought into the current lottery.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + maxLottoAmount + ChatColor.RED + " to buy into the lotto.");
                }
                try {
                    lotteryCfg.save(Main.lotteryFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (parameter.equalsIgnoreCase("total")) {
                int count = 0;
                for (String users : lotteryCfg.getConfigurationSection("players").getKeys(false)) {
                    if (Boolean.TRUE.equals(lotteryCfg.getBoolean(Constants.YML_PLAYERS + users + ".boughtIn"))) {
                        count++;
                    }
                }
                double totalLottoAmount = count * maxLottoAmount;
                player.sendMessage(ChatColor.GREEN + "Current lottery worth " + ChatColor.GOLD + "$" + totalLottoAmount);
            } else if (parameter.equalsIgnoreCase("run")){
                if (playerName.equalsIgnoreCase(Constants.SERVER_OWNER_NAME)){
                    Lottery.executeLotto(player);
                }
            }
        } catch (IndexOutOfBoundsException e){
            player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lotto check] [/dt lotto buyin] [/dt lotto total]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
