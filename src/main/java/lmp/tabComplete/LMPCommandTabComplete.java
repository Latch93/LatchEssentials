package lmp.tabComplete;

import lmp.api.Api;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LMPCommandTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> playerShopCommandList = new ArrayList<>();
        playerShopCommandList.add("claim");
        playerShopCommandList.add("deposit");

        if (sender instanceof Player){
            Player commandSender = (Player) sender;
            try {
                if (Api.doesPlayerHavePermission(commandSender.getUniqueId().toString(), "hardcore")){
                    playerShopCommandList.add("hardcore");
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        playerShopCommandList.add("help");
        playerShopCommandList.add("lotto");
        playerShopCommandList.add("rtp");
        playerShopCommandList.add("stat");
        playerShopCommandList.add("withdraw");
        playerShopCommandList.add("xp");
        playerShopCommandList.add("xpDeposit");
        playerShopCommandList.add("xpWithdraw");
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
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Withdraw command.");
            }
        }
        else if (args[0].equalsIgnoreCase("claim")) {
            for (Material material : Material.values()){
                tabList.add(material.toString());
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect LMP Claim command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
