package discord.PlayerShops;

import discord.Api;
import discord.Constants;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerShopsTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> playerShopCommandList = new ArrayList<>();
        FileConfiguration playerShopCfg = Api.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
        playerShopCommandList.add(Constants.MY_SHOP_COMMAND);
        playerShopCommandList.add(Constants.SET_WORTH_COMMAND);
        playerShopCommandList.add(Constants.OPEN_COMMAND);
        List<String> tabList = new ArrayList<>();
        String commandText = "";
        if (!args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)){
            tabList = playerShopCommandList;
            commandText = args[0];
        } else {
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
                if (playerShopCfg.isSet(olp.getName() + Constants.YML_SIZE)){
                    tabList.add(olp.getName());
                }
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e){
                System.out.println("Incorrect player shop command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
