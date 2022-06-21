package lmp.AutoMiner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoMinerTabComplete implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> backpackCommandList = new ArrayList<>();
        backpackCommandList.add("setXDistance");
        backpackCommandList.add("setYDistance");
        backpackCommandList.add("setZDistance");
        return StringUtil.copyPartialMatches(args[0], backpackCommandList, new ArrayList<>());
    }
}
