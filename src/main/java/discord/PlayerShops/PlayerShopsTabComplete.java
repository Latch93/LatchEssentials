package discord.PlayerShops;

import discord.Constants;
import discord.Main;

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
        FileConfiguration playerShopCfg = Main.getFileConfiguration(Main.playerShopFile);
        playerShopCommandList.add(Constants.MY_SHOP_COMMAND);
        playerShopCommandList.add(Constants.SET_WORTH_COMMAND);
        playerShopCommandList.add(Constants.OPEN_COMMAND);
        List<String> tabList = new ArrayList<>();
        String commandText = "";
        if (!args[0].equalsIgnoreCase(Constants.OPEN_COMMAND)){
            tabList = playerShopCommandList;
            commandText = args[0];
        } else {

            for (String player : playerShopCfg.getConfigurationSection("players").getKeys(false)){
                tabList.add(player);
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException ignored){

            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
