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
            int n = rand.nextInt(count);
            if (n > 0){
                n = n - 1;
            }
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
}
