package discord.DiscordText;

import discord.LatchDiscord;
import discord.Lottery;
import discord.RandomTeleport;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DiscordTextCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();
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
//        else if (args[0].equalsIgnoreCase("test")){
//            Horse horse = (Horse) player.getVehicle();
//            assert horse != null;
//            player.sendMessage("Horse Speed1: " + horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue());
//            player.sendMessage("Horse Speed2: " + horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
//            player.sendMessage("Horse Speed3: " + horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue());
//            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.9);
//            player.sendMessage("Horse health1: " + horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
//            player.sendMessage("Horse health2: " + horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
//            player.sendMessage("Horse health3: " + horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
//            horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
//            player.sendMessage("Horse player1: " + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
//            player.sendMessage("Horse player2: " + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
//            player.sendMessage("Horse player3: " + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
//            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
//        }
//        player.getAttribute(Attribute.)
        return false;
    }
}
