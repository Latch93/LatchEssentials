package lmp.Bank;

import lmp.Api;
import lmp.Constants;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Bank {
    private static File bankFile = Api.getConfigFile(Constants.YML_BANK_FILE_NAME);
    private static FileConfiguration bankCfg = Api.getFileConfiguration(bankFile);
    public static void setLoginTime(PlayerJoinEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        String playerId = e.getPlayer().getUniqueId().toString();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerId)){
            bankCfg.set(Constants.YML_PLAYERS + playerId + ".playerName", playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".loginTime", timeMilli);
        bankCfg.save(bankFile);
    }
    public static void setLogoutTime(PlayerQuitEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        String playerId = e.getPlayer().getUniqueId().toString();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerId)){
            bankCfg.set(Constants.YML_PLAYERS + playerId + ".playerName", playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".logoutTime", timeMilli);
        bankCfg.save(bankFile);
    }

    public static void setPlayerSessionSecondsPlayed(PlayerQuitEvent e) throws IOException {
        String playerId = e.getPlayer().getUniqueId().toString();
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".lastSessionSeconds", TimeUnit.MILLISECONDS.toSeconds(getSecondsPlayedInSession(playerId)));
        bankCfg.save(bankFile);
    }

    public static Long getSecondsPlayedInSession(String playerId){
        long loginTime = bankCfg.getLong(Constants.YML_PLAYERS + playerId + ".loginTime");
        Date date = new Date();
        long logoutTime = date.getTime();
        long totalTimePlayedMilli = logoutTime - loginTime;
        return TimeUnit.MILLISECONDS.toSeconds(totalTimePlayedMilli);
    }

    public static double getPlayerBalance(Player player){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        Economy econ = Api.getEconomy();
        return econ.getBalance(offlinePlayer);
    }

    public static void setPlayerBalanceInConfigOnLogin(Player player) throws IOException {
        bankCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".loginBalance", getPlayerBalance(player));
        bankCfg.save(bankFile);
    }

    public static void showLastSessionReward(Player player) {
        if (bankCfg.isSet(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")) {
            player.sendMessage(Objects.requireNonNull(bankCfg.getString(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")));
        }
    }
    
    public static void setPlayerBalanceWithInterest(Player player) throws IOException {
        FileConfiguration mainCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        double moneyPlayedMultiplier = mainCfg.getDouble("bankMultiplier");
        double moneyForTimePlayed = (double) getSecondsPlayedInSession(player.getUniqueId().toString()) * moneyPlayedMultiplier;
        double timePlayedMinutes = getSecondsPlayedInSession(player.getUniqueId().toString()) / 60.0;
        DecimalFormat df = new DecimalFormat("0.00");
        if (moneyForTimePlayed != 0){
            bankCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward", "You played " + df.format(timePlayedMinutes) + " minutes and earned " +  "$" + df.format(moneyForTimePlayed) );
            bankCfg.save(bankFile);
            Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), moneyForTimePlayed);
        }
    }
}
