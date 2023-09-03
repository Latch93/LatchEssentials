package lmp.tabComplete;

import lmp.api.Api;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LMPCommandTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> lmpCommandTabList = new ArrayList<>();
        lmpCommandTabList.add("addBirthday");
        lmpCommandTabList.add("advbook");
        lmpCommandTabList.add("anarchy");
        lmpCommandTabList.add("biome");
        lmpCommandTabList.add("claim");
        lmpCommandTabList.add("classic");
        lmpCommandTabList.add("combine");
        lmpCommandTabList.add("creative");
        lmpCommandTabList.add("deposit");
        lmpCommandTabList.add("fly");
        lmpCommandTabList.add("gift");
        lmpCommandTabList.add("help");
        lmpCommandTabList.add("hub");
        lmpCommandTabList.add("lotto");
        lmpCommandTabList.add("money");
        lmpCommandTabList.add("oneblock");
        lmpCommandTabList.add("online");
//        lmpCommandTabList.add("rtp");
        lmpCommandTabList.add("skyblock");
        lmpCommandTabList.add("shop");
        lmpCommandTabList.add("stat");
        lmpCommandTabList.add("withdraw");
        lmpCommandTabList.add("xp");
        lmpCommandTabList.add("xpDeposit");
        lmpCommandTabList.add("xpWithdraw");
        List<String> tabList = new ArrayList<>();
        String commandText = "";
        if (!args[0].equalsIgnoreCase("lotto") && !args[0].equalsIgnoreCase("withdraw") && !args[0].equalsIgnoreCase("gift")) {
            tabList = lmpCommandTabList;
            commandText = args[0];
        } else if (args[0].equalsIgnoreCase("lotto")) {
            tabList.add("buyin");
            tabList.add("check");
            tabList.add("total");
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Lotto command.");
            }
        } else if (args[0].equalsIgnoreCase("withdraw")) {
            tabList.add("[amount]");
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Withdraw command.");
            }
        } else if (args[0].equalsIgnoreCase("claim")) {
            for (Material material : Material.values()) {
                tabList.add(material.toString());
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Claim command.");
            }
        } else if (args[0].equalsIgnoreCase("gift")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerWorld = player.getWorld().getName();
                List<String> enabledGiftWorlds = new ArrayList<>();
                enabledGiftWorlds.add("world");
                enabledGiftWorlds.add("world_nether");
                enabledGiftWorlds.add("world_the_end");
                if (enabledGiftWorlds.contains(playerWorld)){
                    tabList.add(player.getName());
                }
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Gift command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
