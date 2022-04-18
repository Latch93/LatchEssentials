package discord.DiscordText;

import discord.Constants;
import discord.LatchDiscord;
import discord.Main;
import discord.RandomTeleport;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class DiscordTextCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("purgeWhitelist")) {
            LatchDiscord.purge();
        } else if (args[0].equalsIgnoreCase("setdiscord")) {
            try {
                LatchDiscord.setDiscordUserNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("stat")) {
            for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
                if (Objects.equals(player.getName(), sender.getName())) {
                    sender.sendMessage(ChatColor.GREEN + "Total deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
                    sender.sendMessage(ChatColor.GREEN + "Total mobs killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
                    sender.sendMessage(ChatColor.GREEN + "Number of times jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
                }
            }
        }
        else if (args[0].equalsIgnoreCase("rtp")){
            RandomTeleport.randomTp((Player) sender);
        }
        return false;
    }
}
