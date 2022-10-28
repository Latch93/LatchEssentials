package lmp.listeners.playerQuitEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.commands.LatchTwitchBotCommand;
import lmp.runnable.LatchTwitchBotRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;
import java.util.List;

public class StopTwitchBotOnPlayerLogoutEvent implements Listener {

    public StopTwitchBotOnPlayerLogoutEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    private final List<LatchTwitchBotRunnable> twitchBotList = LatchTwitchBotCommand.twitchBotList;

    @EventHandler
    private void stopTwitchBot(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        while (iter.hasNext()) {
            LatchTwitchBotRunnable runBot = iter.next();
            if (runBot.getMinecraftName().equalsIgnoreCase(player.getName())) {
                runBot.getTwitchClient().close();
                player.sendMessage(ChatColor.GREEN + "Your TwitchBot has been " + ChatColor.RED + "terminated.");
                Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.RED + "TwitchBot.");
                iter.remove();
            }
        }
    }
}
