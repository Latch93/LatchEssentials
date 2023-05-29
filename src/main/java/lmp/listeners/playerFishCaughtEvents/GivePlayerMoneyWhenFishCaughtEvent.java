package lmp.listeners.playerFishCaughtEvents;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GivePlayerMoneyWhenFishCaughtEvent implements Listener {

    public GivePlayerMoneyWhenFishCaughtEvent(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void givePlayerMoneyWhenFishCaughtEvent(PlayerFishEvent e){
        List<String> enabledFishMoneyWorlds = new ArrayList<>();
        enabledFishMoneyWorlds.add("world");
        enabledFishMoneyWorlds.add("world_nether");
        enabledFishMoneyWorlds.add("world_the_end");
        if (enabledFishMoneyWorlds.contains(e.getPlayer().getWorld().getName())) {
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);

            double rewardMoney = configCfg.getDouble("fishRewardMoney");
            Player player = e.getPlayer();
            ArrayList<Material> itemCaughtWhitelist = new ArrayList<>();
            itemCaughtWhitelist.add(Material.COD);
            itemCaughtWhitelist.add(Material.SALMON);
            itemCaughtWhitelist.add(Material.TROPICAL_FISH);
            itemCaughtWhitelist.add(Material.PUFFERFISH);
            itemCaughtWhitelist.add(Material.BOW);
            itemCaughtWhitelist.add(Material.ENCHANTED_BOOK);
            itemCaughtWhitelist.add(Material.FISHING_ROD);
            itemCaughtWhitelist.add(Material.NAME_TAG);
            itemCaughtWhitelist.add(Material.NAUTILUS_SHELL);
            itemCaughtWhitelist.add(Material.SADDLE);
            itemCaughtWhitelist.add(Material.LILY_PAD);
            itemCaughtWhitelist.add(Material.BOWL);
            itemCaughtWhitelist.add(Material.LEATHER);
            itemCaughtWhitelist.add(Material.ROTTEN_FLESH);
            itemCaughtWhitelist.add(Material.STICK);
            itemCaughtWhitelist.add(Material.STRING);
            itemCaughtWhitelist.add(Material.POTION);
            itemCaughtWhitelist.add(Material.BONE);
            itemCaughtWhitelist.add(Material.INK_SAC);
            itemCaughtWhitelist.add(Material.TRIPWIRE_HOOK);
            DecimalFormat df = new DecimalFormat("0.00");
            if (e.getCaught() != null && e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
                Api.givePlayerMoney(player.getUniqueId().toString(), rewardMoney);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(player))));
            }
        }
    }

}
