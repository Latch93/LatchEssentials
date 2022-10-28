package lmp.listeners.playerJoinEvents;

import lmp.Constants;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
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
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        Player player = e.getPlayer();
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
        showLastSessionMoneyRewardedMessage(bankCfg, player);
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

    private static void showLastSessionMoneyRewardedMessage(FileConfiguration bankCfg, Player player) {
        if (bankCfg.isSet(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")) {
            player.sendMessage(Objects.requireNonNull(bankCfg.getString(Constants.YML_PLAYERS + player.getUniqueId() + ".lastSessionReward")));
        }
    }

}
