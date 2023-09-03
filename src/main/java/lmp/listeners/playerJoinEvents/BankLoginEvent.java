package lmp.listeners.playerJoinEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class BankLoginEvent implements Listener {

    public BankLoginEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void bankLoginEvent(PlayerJoinEvent e) throws IOException {
        setPlayerLoginTime(e.getPlayer());
        Api.setBankSessionToAFK(false, e.getPlayer());
        Api.removePlayerLuckPermPermission(e.getPlayer(), "essentials.afk.kickexempt");
        Api.removePlayerLuckPermPermission(e.getPlayer(), "essentials.sleepingignored");
    }

    public static void showLastSessionMoneyRewardedMessage(FileConfiguration bankCfg, Player player) {
        if (!bankCfg.getBoolean(Constants.YML_PLAYERS + player.getUniqueId() + ".isAFK")) {
            if (bankCfg.isSet(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")) {
                player.sendMessage(Objects.requireNonNull(bankCfg.getString(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")));
            }
        }
    }

    public static void setPlayerLoginTime(Player player) throws IOException {
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        String playerName = player.getName();
        String playerId = player.getUniqueId().toString();
        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();
        if (!bankCfg.isSet(Constants.YML_PLAYERS + playerId)) {
            bankCfg.set(Constants.YML_PLAYERS + playerId + ".playerName", playerName);
        }
        bankCfg.set(Constants.YML_PLAYERS + playerId + ".loginTime", timeMilli);
        bankCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".loginBalance", Api.getPlayerBalance(player));
        if (bankCfg.getInt(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionSeconds") == 0) {
            player.sendMessage(ChatColor.YELLOW + "You haven't logged any time. Please only claim money for time played sparingly.");
        } else if (bankCfg.getBoolean(Constants.YML_PLAYERS + player.getUniqueId() + ".isAFK")) {
            player.sendMessage(ChatColor.GREEN + "You were afk exempt in your last session, so you weren't rewarded money for play time.");
        } else {
            showLastSessionMoneyRewardedMessage(bankCfg, player);
        }
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

}
