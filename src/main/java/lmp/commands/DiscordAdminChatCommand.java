package lmp.commands;

import lmp.Constants;
import lmp.LatchDiscord;
import lmp.api.Api;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordAdminChatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                Player player = (Player) sender;
                if (args[0] != null){
                    if (player.hasPermission("group.admin")){
                        String playerName = player.getName();
                        TextChannel adminChannel = LatchDiscord.jda.getTextChannelById(Constants.ADMIN_CHANNEL_ID);
                        StringBuilder messageString = new StringBuilder();
                        for (int i = 0; i < args.length; i++){
                            if (i != args.length-1){
                                messageString.append(args[i]).append(" ");
                            } else {
                                messageString.append(args[i]);
                            }
                        }
                        assert adminChannel != null;
                        String convertedMessage = Api.convertMinecraftMessageToDiscord(playerName, String.valueOf(messageString));
                        String[] messageArr = convertedMessage.split(" » ");
                        for (Player p : Bukkit.getOnlinePlayers()){
                            if (p.hasPermission("group.admin")){
                                p.sendMessage("[" + ChatColor.DARK_PURPLE + "Admin Chat" + ChatColor.WHITE + "] - " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE + " » " + ChatColor.AQUA + messageArr[1]);
                            }
                        }
                        adminChannel.sendMessage("[Admin Chat] - " + convertedMessage).queue();
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
            }
        }
        return false;
    }
}