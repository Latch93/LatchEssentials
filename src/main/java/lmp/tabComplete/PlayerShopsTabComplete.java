package lmp.tabComplete;

import lmp.api.Api;
import lmp.constants.ServerCommands;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerShopsTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> playerShopCommandList = new ArrayList<>();
        FileConfiguration playerShopCfg = Api.loadConfig(YmlFileNames.YML_PLAYER_SHOP_FILE_NAME);
        playerShopCommandList.add(ServerCommands.MY_SHOP_COMMAND);
        playerShopCommandList.add(ServerCommands.SET_WORTH_COMMAND);
        playerShopCommandList.add(ServerCommands.OPEN_COMMAND);
        List<String> tabList = new ArrayList<>();
        String commandText = "";
        if (!args[0].equalsIgnoreCase(ServerCommands.OPEN_COMMAND)) {
            tabList = playerShopCommandList;
            commandText = args[0];
        } else {
            for (String minecraftId : playerShopCfg.getKeys(false)) {
                if (playerShopCfg.isSet(minecraftId + ".slots") && playerShopCfg.getInt(minecraftId + ".slots") != 0) {
                    tabList.add(Bukkit.getOfflinePlayer(UUID.fromString(minecraftId)).getName());
                }
            }
            try {
                return StringUtil.copyPartialMatches(args[1], tabList, new ArrayList<>());
            } catch (IndexOutOfBoundsException e) {
                Api.messageInConsole(ChatColor.RED + "Incorrect player shop command.");
            }
        }
        return StringUtil.copyPartialMatches(commandText, tabList, new ArrayList<>());
    }
}
