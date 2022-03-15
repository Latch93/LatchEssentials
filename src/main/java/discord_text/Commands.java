package discord_text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Objects;

public class Commands implements Listener, CommandExecutor {

    public String cmd1 = "hide";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
             for(Player online : Bukkit.getOnlinePlayers()){
                 System.out.println("asada");
                    online.hidePlayer(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DiscordText")),player);
                }


        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        return false;
    }

}