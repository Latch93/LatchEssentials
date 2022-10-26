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

public class DiscordStaffChatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                Player player = (Player) sender;
                if (args[0] != null) {
                    if (player.hasPermission("group.jr-mod")) {
                        String playerName = player.getName();
                        TextChannel discordStaffChannel = LatchDiscord.jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                        StringBuilder messageString = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            if (i != args.length - 1) {
                                messageString.append(args[i]).append(" ");
                            } else {
                                messageString.append(args[i]);
                            }
                        }
                        assert discordStaffChannel != null;
                        String convertedMessage = Api.convertMinecraftMessageToDiscord(playerName, String.valueOf(messageString));
                        String[] messageArr = convertedMessage.split(" » ");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("group.jr-mod")) {
                                p.sendMessage("[" + ChatColor.LIGHT_PURPLE + "Mod Chat" + ChatColor.WHITE + "] - " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE + " » " + ChatColor.AQUA + messageArr[1]);
                            }
                        }
                        discordStaffChannel.sendMessage("[Mod Chat] - " + convertedMessage).queue();
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
            }
        }
        return false;
    }
}
