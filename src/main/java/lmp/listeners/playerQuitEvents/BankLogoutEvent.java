package lmp.listeners.playerQuitEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class BankLogoutEvent implements Listener {
    public BankLogoutEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void bankLogoutEvent(PlayerQuitEvent e) throws IOException {
        setPlayerSessionTime(e.getPlayer());
    }

    public static void setPlayerBalanceWithInterest(FileConfiguration bankCfg, Player player) throws IOException {
        FileConfiguration mainCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        double moneyPlayedMultiplier = mainCfg.getDouble("bankMultiplier");
        double moneyForTimePlayed = (double) Api.getSecondsPlayedInSession(player.getUniqueId().toString()) * moneyPlayedMultiplier;
        double timePlayedMinutes = Api.getSecondsPlayedInSession(player.getUniqueId().toString()) / 60.0;
        DecimalFormat df = new DecimalFormat("0.00");
        if (moneyForTimePlayed != 0) {
            if (bankCfg.getBoolean(Constants.YML_PLAYERS + player.getUniqueId() + ".isAFK")){
                player.sendMessage(ChatColor.GREEN + "You were afk exempt in your last session, so you weren't rewarded money for play time.");
            } else {
                bankCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward", "You played " + df.format(timePlayedMinutes) + " minutes and earned " + "$" + df.format(moneyForTimePlayed));
                Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), moneyForTimePlayed);
            }
        }
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

    public static void setPlayerSessionTime(Player player) throws IOException {
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        String playerName = player.getName();
        String playerId = player.getUniqueId().toString();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerId)) {
            bankCfg.set(Constants.YML_PLAYERS + playerId + ".playerName", playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".logoutTime", timeMilli);
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".lastSessionSeconds", Api.getSecondsPlayedInSession(playerId));
        setPlayerBalanceWithInterest(bankCfg, player);
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

}
