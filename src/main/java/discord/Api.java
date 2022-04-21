package discord;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Api {
    private static Economy econ = null;
    // Formats chat message in Minecraft to send to Discord channel
    public static String convertMinecraftMessageToDiscord(String senderName, String senderMessage) {
        if (!senderMessage.toLowerCase().contains("@everyone") && !senderMessage.toLowerCase().contains("@here")){
            ArrayList<String> colorCodes = new ArrayList<>();
            colorCodes.add("&a");
            colorCodes.add("&b");
            colorCodes.add("&c");
            colorCodes.add("&d");
            colorCodes.add("&e");
            colorCodes.add("&f");
            colorCodes.add("&0");
            colorCodes.add("&1");
            colorCodes.add("&2");
            colorCodes.add("&3");
            colorCodes.add("&4");
            colorCodes.add("&5");
            colorCodes.add("&6");
            colorCodes.add("&7");
            colorCodes.add("&8");
            colorCodes.add("&9");
            colorCodes.add("&k");
            colorCodes.add("&l");
            colorCodes.add("&m");
            colorCodes.add("&n");
            colorCodes.add("&o");
            colorCodes.add("&r");
            colorCodes.add("§a");
            colorCodes.add("§b");
            colorCodes.add("§c");
            colorCodes.add("§d");
            colorCodes.add("§e");
            colorCodes.add("§f");
            colorCodes.add("§0");
            colorCodes.add("§1");
            colorCodes.add("§2");
            colorCodes.add("§3");
            colorCodes.add("§4");
            colorCodes.add("§5");
            colorCodes.add("§6");
            colorCodes.add("§7");
            colorCodes.add("§8");
            colorCodes.add("§9");
            colorCodes.add("§k");
            colorCodes.add("§l");
            colorCodes.add("§m");
            colorCodes.add("§n");
            colorCodes.add("§o");
            colorCodes.add("§r");
            for (String colorCode : colorCodes){
                senderName = senderName.replace(colorCode, "");
                senderMessage = senderMessage.replace(colorCode, "");
            }
        }
        return senderName + " » " + senderMessage;
    }

    public static OfflinePlayer getPlayerFromOfflinePlayer(Player player){
        OfflinePlayer offlinePlayer = null;
        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
            if (olp.getName().equalsIgnoreCase(player.getName())) {
                offlinePlayer = olp;
            }
        }
        return offlinePlayer;
    }

    // Economy Setup
    public static void setupEconomy(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        setEconomy(econ);
    }

    public static Economy getEconomy(){
        return econ;
    }

    public static void setEconomy(Economy economy) {
        econ = economy;
    }

    public static File getConfigFile(String fileName){
        return new File(getMainPlugin().getDataFolder(), fileName + ".yml");
    }

    public static FileConfiguration getFileConfiguration(File file){
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration loadConfig(String fileName) {
        return YamlConfiguration.loadConfiguration(getConfigFile(fileName));
    }

    public static Main getMainPlugin(){
        return getPlugin(Main.class);
    }

    public static void cancelEventsInPreviousSeason(String worldName, String player, BlockBreakEvent blockBreakEvent, BlockPlaceEvent blockPlaceEvent, InventoryClickEvent inventoryClickEvent, PlayerPortalEvent playerPortalEvent){
        if (worldName.equalsIgnoreCase("season1")) {
            if (player == null || !player.equalsIgnoreCase("Latch93")){
                if (blockBreakEvent != null) {
                    blockBreakEvent.setCancelled(true);
                } else if (blockPlaceEvent != null) {
                    blockPlaceEvent.setCancelled(true);
                } else if (inventoryClickEvent != null) {
                    inventoryClickEvent.setCancelled(true);
                } else if (playerPortalEvent != null) {
                    playerPortalEvent.setCancelled(true);
                }
            }
        }
    }

    public static boolean isPlayerInvisible(String uuid){
        File superVanishFile = new File("plugins/SuperVanish", "data.yml");
        return YamlConfiguration.loadConfiguration(superVanishFile).getStringList("InvisiblePlayers").contains(uuid);
    }
}
