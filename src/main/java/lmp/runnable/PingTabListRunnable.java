package lmp.runnable;

import lmp.Main;
import lmp.api.Api;
import lmp.util.PingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PingTabListRunnable extends BukkitRunnable {
    private final Main plugin;

    public PingTabListRunnable(Main plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//            [" + ChatColor.GOLD + Api.getPlayerTime(player.getUniqueId().toString()) + ChatColor.WHITE + "] -
            if (Api.getPlayerTime(player.getUniqueId().toString()).isEmpty()) {
                player.setPlayerListName(player.getDisplayName() + ChatColor.WHITE + " - [" + ChatColor.AQUA + PingUtil.getPing(player) + "ms" + ChatColor.WHITE + "]");
            } else {
                player.setPlayerListName(player.getDisplayName() + ChatColor.WHITE + " - [" + ChatColor.GOLD + Api.getPlayerTime(player.getUniqueId().toString()) + ChatColor.WHITE + "] - [" + ChatColor.AQUA + PingUtil.getPing(player) + "ms" + ChatColor.WHITE + "]");

            }
        }
    }
}