package lmp.listeners.playerAdvancementDoneEvents;

import lmp.*;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public final class PlayerAdvancementEvent implements Listener {
    public PlayerAdvancementEvent(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static List<Advancement> advancementList = Advancements.getAdvancements();

    @EventHandler
    public void updateAndBroadcastPlayerAdvancement(PlayerAdvancementDoneEvent e) throws IOException {
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        setPlayerAdvancementOnCompletion(e);
        if (enabledEventsCfg.getBoolean("enableAdvancementBroadcast")) {
            broadcastAdvancementInDiscord(e);
            broadcastAdvancementOnLMP(e);
        }
    }

    public static void broadcastAdvancementInDiscord(PlayerAdvancementDoneEvent e) {
        String advancement = e.getAdvancement().getKey().toString();
        String advancementMessage = "";
        TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
        EmbedBuilder eb = new EmbedBuilder();
        String worldPrefix = "[LMP] - ";
        if (e.getPlayer().getWorld().getName().contains("hardcore")) {
            worldPrefix = "[Hardcore] - ";
        }
        if (e.getPlayer().getWorld().getName().contains("anarchy")) {
            worldPrefix = "[Anarchy] - ";
        }
        int playerAchievementCount = Api.loadConfig(YmlFileNames.YML_ADVANCEMENT_FILE_NAME).getInt(Constants.YML_PLAYERS + e.getPlayer().getName() + ".advancementCount");
        for (Advancement advance : advancementList) {
            if (advancement.equalsIgnoreCase(advance.getID()) && Boolean.FALSE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))) {
                eb.setTitle(worldPrefix + advance.getName() + "!");
                eb.setColor(new Color(0xE1F504B9, true));
                eb.setThumbnail("https://minotar.net/avatar/" + e.getPlayer().getName() + ".png?size=5");
                advancementMessage = e.getPlayer().getName() + "\n" + advance.getCriteria() + "\nCompleted " + playerAchievementCount + "/" + Advancements.getAdvancements().size();
                eb.setDescription(advancementMessage);
                assert minecraftChatChannel != null;
                minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
            }
        }
    }

    public static void broadcastAdvancementOnLMP(PlayerAdvancementDoneEvent e) {
        String advancement = e.getAdvancement().getKey().toString();
        net.md_5.bungee.api.chat.TextComponent advancementName;
        HoverEvent he;
        String worldPrefix = "[LMP] - ";
        if (e.getPlayer().getWorld().getName().contains("hardcore")) {
            worldPrefix = "[Hardcore] - ";
        }
        if (e.getPlayer().getWorld().getName().contains("anarchy")) {
            worldPrefix = "[Anarchy] - ";
        }

        net.md_5.bungee.api.chat.TextComponent playerName = new net.md_5.bungee.api.chat.TextComponent(ChatColor.GOLD + worldPrefix + ChatColor.DARK_AQUA + e.getPlayer().getName() + ChatColor.WHITE + " has made the advancement ");
        for (Advancement advance : advancementList) {
            if (advancement.equalsIgnoreCase(advance.getID())) {
                advancementName = new TextComponent(ChatColor.GREEN + "[" + advance.getName() + "]");
                he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + advance.getCriteria()));
                advancementName.setHoverEvent(he);
                advancementName.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(playerName, advancementName);
                }
            }
        }

    }

    public static void setPlayerAdvancementOnCompletion(PlayerAdvancementDoneEvent e) throws IOException {
        if (isValidAdvancement(e)) {
            for (Advancement advancement : advancementList) {
                if (e.getAdvancement().getKey().toString().equalsIgnoreCase(advancement.getID())) {
                    String playerName = e.getPlayer().getName();
                    FileConfiguration advancementCfg = Api.loadConfig(YmlFileNames.YML_ADVANCEMENT_FILE_NAME);
                    advancementCfg.set(Constants.YML_PLAYERS + playerName + ".advancementCount", advancementCfg.getInt(Constants.YML_PLAYERS + playerName + ".advancementCount") + 1);
                    advancementCfg.save(Api.getConfigFile(YmlFileNames.YML_ADVANCEMENT_FILE_NAME));
                }
            }
        }
    }

    public static boolean isValidAdvancement(PlayerAdvancementDoneEvent e) {
        boolean isValid = false;
        for (Advancement advancement : advancementList) {
            if (advancement.getID().equalsIgnoreCase(e.getAdvancement().getKey().toString())) {
                isValid = true;
            }
        }
        return isValid;
    }

}
