package lmp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MessageDiscordUserFromServerCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                if (args[0] != null && args[1] != null){
                    StringBuilder messageString = new StringBuilder();
                    for (int i = 1; i <= args.length - 1 ; i++){
                        if (i != (args.length - 1) ){
                            messageString.append(args[i]).append(" ");
                        } else {
                            messageString.append(args[i]);
                        }
                    }
                    String offlineDiscordUserID = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME)).getString(Constants.YML_PLAYERS + Api.getMinecraftIdFromMinecraftName(args[0]) + ".discordId");
                    String senderDiscordName = Api.getDiscordNameFromMCid(player.getUniqueId().toString());
                    if (offlineDiscordUserID != null || !offlineDiscordUserID.isEmpty()){
                        Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(offlineDiscordUserID)).getUser().openPrivateChannel().flatMap(dm -> dm.sendMessage("LMP Mail from [Minecraft Name: " + player.getName() +" --- Discord Name: " + senderDiscordName + "]\n" +
                                messageString)).queue();
                    } else {

                        player.sendMessage(ChatColor.RED + "Maybe that player doesn't exist. Please try again using this format -> " + ChatColor.AQUA + "/dcmsg [playerMinecraftName] [message]");
                    }

                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
                player.sendMessage(ChatColor.RED + "Maybe that player doesn't exist. Please try again using this format -> " + ChatColor.AQUA + "/dcmsg [playerMinecraftName] [message]");
            }
        }
        return false;
    }
}