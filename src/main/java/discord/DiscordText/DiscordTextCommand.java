package discord.DiscordText;

import discord.*;
import discord.LatchTwitchBot.LatchTwitchBotRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;

public class DiscordTextCommand implements CommandExecutor {
    private BukkitTask task = null;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args[0]!= null){
            if (args[0].equalsIgnoreCase("purgeWhitelist")) {
                LatchDiscord.purge();
            } else if (args[0].equalsIgnoreCase("setdiscord")) {
                try {
                    LatchDiscord.setDiscordUserNames();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("stat")) {
                sender.sendMessage(ChatColor.GREEN + "Total deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
                sender.sendMessage(ChatColor.GREEN + "Total mobs killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
                sender.sendMessage(ChatColor.GREEN + "Number of times jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
            } else if (args[0].equalsIgnoreCase("rtp")){
                RandomTeleport.randomTp(player);
            } else if (args[0].equalsIgnoreCase("lotto")) {
                try {
                    Lottery.lottoCommands(player, args[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lotto check] [/dt lotto buyin] [/dt lotto total]");
                }
            }
        }
        return false;
    }
}
