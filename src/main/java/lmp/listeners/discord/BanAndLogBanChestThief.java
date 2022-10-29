package lmp.listeners.discord;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class BanAndLogBanChestThief implements Listener {

    public BanAndLogBanChestThief(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}


    @EventHandler
    public static void banPlayerStealing(InventoryClickEvent event) {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("banPlayerFromTakingFromBanChest")) {
            String playerName = event.getWhoClicked().getName();
            FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
            Location chestLocation = new Location(event.getWhoClicked().getWorld(), configCfg.getDouble("banChest.x"), configCfg.getDouble("banChest.y"), configCfg.getDouble("banChest.z"));
            String chestMaterial = "";
            try {
                JDA jda = LatchDiscord.getJDA();
                chestMaterial = Objects.requireNonNull(event.getClickedInventory()).getType().toString();
                if (chestMaterial.equalsIgnoreCase("CHEST") && chestLocation.equals(event.getClickedInventory().getLocation())) {
                    if (event.getCurrentItem() != null && !event.getWhoClicked().hasPermission("group.mod")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban " + playerName + " 3d stole from bigboi's chest");
                        NewsChannel staffChatChannel = LatchDiscord.getJDA().getNewsChannelById(lmp.Constants.BAN_CHEST_LOG_CHANNEL_ID);
                        assert staffChatChannel != null;
                        NewsChannel banLogChannel = LatchDiscord.getJDA().getNewsChannelById(lmp.Constants.BAN_LOG_CHANNEL_ID);
                        assert banLogChannel != null;
                        if (event.getCurrentItem().getType().equals(Material.BEACON)) {
                            staffChatChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> GOT BEACON'd and will be temporarily banned for 3 days. :)- Their MC username is: " + playerName).queue();
                            banLogChannel.sendMessage("Minecraft Username: " + playerName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> | Reason: They got BEACON'D ").queue();
                        } else {
                            staffChatChannel.sendMessage("<@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> will be temporarily banned for 3 days. Reason: Stealing from BigBoi's Chest. They tried to steal " + event.getCurrentItem().getAmount() + " " + event.getCurrentItem().getType() + " :)- Their MC username is: " + playerName).queue();
                            banLogChannel.sendMessage("Minecraft Username: " + playerName + " | Discord Username: <@" + Api.getDiscordIdFromMCid(event.getWhoClicked().getUniqueId().toString()) + "> | Reason: Stealing from spawn chest | Item(s) stolen: " + event.getCurrentItem().getAmount() + " " + event.getCurrentItem().getType()).queue();
                        }
                    }
                }
            } catch (NullPointerException ignored) {

            }
        }

    }
}
