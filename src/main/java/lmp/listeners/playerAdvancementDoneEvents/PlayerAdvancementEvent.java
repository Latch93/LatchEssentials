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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
            broadcastAdvancementInDiscordAndMinecraft(e);
        }
    }

    public static void broadcastAdvancementInDiscordAndMinecraft(PlayerAdvancementDoneEvent e) {
        if (Boolean.FALSE.equals(Api.isPlayerInvisible(e.getPlayer().getUniqueId().toString()))) {
            org.bukkit.advancement.Advancement adv = e.getAdvancement();
            String advancementMessage = "";
            TextChannel minecraftChatChannel = LatchDiscord.getJDA().getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
            EmbedBuilder eb = new EmbedBuilder();
            String worldPrefix = Api.getPlayerChatWorldPrefix(e.getPlayer().getWorld().getName());
            int totalCount = 0;
            int completedCount = 0;
            for (Iterator<org.bukkit.advancement.Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
                org.bukkit.advancement.Advancement a = it.next();
                if (a.getDisplay() != null && Boolean.TRUE.equals(Objects.requireNonNull(a.getDisplay()).shouldAnnounceChat())) {
                    totalCount++;
                    if (e.getPlayer().getAdvancementProgress(a).getAwardedCriteria().toArray().length != 0) {
                        completedCount++;
                    }
                }
            }
            if (adv.getDisplay() != null && Boolean.TRUE.equals(Objects.requireNonNull(adv.getDisplay()).shouldAnnounceChat())) {
                eb.setTitle(worldPrefix + adv.getDisplay().getTitle() + "!");
                eb.setColor(new Color(0xE1F504B9, true));
                eb.setThumbnail("https://minotar.net/avatar/" + e.getPlayer().getName() + ".png?size=5");
                advancementMessage = e.getPlayer().getName() + "\n" + adv.getDisplay().getDescription() + "\nCompleted " + completedCount + "/" + totalCount;
                eb.setDescription(advancementMessage);
                assert minecraftChatChannel != null;
                minecraftChatChannel.sendMessageEmbeds(eb.build()).queue();
                broadcastAdvancementOnLMP(e.getPlayer().getName(), adv.getDisplay().getTitle(), adv.getDisplay().getDescription(), e.getPlayer().getWorld().getName());
            }
        }
    }

    public static void broadcastAdvancementOnLMP(String playerName,  String advancementName, String advancementDescription, String playerWorld) {
        net.md_5.bungee.api.chat.TextComponent aName;
        HoverEvent he;
        String worldPrefix = Api.getPlayerChatWorldPrefix(playerWorld);
        net.md_5.bungee.api.chat.TextComponent pName = new net.md_5.bungee.api.chat.TextComponent(ChatColor.GOLD + worldPrefix + ChatColor.DARK_AQUA + playerName + ChatColor.WHITE + " has made the advancement ");
        aName = new TextComponent(ChatColor.GREEN + "[" + advancementName + "]");
        he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + advancementDescription));
        aName.setHoverEvent(he);
        aName.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(pName, aName);
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
