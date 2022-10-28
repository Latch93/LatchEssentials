package lmp.listeners.playerJoinEvents;

import lmp.Main;
import lmp.api.Api;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ExecutionException;

public class SendHowToLinkMessageEvent implements Listener {

    public SendHowToLinkMessageEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void checkPlayerMemberStatus(PlayerJoinEvent e) throws ExecutionException, InterruptedException {
        Player player = e.getPlayer();
        if (Api.doesPlayerHavePermission(player.getUniqueId().toString(), "default")) {
            player.sendMessage(ChatColor.RED + "You need to link your Discord and Minecraft accounts.\n" +
                    "Go to Discord and type the following into the General Channel -> " + ChatColor.AQUA + "!link\n" +
                    ChatColor.RED + "Then copy and paste the command into Minecraft chat and click enter.");
        }
    }
}
