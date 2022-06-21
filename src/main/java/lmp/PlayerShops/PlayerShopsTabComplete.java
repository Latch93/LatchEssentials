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
            for (String minecraftId : playerShopCfg.getKeys(false)){
                if (playerShopCfg.isSet(minecraftId + ".slots") && playerShopCfg.getString(minecraftId + ".slots") != null){

                    tabList.add(Bukkit.getOfflinePlayer(UUID.fromString(minecraftId)).getName());
                }
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e){
                Api.messageInConsole(ChatColor.RED + "Incorrect player shop command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
