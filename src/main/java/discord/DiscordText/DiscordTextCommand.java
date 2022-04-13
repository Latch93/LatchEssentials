package discord.DiscordText;

import discord.Advancements;
import discord.LatchDiscord;
import discord.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class DiscordTextCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("purgeWhitelist")){
            LatchDiscord.purge();
        } else if (args[0].equalsIgnoreCase("setdiscord")){
            try {
                LatchDiscord.setDiscordUserNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
