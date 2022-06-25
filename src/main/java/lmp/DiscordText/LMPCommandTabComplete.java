package lmp.PlayerShops;

import lmp.Api;
import lmp.Constants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LMPCommandTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> playerShopCommandList = new ArrayList<>();
        playerShopCommandList.add("deposit");
        playerShopCommandList.add("help");
        playerShopCommandList.add("lotto");
        playerShopCommandList.add("stat");
        playerShopCommandList.add("withdraw");
        List<String> tabList = new ArrayList<>();
        String commandText = "";
        if (!args[0].equalsIgnoreCase("lotto") && !args[0].equalsIgnoreCase("withdraw")){
            tabList = playerShopCommandList;
            commandText = args[0];
        } else if (args[0].equalsIgnoreCase("lotto")) {
            tabList.add("buyin");
            tabList.add("check");
            tabList.add("total");
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e){
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Lotto command.");
            }
        }
        else if (args[0].equalsIgnoreCase("withdraw")) {
            tabList.add("[amount]");
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Lotto command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
