package discord;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordStaffChatCommand {public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
        try {
            Player player = (Player) sender;
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
            System.out.println(String.valueOf(messageString));
            discordStaffChannel.sendMessage(Main.convertMinecraftMessageToDiscord(playerName, String.valueOf(messageString))).queue();
        } catch (IllegalArgumentException ignored) {
        }
    }
    return false;
}
}
