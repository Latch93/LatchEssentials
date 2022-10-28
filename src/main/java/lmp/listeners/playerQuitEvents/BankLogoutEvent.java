package lmp.listeners.playerQuitEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BankLogoutEvent implements Listener {
    public BankLogoutEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void bankLoginEvent(PlayerQuitEvent e) throws IOException {
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        Player player = e.getPlayer();
        String playerName = e.getPlayer().getName();
        String playerId = e.getPlayer().getUniqueId().toString();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerId)) {
            bankCfg.set(Constants.YML_PLAYERS + playerId + ".playerName", playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".logoutTime", timeMilli);
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".lastSessionSeconds", TimeUnit.MILLISECONDS.toSeconds(Api.getSecondsPlayedInSession(playerId)));
        setPlayerBalanceWithInterest(bankCfg, player);
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

    public static void setPlayerBalanceWithInterest(FileConfiguration bankCfg, Player player) {
        FileConfiguration mainCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        double moneyPlayedMultiplier = mainCfg.getDouble("bankMultiplier");
        double moneyForTimePlayed = (double) Api.getSecondsPlayedInSession(player.getUniqueId().toString()) * moneyPlayedMultiplier;
        double timePlayedMinutes = Api.getSecondsPlayedInSession(player.getUniqueId().toString()) / 60.0;
        DecimalFormat df = new DecimalFormat("0.00");
        if (moneyForTimePlayed != 0) {
            bankCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward", "You played " + df.format(timePlayedMinutes) + " minutes and earned " + "$" + df.format(moneyForTimePlayed));
            Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), moneyForTimePlayed);
        }
    }

}
