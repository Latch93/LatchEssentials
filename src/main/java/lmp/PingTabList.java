package lmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PingTabList extends BukkitRunnable {
    private final Main plugin;
    public PingTabList(Main plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setPlayerListName(player.getDisplayName()+ ChatColor.WHITE + " - [" + ChatColor.GOLD + Api.getPlayerTime(player.getUniqueId().toString()) + ChatColor.WHITE +"] - [" + ChatColor.AQUA + PingUtil.getPing(player) + "ms" + ChatColor.WHITE + "]");
        }
    }
}