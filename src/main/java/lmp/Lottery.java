package lmp;

import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Lottery {

    public static void executeLotto() throws IOException {
        FileConfiguration lotteryCfg = Api.loadConfig(YmlFileNames.YML_LOTTERY_FILE_NAME);
        double maxLottoAmount = lotteryCfg.getDouble("lottoBuyinAmount");
        try {
            int count = 0;
            ArrayList<String> playerList = new ArrayList<>();
            for (String user : lotteryCfg.getConfigurationSection("players").getKeys(false)) {
                if (Boolean.TRUE.equals(lotteryCfg.getBoolean(lmp.Constants.YML_PLAYERS + user + ".boughtIn"))) {
                    playerList.add(user);
                    lotteryCfg.set(lmp.Constants.YML_PLAYERS + user + ".boughtIn", false);
                    count++;
                }
            }
            Random rand = new Random();
            int n = rand.nextInt(playerList.size());
            double totalLottoAmount = (count * maxLottoAmount) + lotteryCfg.getDouble("additionalPrize");;
            Api.messageInConsole(ChatColor.GREEN + playerList.get(n));
            OfflinePlayer winningPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerList.get(n)));
            TextChannel lotteryWinnerChannel = LatchDiscord.jda.getTextChannelById(lmp.Constants.LOTTERY_WINNER_CHANNEL_ID);
            assert lotteryWinnerChannel != null;
            lotteryWinnerChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(playerList.get(n)) + "> won the lottery!!! They won $" + totalLottoAmount).queue();
            Api.getEconomy().depositPlayer(winningPlayer, totalLottoAmount);
            Bukkit.broadcastMessage(ChatColor.GOLD + winningPlayer.getName() + ChatColor.GREEN + " won the lottery!!! They won " + ChatColor.GOLD + "$" + totalLottoAmount );
            lotteryCfg.save(Api.getConfigFile(YmlFileNames.YML_LOTTERY_FILE_NAME));
        } catch (IllegalArgumentException ignored){
            
        }

    }

    public static void lottoCommands(Player player, String parameter, CommandSender sender){
        String playerName = player.getName();
        String playerId = player.getUniqueId().toString();
        FileConfiguration lotteryCfg = Api.loadConfig(YmlFileNames.YML_LOTTERY_FILE_NAME);
        double lottoBuyinAmount = lotteryCfg.getDouble("lottoBuyinAmount");
        String lottoPlayerCheck = lmp.Constants.YML_PLAYERS + playerId;
        boolean playerBoughtIn;
        try {
            if (parameter.equalsIgnoreCase("check")){
                if (lotteryCfg.isSet(lottoPlayerCheck)){
                    playerBoughtIn = lotteryCfg.getBoolean(lottoPlayerCheck + ".boughtIn");
                    if (Boolean.TRUE.equals(playerBoughtIn)){
                        player.sendMessage(ChatColor.GREEN + "You have bought in the current lotto for " + ChatColor.GOLD + "$" + lottoBuyinAmount);
                    } else {
                        player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/lmp lotto buyin");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You have to buy in with " + ChatColor.AQUA + "/lmp lotto buyin");
                }
            } else if (parameter.equalsIgnoreCase("buyin")){
                double playerBalance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (playerBalance >= lottoBuyinAmount){
                    if (Boolean.FALSE.equals(lotteryCfg.getBoolean(lmp.Constants.YML_PLAYERS + playerId + ".boughtIn"))){
                        player.sendMessage(ChatColor.GREEN + "You bought into the current lottery for " + ChatColor.GOLD + "$" + lottoBuyinAmount);
                        Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), lottoBuyinAmount);
                        lotteryCfg.set(lottoPlayerCheck + ".boughtIn", true);
                        lotteryCfg.set(lottoPlayerCheck + ".playerName", playerName);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You have already bought into the current lottery.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + lottoBuyinAmount + ChatColor.RED + " to buy into the lotto.");
                }
                try {
                    lotteryCfg.save(Api.getConfigFile(YmlFileNames.YML_LOTTERY_FILE_NAME));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (parameter.equalsIgnoreCase("total")) {
                int count = 0;
                for (String users : lotteryCfg.getConfigurationSection("players").getKeys(false)) {
                    if (Boolean.TRUE.equals(lotteryCfg.getBoolean(lmp.Constants.YML_PLAYERS + users + ".boughtIn"))) {
                        count++;
                    }
                }
                double totalLottoAmount = (count * lottoBuyinAmount) + lotteryCfg.getDouble("additionalPrize") ;
                player.sendMessage(ChatColor.GREEN + "Current lottery worth " + ChatColor.GOLD + "$" + totalLottoAmount);
            } else if (parameter.equalsIgnoreCase("run")){
                if (playerName.equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) || sender != null){
                    Lottery.executeLotto();
                }
            }
        } catch (IndexOutOfBoundsException e){
            player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/lmp lotto check] [/lmp lotto buyin] [/lmp lotto total]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
