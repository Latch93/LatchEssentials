package discord.Bank;

import discord.Api;
import discord.Constants;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Bank {
    private static File bankFile = Api.getConfigFile(Constants.YML_BANK_FILE_NAME);
    private static FileConfiguration bankCfg = Api.getFileConfiguration(bankFile);
    public static void setLoginTime(PlayerJoinEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerName)){
            bankCfg.set(Constants.YML_PLAYERS + playerName, playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerName + ".loginTime", timeMilli);
        bankCfg.save(bankFile);
    }
    public static void setLogoutTime(PlayerQuitEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerName)){
            bankCfg.set(Constants.YML_PLAYERS + playerName, playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerName + ".logoutTime", timeMilli);
        bankCfg.save(bankFile);
    }

    public static void setPlayerSessionSecondsPlayed(PlayerQuitEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        bankCfg.set(Constants.YML_PLAYERS + playerName + ".lastSessionSeconds", TimeUnit.MILLISECONDS.toSeconds(getSecondsPlayedInSession(playerName)));
        bankCfg.save(bankFile);
    }

    public static Long getSecondsPlayedInSession(String playerName){
        long loginTime = bankCfg.getLong(Constants.YML_PLAYERS + playerName + ".loginTime");
        Date date = new Date();
        long logoutTime = date.getTime();
        long totalTimePlayedMilli = logoutTime - loginTime;
        return TimeUnit.MILLISECONDS.toSeconds(totalTimePlayedMilli);
    }

    public static double getPlayerBalance(Player player){
        OfflinePlayer offlinePlayer = Api.getOfflinePlayerFromPlayer(player);
        Economy econ = Api.getEconomy();
        return econ.getBalance(offlinePlayer);
    }

    public static void setPlayerBalanceInConfigOnLogin(Player player) throws IOException {
        bankCfg.set(Constants.YML_PLAYERS + player.getName() + ".loginBalance", getPlayerBalance(player));
        bankCfg.save(bankFile);
    }

    public static void setPlayerBalanceWithInterest(Player player){
        FileConfiguration mainCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        double moneyPlayedMultiplier = mainCfg.getDouble("bankMultiplier");
        double moneyForTimePlayed = (double) getSecondsPlayedInSession(player.getName()) * moneyPlayedMultiplier;
        Api.getEconomy().depositPlayer(Api.getOfflinePlayerFromPlayer(player), moneyForTimePlayed);
    }
}
