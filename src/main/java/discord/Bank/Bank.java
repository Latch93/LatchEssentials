package discord.Bank;

import discord.Constants;
import discord.Main;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Bank {
    private static FileConfiguration bankCfg = Main.getFileConfiguration(Main.bankFile);
    public static void setLoginTime(PlayerJoinEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerName)){
            bankCfg.set(Constants.YML_PLAYERS + playerName, playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerName + ".loginTime", timeMilli);
        bankCfg.save(Main.bankFile);
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
        bankCfg.save(Main.bankFile);
    }

    public static void setPlayerSessionSecondsPlayed(PlayerQuitEvent e) throws IOException {
        String playerName = e.getPlayer().getName();
        bankCfg.set(Constants.YML_PLAYERS + playerName + ".lastSessionSeconds", TimeUnit.MILLISECONDS.toSeconds(getSecondsPlayedInSession(playerName)));
        bankCfg.save(Main.bankFile);
    }

    public static Long getSecondsPlayedInSession(String playerName){
        long loginTime = bankCfg.getLong(Constants.YML_PLAYERS + playerName + ".loginTime");
        Date date = new Date();
        long logoutTime = date.getTime();
        long totalTimePlayedMilli = logoutTime - loginTime;
        return TimeUnit.MILLISECONDS.toSeconds(totalTimePlayedMilli);
    }

    public static double getPlayerBalance(Player player){
        OfflinePlayer offlinePlayer = Main.getPlayerFromOfflinePlayer(player);
        Economy econ = Main.getEconomy();
        return econ.getBalance(offlinePlayer);
    }

    public static void setPlayerBalanceInConfigOnLogin(Player player) throws IOException {
        bankCfg.set(Constants.YML_PLAYERS + player.getName() + ".loginBalance", getPlayerBalance(player));
        bankCfg.save(Main.bankFile);
    }

    public static void setPlayerBalanceWithInterest(Player player){
        double moneyForTimePlayed = (double) getSecondsPlayedInSession(player.getName()) * (double) 5;
        Main.getEconomy().depositPlayer(Main.getPlayerFromOfflinePlayer(player), moneyForTimePlayed);
    }
}
