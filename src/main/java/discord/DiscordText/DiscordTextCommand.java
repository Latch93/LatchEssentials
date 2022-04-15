package discord.DiscordText;

import discord.Advancements;
import discord.LatchDiscord;
import discord.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;

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
        } else if (args[0].equalsIgnoreCase("stat")){
            for(OfflinePlayer player : Bukkit.getWhitelistedPlayers()){
                if (Objects.equals(player.getName(), sender.getName())){
                    System.out.println("ads: "+ player.getStatistic(Statistic.DEATHS));
                    System.out.println("ads2: "+ player.getStatistic(Statistic.RECORD_PLAYED));
                    System.out.println("ads3: "+ player.getStatistic(Statistic.MOB_KILLS));
                }
            }
        }
        return false;
    }
}
