package lmp.listeners.playerDeathEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.DecimalFormat;

public class WithdrawPlayerMoneyOnDeathEvent implements Listener {

    public WithdrawPlayerMoneyOnDeathEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void withdrawMoneyFromPlayerOnDeath(PlayerDeathEvent e){
        if (e.getEntity().getKiller() == null && Boolean.TRUE.equals(Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getBoolean("doesPlayerLoseMoneyOnDeath"))) {
            DecimalFormat df = new DecimalFormat("0.00");
            double playerBalance = Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(e.getEntity()));
            double percentToRemove = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("deathBalancePercentage");
            double amountToRemove = (playerBalance / 100.00) * percentToRemove;
            Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(e.getEntity()), amountToRemove);
            e.getEntity().sendMessage(ChatColor.YELLOW + "You lost " + ChatColor.RED + "$" + df.format(amountToRemove) + ChatColor.YELLOW + " because you died.");
        }

    }
}
