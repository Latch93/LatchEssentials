package discord;

import net.dv8tion.jda.api.entities.TextChannel;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordStaffChatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                Player player = (Player) sender;
                if (player.hasPermission("group.mod")){
                    String playerName = player.getName();
                    TextChannel discordStaffChannel = LatchDiscord.jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                    StringBuilder messageString = new StringBuilder();
                    for (int i = 0; i < args.length; i++){
                        if (i != args.length-1){
                            messageString.append(args[i]).append(" ");
                        } else {
                            messageString.append(args[i]);
                        }
                    }
                    assert discordStaffChannel != null;
                    String convertedMessage = Api.convertMinecraftMessageToDiscord(playerName, String.valueOf(messageString));
                    String[] messageArr = convertedMessage.split(" » ");
                    for (Player p : Bukkit.getOnlinePlayers()){
                        if (p.hasPermission("group.mod")){
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "DTSC" + ChatColor.WHITE + " - " + ChatColor.GOLD + playerName + ChatColor.WHITE + " » " + ChatColor.AQUA + messageArr[1]);
                        }
                    }
                    discordStaffChannel.sendMessage("[Mod-Chat] - " + convertedMessage).queue();
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    return false;
    }
}
